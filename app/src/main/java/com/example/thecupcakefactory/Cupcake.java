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

public class Cupcake {
    private  String CupcakeID;
    private  String CupcakeName;
    private String CupcakeCategory;
    private String CupcakeDescription;
    private Double CupcakePrice;
    private Double CupcakeOffers;
    private String CupcakeImage;

    public String getCupcakeImage() {
        return CupcakeImage;
    }

    public void setCupcakeImage(String cupcakeImage) {
        CupcakeImage = cupcakeImage;
    }

    public String getCupcakeID() {
        return CupcakeID;
    }

    public void setCupcakeID(String cupcakeID) {
        CupcakeID = cupcakeID;
    }

    public String getCupcakeName() {
        return CupcakeName;
    }

    public void setCupcakeName(String cupcakeName) {
        CupcakeName = cupcakeName;
    }

    public String getCupcakeCategory() {
        return CupcakeCategory;
    }

    public void setCupcakeCategory(String cupcakeCategory) {
        CupcakeCategory = cupcakeCategory;
    }

    public String getCupcakeDescription() {
        return CupcakeDescription;
    }

    public void setCupcakeDescription(String cupcakeDescription) {
        CupcakeDescription = cupcakeDescription;
    }

    public Double getCupcakePrice() {
        return CupcakePrice;
    }

    public void setCupcakePrice(Double cupcakePrice) {
        CupcakePrice = cupcakePrice;
    }

    public Double getCupcakeOffers() {
        return CupcakeOffers;
    }

    public void setCupcakeOffers(Double cupcakeOffers) {
        CupcakeOffers = cupcakeOffers;
    }

    public void Add(FirebaseDatabase fb){
        try {
            DatabaseReference reference = fb.getReference().child("cupcake").child(this.CupcakeID);
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
                    child("cupcake").child(String.valueOf(this.CupcakeID));
            reference.removeValue();

        }
        catch (Exception ex)
        {
            throw ex;
        }
    }
    public void Update(FirebaseDatabase fb, Context context) {
        try {
            // Get a reference to the specific cupcake using its ID
            DatabaseReference reference = fb.getReference().child("cupcake").child(String.valueOf(this.CupcakeID));

            // Create a HashMap containing the updated values
            HashMap<String, Object> updates = new HashMap<>();
            updates.put("cupcakeName", this.CupcakeName); // Update only specific fields if needed
            updates.put("cupcakeCategory", this.CupcakeCategory);
            updates.put("cupcakeDescription", this.CupcakeDescription);
            updates.put("cupcakePrice", this.CupcakePrice);
            updates.put("cupcakeOffers", this.CupcakeOffers);
            // Only update the image URL if necessary (consider a separate function)
            if (this.CupcakeImage != null) {
                updates.put("cupcakeImage", this.CupcakeImage);
            }

            // Update the cupcake data using updateChildren()
            reference.updateChildren(updates)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Update successful (optional: handle success)
                            Toast.makeText(context, "Cupcake updated successfully!", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Update failed (handle error)
                            Log.e("UpdateCupcakeActivity", "Error updating cupcake", e);
                            Toast.makeText(context, "Error updating cupcake. Please try again.", Toast.LENGTH_LONG).show();
                        }
                    });

        } catch (Exception ex) {
            throw ex; // Re-throw the exception for proper handling
        }
    }


}
