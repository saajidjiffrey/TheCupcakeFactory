package com.example.thecupcakefactory;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Category {
    private  String categoryID;
    private  String categoryName;
    private String categoryDescription;
    private String categoryImage;

    public String getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryDescription() {
        return categoryDescription;
    }

    public void setCategoryDescription(String categoryDescription) {
        this.categoryDescription = categoryDescription;
    }

    public String getCategoryImage() {
        return categoryImage;
    }

    public void setCategoryImage(String categoryImage) {
        this.categoryImage = categoryImage;
    }
    public void Add(FirebaseDatabase fb){
        try {
            DatabaseReference reference = fb.getReference().child("category"); //referring the child(Customer) from fb
            reference.push().setValue(this); // pushing the new vlaues to the child
        }catch (Exception ex){
            throw ex;
        }
    }
    public void Delete(FirebaseDatabase fb)
    {
        try
        {
            DatabaseReference reference= fb.getReference().
                    child("category").child(String.valueOf(this.categoryID));
            reference.removeValue();

        }
        catch (Exception ex)
        {
            throw ex;
        }
    }
    public void Update(FirebaseDatabase fb)
    {
        try
        {
            DatabaseReference reference= fb.getReference().
                    child("category").child(String.valueOf(this.categoryID));
            HashMap<String, Object> result = new HashMap<>();
            result.put("catid",this.categoryID);
            result.put("catname",this.categoryName);
            result.put("catdescription",this.categoryDescription);
            result.put("catImg",this.categoryImage);
            reference.updateChildren(result);
        }
        catch (Exception ex)
        {
            throw ex;
        }
    }
}
