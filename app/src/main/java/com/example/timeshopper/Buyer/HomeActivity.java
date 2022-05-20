package com.example.timeshopper.Buyer;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.timeshopper.MainActivity;
import com.example.timeshopper.Model.Sellers;
import com.example.timeshopper.R;
import com.example.timeshopper.ViewHolder.SellerViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zl.reik.dilatingdotsprogressbar.DilatingDotsProgressBar;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
{
    private DatabaseReference SellerRef;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    DilatingDotsProgressBar progressBar;
    private TextView loadText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        SellerRef = FirebaseDatabase.getInstance().getReference().child("Sellers");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(HomeActivity.this, CartActivity.class);
                startActivity(intent);

            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);

        TextView userNameTextView = headerView.findViewById(R.id.user_profile_name);

        userNameTextView.setText(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());

        recyclerView = findViewById(R.id.recycler_menu);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        progressBar = findViewById(R.id.progress);
        loadText = findViewById(R.id.loadText);

// show progress bar and start animating
        progressBar.showNow();

    }

    @Override
    protected void onStart() {
        super.onStart();

        // Query to retrieve all the seller from the database
        FirebaseRecyclerOptions<Sellers> options = new FirebaseRecyclerOptions.Builder<Sellers>()
                .setQuery(SellerRef, Sellers.class)
                .build();
        // Model (Seller) && ViewHolder (SellerViewHolder)
        FirebaseRecyclerAdapter<Sellers, SellerViewHolder> adapter =
                new FirebaseRecyclerAdapter<Sellers, SellerViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull SellerViewHolder holder, int position, @NonNull final Sellers model) {
                        holder.sellerNames.setText(model.getName());
                        holder.sellerPhones.setText(model.getPhone());
                        holder.sellerSids.setText(model.getSid());

                        Glide.with(HomeActivity.this).load(model.getImage()).placeholder(R.drawable.shop_image).into(holder.sellerImage);

                        // stop animation and hide
                        progressBar.hideNow();
                        loadText.setVisibility(View.GONE);

                        // After that setAdapter
                        // 21 sending user to SellerProductActivity
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                    Intent intent = new Intent(HomeActivity.this, SellerProductActivity.class);
                                    intent.putExtra("sID", model.getSid());
                                    startActivity(intent);

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public SellerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout, parent, false);
                        SellerViewHolder holder = new SellerViewHolder(view);
                        return holder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_cart) {
                Intent intent = new Intent(HomeActivity.this, CartActivity.class);
                startActivity(intent);

        }  else if (id == R.id.nav_logout) {

            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            startActivity(intent);
            finish();

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
