package com.example.timeshopper.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.timeshopper.Interface.ItemClickListner;
import com.example.timeshopper.R;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    // Access productItemLayout items

    public TextView txtProductName, txtProductDescription, txtProductPrice;
    public ImageView imageView;

    // Access ItemClickListener Interface
    public ItemClickListner listner;

    public ProductViewHolder(@NonNull View itemView){
        super(itemView);

          txtProductName = itemView.findViewById(R.id.particular_product_name);
          txtProductDescription = itemView.findViewById(R.id.particular_product_description);
          txtProductPrice = itemView.findViewById(R.id.particular_product_price);
          imageView = itemView.findViewById(R.id.particular_product_image);
    }

    // Interface(ItemClickListener)
    public void setItemClickListner(ItemClickListner listner){
        this.listner = listner;
    }

    @Override
    public void onClick(View view) {
        listner.onClick(view, getAdapterPosition(),false);
    }
}
