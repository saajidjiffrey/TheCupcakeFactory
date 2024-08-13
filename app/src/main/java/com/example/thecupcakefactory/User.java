package com.example.thecupcakefactory;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class User {
    private String userId;
    private  String userFullName;
    private int userPhone;
    private String userBirthday;
    private String userImage;
    private  String userEmail;
    private  String userPassword;

    public User(String userId, String userFullName, int userPhone, String userBirthday, String userImage, String userEmail) {
        this.userId = userId;
        this.userFullName = userFullName;
        this.userPhone = userPhone;
        this.userBirthday = userBirthday;
        this.userImage = userImage;
        this.userEmail = userEmail;
    }
    public User() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserBirthday() {
        return userBirthday;
    }

    public void setUserBirthday(String userBirthday) {
        this.userBirthday = userBirthday;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public int getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(int userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public void AddUser(FirebaseDatabase fb){
        try {
            DatabaseReference reference = fb.getReference().child("user"); //referring the child(Customer) from fb
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
                    child("user").child(String.valueOf(this.userEmail));
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
                    child("user").child(String.valueOf(this.userEmail));
            HashMap<String, Object> result = new HashMap<>();
            result.put("fullname",this.userFullName);
            result.put("phone",this.userPhone);
            result.put("birthday",this.userBirthday);
            result.put("email",this.userEmail);
            reference.updateChildren(result);

        }
        catch (Exception ex)
        {
            throw ex;
        }
    }
}
