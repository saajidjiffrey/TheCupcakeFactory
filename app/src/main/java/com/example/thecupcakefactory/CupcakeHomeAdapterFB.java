package com.example.thecupcakefactory;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class CupcakeHomeAdapterFB extends FirebaseRecyclerAdapter<Cupcake, CupcakeHomeAdapterFB.ViewHolder> {


    public CupcakeHomeAdapterFB(@NonNull FirebaseRecyclerOptions<Cupcake> options) {
        super(options);
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txvCID,txvCName,txvCPrice;
        Button ibtnOpen;
        ImageView imvCimg;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txvCID= itemView.findViewById(R.id.txvCupGIDItem);
            txvCName= itemView.findViewById(R.id.txvCupGNameItem);
            txvCPrice= itemView.findViewById(R.id.txvCupGPriceItem);
            imvCimg=itemView.findViewById(R.id.imvCupGImgItem);
            ibtnOpen=itemView.findViewById(R.id.ibtnOpenGCupItem);
        }
    }
    @Override
    protected void onBindViewHolder(@NonNull CupcakeHomeAdapterFB.ViewHolder holder, int position, @NonNull Cupcake model) {
        Cupcake cupcake= model;
        holder.txvCID.setText(String.valueOf(cupcake.getCupcakeID()));
        holder.txvCName.setText(String.
                valueOf(cupcake.getCupcakeName()));
        holder.txvCPrice.setText(String.valueOf(cupcake.getCupcakePrice()));

        String imageUrl = cupcake.getCupcakeImage();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .into(holder.imvCimg);
        } else {
            // Set a placeholder image (optional)
            holder.imvCimg.setImageResource(R.drawable.ic_launcher_background); // Replace with your placeholder resource ID
        }
        holder.ibtnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(), CupcakeActivity.class);
                intent.putExtra("id",cupcake.getCupcakeID());
                intent.putExtra("name",cupcake.getCupcakeName());
                intent.putExtra("category",cupcake.getCupcakeCategory());
                intent.putExtra("description",cupcake.getCupcakeDescription());
                System.out.println(cupcake.getCupcakePrice());
                System.out.println(cupcake.getCupcakeOffers());
                intent.putExtra("price",cupcake.getCupcakePrice());
                intent.putExtra("offer",cupcake.getCupcakeOffers());
                intent.putExtra("image",cupcake.getCupcakeImage());
                v.getContext().startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public CupcakeHomeAdapterFB.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=
                LayoutInflater.from(parent.getContext());
        View items= inflater.
                inflate(R.layout.general_cupcake_item,parent,false);
        CupcakeHomeAdapterFB.ViewHolder holder=
                new CupcakeHomeAdapterFB.ViewHolder(items);
        return holder;
    }


}
