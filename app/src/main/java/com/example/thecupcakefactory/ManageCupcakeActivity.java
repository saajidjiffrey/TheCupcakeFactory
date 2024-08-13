package com.example.thecupcakefactory;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ManageCupcakeActivity extends AppCompatActivity {
    Button btnAddCupcake, ibtnBack;
    CupcakeAdapterFB adapter;
    RecyclerView rcvCup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_cupcake);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        /////
        setUpViews();
        /////
        FirebaseDatabase fb=FirebaseDatabase.getInstance();
        DatabaseReference ref= fb.getReference("cupcake");
        FirebaseRecyclerOptions<Cupcake> options= new FirebaseRecyclerOptions.Builder<Cupcake>().setQuery(ref,Cupcake.class).build();
        adapter= new CupcakeAdapterFB(options);
        rcvCup.setLayoutManager(new LinearLayoutManager(this));
        rcvCup.setAdapter(adapter);

        boolean refreshData = getIntent().getBooleanExtra("refreshData", false);
        if (refreshData) {
            // Stop listening initially (optional, for efficiency)
            adapter.stopListening();

            // Restart listening to refresh data
            adapter.startListening();
        }

        btnAddCupcake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddCupcakeActivity.class);
                startActivity(intent);
            }
        });
        ibtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AdminActivity.class);
                startActivity(intent);
            }
        });
    }
    @Override public void onStart()
    {
        super.onStart();
        adapter.startListening();
    }
    @Override public void onStop()
    {
        super.onStop();
        adapter.stopListening();
    }
    private  void setUpViews(){
        btnAddCupcake = findViewById(R.id.btnAddCupcakeMng);
        ibtnBack = findViewById(R.id.ibtnBackMngCup);
        rcvCup= findViewById(R.id.rcvCupcake);
    }
}