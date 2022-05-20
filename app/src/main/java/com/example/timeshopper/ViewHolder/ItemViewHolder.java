package com.example.timeshopper.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.timeshopper.Interface.ItemClickListner;
import com.example.timeshopper.R;


public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    // Access productItemLayout items

    public TextView txtProductName, txtProductDescription, txtProductPrice, txtProductState;
    public ImageView imageView;

    // Access ItemClickListner Interface
    public ItemClickListner listner;

    public ItemViewHolder(@NonNull View itemView){
        super(itemView);

        txtProductName = itemView.findViewById(R.id.seller_product_name);
        txtProductDescription = itemView.findViewById(R.id.product_seller_description);
        txtProductPrice = itemView.findViewById(R.id.product_seller_price);
        imageView = itemView.findViewById(R.id.product_seller_image);
    }

    // Interface(ItemClickListner)
    public void setItemClickListner(ItemClickListner listner){
        this.listner = listner;
    }

    @Override
    public void onClick(View view) {
        listner.onClick(view, getAdapterPosition(),false);
    }
}

