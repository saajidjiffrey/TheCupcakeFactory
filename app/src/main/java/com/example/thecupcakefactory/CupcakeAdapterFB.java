package com.example.thecupcakefactory;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;


public class CupcakeAdapterFB extends FirebaseRecyclerAdapter<Cupcake,CupcakeAdapterFB.ViewHolder>{

    public CupcakeAdapterFB(@NonNull FirebaseRecyclerOptions<Cupcake> options) {
        super(options);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txvCID,txvCName,txvCPrice;
        Button ibtnCEdit,ibtnCRemove;
        ImageView imvCimg;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txvCID= itemView.findViewById(R.id.txvCupIDItem);
            txvCName= itemView.findViewById(R.id.txvCupcakeNameItem);
            txvCPrice= itemView.findViewById(R.id.txvCupcakeCategoryItem);
            imvCimg=itemView.findViewById(R.id.imvCupGImgItem);
            ibtnCEdit= itemView.findViewById(R.id.ibtnEditCupItem);
            ibtnCRemove= itemView.findViewById(R.id.ibtnRemoveCupItem);
        }
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Cupcake model) {
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
        holder.ibtnCEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(position);
                System.out.println(cupcake.getCupcakeID());
                System.out.println(cupcake.getCupcakeName());
                Intent intent=new Intent(v.getContext(), UpdateCupcakeActivity.class);
                intent.putExtra("id",cupcake.getCupcakeID());
                intent.putExtra("name",cupcake.getCupcakeName());
                intent.putExtra("category",cupcake.getCupcakeCategory());
                intent.putExtra("description",cupcake.getCupcakeDescription());
                intent.putExtra("price",cupcake.getCupcakePrice());
                intent.putExtra("offer",cupcake.getCupcakeOffers());
                intent.putExtra("image",cupcake.getCupcakeImage());
                v.getContext().startActivity(intent);
            }
        });
        holder.ibtnCRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseDatabase fb=FirebaseDatabase.getInstance();;
                cupcake.Delete(fb);
                Toast.makeText(v.getContext(), "Cupcake Deleted successfully!", Toast.LENGTH_LONG).show();

                System.out.println(fb);
            }
        });
    }

    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull Cupcake model, int position) {

    }

    @NonNull
    @Override
    public CupcakeAdapterFB.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=
                LayoutInflater.from(parent.getContext());
        View items= inflater.
                inflate(R.layout.cupcake_item,parent,false);
        ViewHolder holder=
                new ViewHolder(items);
        return holder;
    }


}
