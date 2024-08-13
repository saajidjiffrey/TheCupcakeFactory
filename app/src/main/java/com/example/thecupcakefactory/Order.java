package com.example.thecupcakefactory;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Order {
    private String id;
    private String userID;
    private String userName;
    private String cupcakeID;
    private String cupcakeName;
    private double cupcakePrice;
    private String cupcakeImageUrl;
    private Double total;
    private int quantity;
    private boolean status;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserID(String userIdFB) {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getCupcakeID() {
        return cupcakeID;
    }

    public void setCupcakeID(String cupcakeID) {
        this.cupcakeID = cupcakeID;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getUserID() {
        return userID;
    }

    public String getCupcakeName() {
        return cupcakeName;
    }

    public void setCupcakeName(String cupcakeName) {
        this.cupcakeName = cupcakeName;
    }

    public double getCupcakePrice() {
        return cupcakePrice;
    }

    public void setCupcakePrice(double cupcakePrice) {
        this.cupcakePrice = cupcakePrice;
    }

    public String getCupcakeImageUrl() {
        return cupcakeImageUrl;
    }

    public void setCupcakeImageUrl(String cupcakeImageUrl) {
        this.cupcakeImageUrl = cupcakeImageUrl;
    }

    public void AddOrder(FirebaseDatabase fb){
        try {
            DatabaseReference reference = fb.getReference().child("order").child(this.id);
            reference.setValue(this);
        }catch (Exception ex){
            throw ex;
        }
    }
    public void Delete(FirebaseDatabase fb)
    {
        try
        {
            DatabaseReference reference= fb.getReference().
                    child("order").child(String.valueOf(this.id));
            reference.removeValue();

        }
        catch (Exception ex)
        {
            throw ex;
        }
    }
    public void UpdateStatus(FirebaseDatabase fb, Context context, boolean status) {
        try {
            DatabaseReference reference = fb.getReference().child("order").child(String.valueOf(this.id));

            HashMap<String, Object> updates = new HashMap<>();
            updates.put("status", status);
            reference.updateChildren(updates)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(context, "Order accepted successfully!", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("AdminOrderProcessing", "Error accepting order", e);
                            Toast.makeText(context, "Error accepting order. Please try again.", Toast.LENGTH_LONG).show();
                        }
                    });

        } catch (Exception ex) {
            throw ex;
        }
    }
}
