package com.example.thecupcakefactory;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.UUID;

public class CupcakeActivity extends AppCompatActivity {
    TextView txvCupName, txvCupCat, txvCupDesc, txvCupPrice,txvQuantity, txvTotal;
    ImageView imvCupImg;
    Button btnOrder, btnIncrease, btnDecrease, ibtnBack;
    private String userIdFB, userFullName;
    private int quantity;
    private double total;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference userRef;
    FirebaseDatabase fb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cupcake);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        String name = intent.getStringExtra("name");
        String category = intent.getStringExtra("category");
        String description = intent.getStringExtra("description");
        Double price = Double.valueOf(intent.getDoubleExtra("price", 0));
        String offers = String.valueOf(intent.getDoubleExtra("offer", 0));
        String image = intent.getStringExtra("image");

        //
        SetDB();
        firebaseAuth = FirebaseAuth.getInstance();
        userIdFB = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference("users").child(userIdFB);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Assuming "fullname" is the key where full name is stored
                    userFullName = dataSnapshot.child("fullname").getValue(String.class);

                    // Now you can use the userFullName variable as needed
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle potential errors here
            }
        });        setupViews();
        txvCupName.setText(name);
        txvCupCat.setText(category);
        txvCupDesc.setText(description);
        System.out.println(price);
        System.out.println(offers);
        txvCupPrice.setText("LKR. " + price);
        if (image != null && !image.isEmpty()) {
            Glide.with(this)
                    .load(image)
                    .into(imvCupImg);
        } else {
            // Set a placeholder image if the URL is not available
            imvCupImg.setImageResource(R.drawable.cupcake_placeholder);
        }

        btnIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                quantity++;
                txvQuantity.setText(String.valueOf(quantity));

                double cupcakePrice = price;
                System.out.println(cupcakePrice);
                total = cupcakePrice * quantity;
                txvTotal.setText("LKR. " + String.valueOf(total));
            }
        });

        // Decrease button click listener
        btnDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity > 1) {
                    quantity--;
                    txvQuantity.setText(String.valueOf(quantity));

                    double cupcakePrice = price;
                    System.out.println(cupcakePrice);
                    total = cupcakePrice * quantity;
                    txvTotal.setText("LKR. " + String.valueOf(total));
                }
            }
        });
        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Order order = new Order();
                order.setId(String.valueOf(UUID.randomUUID()));
                order.setUserID(userIdFB);
                order.setUserName(userFullName);
                order.setQuantity(quantity);
                order.setTotal(total);
                order.setStatus(false);
                order.setCupcakeID(id);
                order.setCupcakeName(name);
                order.setCupcakePrice(price);
                order.setCupcakeImageUrl(image);
                try {
                    order.AddOrder(fb);
                    Toast.makeText(CupcakeActivity.this, "Order placed successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity2.class);
                    startActivity(intent);
                } catch (Exception e) {
                    Log.e("Order", "Error placing order:", e);
                    Toast.makeText(CupcakeActivity.this, "Failed to place order. Please try again.", Toast.LENGTH_SHORT).show();
                }

            }
        });
        ibtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getApplicationContext(), MainActivity2.class);
                startActivity(intent);
            }
        });

    }
    private void setupViews(){
        txvCupName = findViewById(R.id.txvCupcakeNameItem);
        txvCupCat = findViewById(R.id.txvCupcakeCategoryItem);
        txvCupDesc = findViewById(R.id.txvCupcakeDesc);
        txvCupPrice = findViewById(R.id.txvCupcakePriceItem);
        txvQuantity = findViewById(R.id.txvQuantity);
        txvTotal =findViewById(R.id.txvTotal);
        imvCupImg = findViewById(R.id.imvCupcakeImage);
        btnOrder = findViewById(R.id.btnOrder);
        btnIncrease = findViewById(R.id.btnIncrease);
        btnDecrease = findViewById(R.id.btnDecrease);
        ibtnBack = findViewById(R.id.ibtnBackCupcake);

    }
    private void SetDB(){
        try {
            fb = FirebaseDatabase.getInstance(); //assign the database instance for the variable

        }
        catch (Exception ex)
        {
            Toast.makeText(getApplicationContext(), "Error occurred in Database" + ex.getMessage(), Toast.LENGTH_LONG).show();

        }
    }
}