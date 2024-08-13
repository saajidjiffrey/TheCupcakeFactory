package com.example.thecupcakefactory;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class CategoryAdapterFB extends FirebaseRecyclerAdapter<Category,CategoryAdapterFB.ViewHolder> {
    public CategoryAdapterFB(@NonNull FirebaseRecyclerOptions<Category> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull CategoryAdapterFB.ViewHolder holder, int position, @NonNull Category model) {
        Category category= model;
        holder.txvCID.setText(String.
                valueOf(category.getCategoryID()));
        holder.txvCName.setText(String.
                valueOf(category.getCategoryName()));
        holder.txvCDesc.setText(category.getCategoryDescription());

    }

    @NonNull
    @Override
    public CategoryAdapterFB.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=
                LayoutInflater.from(parent.getContext());
        View items= inflater.
                inflate(R.layout.category_item,parent,false);
        CategoryAdapterFB.ViewHolder holder=
                new CategoryAdapterFB.ViewHolder(items);
        return holder;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txvCID,txvCName,txvCDesc;
        ImageView imvCImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txvCID= itemView.findViewById(R.id.txvCatIDItem);
            txvCName= itemView.findViewById(R.id.txvCatNameItem);
            txvCDesc= itemView.findViewById(R.id.txvCatDescItem);
            imvCImage = itemView.findViewById(R.id.imvCatImgItem);

        }
    }
}
