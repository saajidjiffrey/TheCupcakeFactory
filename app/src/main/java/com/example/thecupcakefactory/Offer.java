package com.example.thecupcakefactory;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Offer {
    private  String offerID;
    private  String offerName;
    private int offerRate;

    public String getOfferID() {
        return offerID;
    }

    public void setOfferID(String offerID) {
        this.offerID = offerID;
    }

    public String getOfferName() {
        return offerName;
    }

    public void setOfferName(String offerName) {
        this.offerName = offerName;
    }

    public int getOfferRate() {
        return offerRate;
    }

    public void setOfferRate(int offerRate) {
        this.offerRate = offerRate;
    }
    public void Add(FirebaseDatabase fb){
        try {
            DatabaseReference reference = fb.getReference().child("offer"); //referring the child(Customer) from fb
            reference.push().setValue(this); // pushing the new vlaues to the child
        }catch (Exception ex){
            throw ex;
        }
    }
}
