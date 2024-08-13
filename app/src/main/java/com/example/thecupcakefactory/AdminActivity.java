package com.example.thecupcakefactory;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private DatabaseReference userRef;
    TextView txvUserFullName;
    ImageView imvUserProfile;
    Button btnManageCupcake, btnManageCategory,btnManageOrder, ibtnLogout;
    private String userIdFB, userFullName, userProfileUrl ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        firebaseAuth = FirebaseAuth.getInstance();
        userIdFB = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference("users").child(userIdFB);
        updateUserDetail();
        //
        txvUserFullName = findViewById(R.id.txvAdminUsername);
        imvUserProfile = findViewById(R.id.imvAdminAvatar);
        btnManageCupcake = findViewById(R.id.btnAdminManageCupcake);
        btnManageCategory = findViewById(R.id.btnAdminManageCategory);
        btnManageOrder = findViewById(R.id.btnAdminProcessOrders);
        ibtnLogout = findViewById(R.id.ibtnLogout);
        ibtnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 1. Get an instance of FirebaseAuth
                FirebaseAuth mAuth = FirebaseAuth.getInstance();

                // 2. Sign out the user
                mAuth.signOut();
                Intent intent = new Intent(AdminActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
        btnManageCupcake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ManageCupcakeActivity.class);
                startActivity(intent);
            }
        });
        btnManageCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ManageCategoryActivity.class);
                startActivity(intent);
            }
        });
        btnManageOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AdminOrdersActivity.class);
                startActivity(intent);
            }
        });
    }
    private void updateUserDetail(){
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    userFullName = snapshot.child("fullname").getValue(String.class);
                    userProfileUrl = snapshot.child("userProfileUrl").getValue(String.class);

                    txvUserFullName.setText(userFullName);
                    if (userProfileUrl != null && !userProfileUrl.isEmpty()) {
                        Glide.with(getBaseContext())
                                .load(userProfileUrl)
                                //                                .placeholder(R.drawable.boy) // optional placeholder image
                                //                                .error(R.drawable.boy) // optional error image
                                .into(imvUserProfile);
                    } else {
                        // Set a placeholder image (optional)
                        imvUserProfile.setImageResource(R.drawable.boy); // Replace with your placeholder resource ID
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors
            }
        });
    }
}