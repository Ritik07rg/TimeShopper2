package com.example.timeshopper.Buyer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.timeshopper.Model.Products;
import com.example.timeshopper.R;
import com.example.timeshopper.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zl.reik.dilatingdotsprogressbar.DilatingDotsProgressBar;

public class SellerProductActivity extends AppCompatActivity {

    private DatabaseReference ProductsRef;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private String sellerID = "";
    DilatingDotsProgressBar progressBar;
    private TextView loadText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_product);

        sellerID = getIntent().getStringExtra("sID");



        recyclerView = findViewById(R.id.recycler_menu1);
        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(layoutManager);

        progressBar = findViewById(R.id.progress);
        loadText = findViewById(R.id.loadText);

      // show progress bar and start animating
        progressBar.showNow();

        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");
    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Products> options =
                new FirebaseRecyclerOptions.Builder<Products>()
                        .setQuery(ProductsRef.orderByChild("sID").equalTo(sellerID), Products.class)
                        .build();

        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter =
                new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull final Products model)
                    {
                        holder.txtProductName.setText(model.getPname());
                        holder.txtProductPrice.setText(model.getPrice());
                        holder.txtProductDescription.setText(model.getDescription());

                        Glide.with(SellerProductActivity.this).load(model.getImage()).into(holder.imageView);


                        // Picasso.get().load(model.getImage()).into(holder.imageView);

                        // stop animation and hide
                        progressBar.hideNow();
                        loadText.setVisibility(View.GONE);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v)
                            {
                                Intent intent = new Intent(SellerProductActivity.this, ProductDetailsActivity.class);
                                intent.putExtra("sID", model.getPid());
                                intent.putExtra("category", model.getCategory());
                                intent.putExtra("sellerName", model.getSellerName());
                                startActivity(intent);
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.particular_seller_item, parent,false);
                        ProductViewHolder holder = new ProductViewHolder(view);
                        return holder;
                    }
                };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}
