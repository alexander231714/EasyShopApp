package com.mobile_genius.easyshop;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.actions.ItemListIntents;

public class ProductoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
public TextView productoNom,productoDesc,productoPrecio,productoCantidad;
public ImageView prodImg;
public ItemClickListener listener;
    public ProductoViewHolder(@NonNull View itemView) {
        super(itemView);

        productoNom = (TextView) itemView.findViewById(R.id.producNomb);
        productoDesc = (TextView) itemView.findViewById(R.id.prodDescrip);
        productoCantidad = (TextView) itemView.findViewById(R.id.prodcant);
        productoPrecio = (TextView) itemView.findViewById(R.id.prodprecio);
        prodImg = (ImageView) itemView.findViewById(R.id.imgProd);
    }

    public void setItemClickListener(ItemClickListener listener){
        this.listener=listener;
    }

    @Override
    public void onClick(View v) {
listener.onClick(v,getAdapterPosition(), false);
    }
}
