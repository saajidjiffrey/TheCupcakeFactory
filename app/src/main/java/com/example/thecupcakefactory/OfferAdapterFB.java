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

public class OfferAdapterFB extends FirebaseRecyclerAdapter<Offer, OfferAdapterFB.ViewHolder> {
    public OfferAdapterFB(@NonNull FirebaseRecyclerOptions<Offer> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull OfferAdapterFB.ViewHolder holder, int position, @NonNull Offer model) {
        Offer offer= model;
        holder.txvOfferID.setText(String.
                valueOf(offer.getOfferID()));
        holder.txvOfferName.setText(String.
                valueOf(offer.getOfferName()));
        holder.txvOfferRate.setText(String.valueOf(offer.getOfferRate()));
    }

    @NonNull
    @Override
    public OfferAdapterFB.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=
                LayoutInflater.from(parent.getContext());
        View items= inflater.
                inflate(R.layout.offer_item,parent,false);
        OfferAdapterFB.ViewHolder holder=
                new OfferAdapterFB.ViewHolder(items);
        return holder;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txvOfferID,txvOfferName,txvOfferRate;
        ImageView imvOfferImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txvOfferID= itemView.findViewById(R.id.txvOfferIdItem);
            txvOfferName= itemView.findViewById(R.id.txvOfferNameItem);
            txvOfferRate= itemView.findViewById(R.id.txvOfferRateItem);
            imvOfferImage = itemView.findViewById(R.id.imvOfferImgItem);

        }
    }
}
