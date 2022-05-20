package com.example.timeshopper.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.timeshopper.Interface.ItemClickListner;
import com.example.timeshopper.R;

public class SellerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView sellerNames, sellerPhones, sellerSids;
    public ImageView sellerImage;

    public ItemClickListner listner;

    public SellerViewHolder(@NonNull View itemView) {
        super(itemView);

        sellerNames = itemView.findViewById(R.id.seller_names);
        sellerPhones = itemView.findViewById(R.id.seller_phones);
        sellerSids = itemView.findViewById(R.id.seller_sids);
        sellerImage = itemView.findViewById(R.id.seller_image);
    }

    public void setItemClickListner(ItemClickListner listner){
        this.listner = listner;
    }

    @Override
    public void onClick(View view) {
        listner.onClick(view, getAdapterPosition(),false);
    }
}
