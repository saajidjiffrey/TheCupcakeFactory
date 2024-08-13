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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

public class HomeActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseStorage storage;
    private DatabaseReference userRef;
    CupcakeHomeAdapterFB adapterCup;
    CategoryAdapterFB adapterCat;
    RecyclerView rcvCup, rcvCat, rcvOffer;
    Button ibtnLogout;
    TextView txvUserFullName;
    ImageView imvUserProfile;
    private String userIdFB, userFullName, userProfileUrl ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //
        boolean refreshData = getIntent().getBooleanExtra("refreshData", false);
        if (refreshData) {
            // Stop listening initially (optional, for efficiency)
            adapterCup.stopListening();
            adapterCat.stopListening();
            // Restart listening to refresh data
            adapterCup.startListening();
            adapterCat.stopListening();
        }
        firebaseAuth = FirebaseAuth.getInstance();
        userIdFB = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference("users").child(userIdFB);
        ///
        //reference ui view
        setUpView();
        ///
        updateUserDetail();
        //
        FirebaseDatabase fb=FirebaseDatabase.getInstance();
        DatabaseReference ref= fb.getReference("cupcake");
        DatabaseReference refcat= fb.getReference("category");
        FirebaseRecyclerOptions<Cupcake> options= new FirebaseRecyclerOptions.Builder<Cupcake>().setQuery(ref,Cupcake.class).build();
        FirebaseRecyclerOptions<Category> options2= new FirebaseRecyclerOptions.Builder<Category>().setQuery(ref,Category.class).build();
        adapterCup= new CupcakeHomeAdapterFB(options);
        adapterCat= new CategoryAdapterFB(options2);
        rcvCup.setLayoutManager(new LinearLayoutManager(this));
        rcvCat.setLayoutManager(new LinearLayoutManager(this));
        rcvCup.setAdapter(adapterCup);
        rcvCat.setAdapter(adapterCat);

        //
//        rcvCup.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
//            @Override
//            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
//                View view = rv.findChildViewUnder(e.getX(), e.getY());
//                if (view != null && view.findViewById(R.id.card) != null) { // Check if clicked view is a cupcake card
//                    int clickedPosition = rv.getChildAdapterPosition(view);
//                    // Get cupcake data based on a unique identifier, not position
//                    // Firebase child nodes might not correspond directly to your list order
//                    Cupcake selectedCupcake = null;  // Initialize to null
//
//                    // Access your data adapter to get the cupcake at the clicked position
//                    CupcakeHomeAdapterFB adapter = (CupcakeHomeAdapterFB) rv.getAdapter();
//                    if (adapter != null && clickedPosition >= 0 && clickedPosition < adapter.getItemCount()) {
//                        selectedCupcake = adapter.getCupcakeAtPosition(clickedPosition);
//                    }
//
//                    // Navigate to the specific cupcake's page (assuming selectedCupcake is not null)
//                    if (selectedCupcake != null) {
//                        Intent intent = new Intent(HomeActivity.this, CupcakeActivity.class);
//                        intent.putExtra("cupclass", selectedCupcake.getClass());
//                        intent.putExtra("cupId", selectedCupcake.getCupcakeID());
//                        intent.putExtra("cupName", selectedCupcake.getCupcakeName());
//                        intent.putExtra("cupCat", selectedCupcake.getCupcakeCategory());
//                        intent.putExtra("cupDesc", selectedCupcake.getCupcakeDescription());
//                        intent.putExtra("cupPrice", selectedCupcake.getCupcakePrice());
//                        intent.putExtra("cupOffers", selectedCupcake.getCupcakeOffers());
//                        intent.putExtra("cupImage", selectedCupcake.getCupcakeImage());
//                        startActivity(intent);
//                    }
//                    return true;
//                }
//                return false;
//            }
//
//            @Override
//            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
//                // Not needed for this implementation
//            }
//
//            @Override
//            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
//                // Not needed for this implementation
//            }
//        });
        ibtnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 1. Get an instance of FirebaseAuth
                FirebaseAuth mAuth = FirebaseAuth.getInstance();

                // 2. Sign out the user
                mAuth.signOut();
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
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
    private void setUpView(){
        rcvCup= findViewById(R.id.rcvHomeCup);
        rcvCat= findViewById(R.id.rcvHomeCat);
        rcvOffer=findViewById(R.id.rcvHomeOffer);
        ibtnLogout = findViewById(R.id.ibtnLogoutHome);
        txvUserFullName = findViewById(R.id.txvHomeUsername);
        imvUserProfile =findViewById(R.id.imvHomeUserProfile);
    }
    @Override public void onStart()
    {
        super.onStart();
        adapterCat.startListening();
        adapterCup.startListening();
    }
    @Override public void onStop()
    {
        super.onStop();
        adapterCat.stopListening();
        adapterCup.stopListening();
    }

}