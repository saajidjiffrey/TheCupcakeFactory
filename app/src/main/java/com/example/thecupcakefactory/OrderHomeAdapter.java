package com.example.thecupcakefactory;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class OrderHomeAdapter extends FirebaseRecyclerAdapter <Order, OrderHomeAdapter.ViewHolder> {
    private FirebaseAuth firebaseAuth;
    private DatabaseReference userRef;
    private String userEmail;
    FirebaseDatabase fb;
    public OrderHomeAdapter(@NonNull FirebaseRecyclerOptions<Order> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull OrderHomeAdapter.ViewHolder viewHolder, int i, @NonNull Order model) {
        Order order= model;
        //
        fb = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        if (userEmail.equals("admin@gmail.com")){
            viewHolder.txvId.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            viewHolder.txvId.setVisibility(View.VISIBLE);
            viewHolder.txvUserName.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            viewHolder.txvUserName.setVisibility(View.VISIBLE);
            viewHolder.btnProcess.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            viewHolder.btnProcess.setVisibility(View.VISIBLE);
        }
        //
        viewHolder.txvId.setText(String.valueOf(order.getId()));
        viewHolder.txvCupName.setText(String.valueOf(order.getCupcakeName()));
        viewHolder.txvUserName.setText(String.valueOf(order.getUserName()));
        viewHolder.txvCupPrice.setText("LKR. " + String.valueOf(order.getCupcakePrice()));
        viewHolder.txvQuantity.setText(String.valueOf(order.getQuantity()) + " Pieces");
        viewHolder.txvTotal.setText("LKR. " + String.valueOf(order.getTotal().toString()));
        if (!order.isStatus()) {
            viewHolder.txvStatus.setText("Accepted");
        } else if (order.isStatus()) {
            viewHolder.txvStatus.setText("Pending");
        }

        String imageUrl = order.getCupcakeImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(viewHolder.itemView.getContext())
                    .load(imageUrl)
                    .into(viewHolder.imvCupImg);
        } else {
            // Set a placeholder image (optional)
            viewHolder.imvCupImg.setImageResource(R.drawable.cupcake_placeholder); // Replace with your placeholder resource ID
        }
        viewHolder.btnProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!order.isStatus()) {
                    order.UpdateStatus(fb, v.getContext(), true );
                    Toast.makeText(v.getContext(), "Order accepted successfully!", Toast.LENGTH_LONG).show();
                } else if (order.isStatus()) {
                    order.UpdateStatus(fb, v.getContext(), false);
                    Toast.makeText(v.getContext(), "Order set to pending.", Toast.LENGTH_LONG).show();
                }
            }
        });
        viewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                order.Delete(fb);
                Toast.makeText(v.getContext(), "Your order deleted successfully!", Toast.LENGTH_LONG).show();

            }
        });
    }

    @NonNull
    @Override
    public OrderHomeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=
                LayoutInflater.from(parent.getContext());
        View items= inflater.
                inflate(R.layout.order_item,parent,false);
        OrderHomeAdapter.ViewHolder holder=
                new OrderHomeAdapter.ViewHolder(items);
        return holder;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txvId, txvCupName,txvUserName,txvQuantity,txvCupPrice, txvTotal, txvStatus;
        ImageView imvCupImg;
        Button btnProcess, btnDelete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txvId= itemView.findViewById(R.id.txvOrderIdItem);
            txvCupName= itemView.findViewById(R.id.txvOrderCupcakeNameItem);
            txvUserName= itemView.findViewById(R.id.txvOrderUserNameItem);
            txvQuantity= itemView.findViewById(R.id.txvOrderQuantityItem);
            txvCupPrice= itemView.findViewById(R.id.txvOrderCupcakePriceItem);
            txvTotal= itemView.findViewById(R.id.txvOrderTotalItem);
            txvStatus= itemView.findViewById(R.id.txvOrderStatusItem);
            imvCupImg = itemView.findViewById(R.id.imvOrderImgItem);
            btnProcess = itemView.findViewById(R.id.btnProcess);
            btnDelete = itemView.findViewById(R.id.ibtnOrderDelete);
        }

    }

}
