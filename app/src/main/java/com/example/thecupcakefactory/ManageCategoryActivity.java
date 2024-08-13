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

public class ManageCategoryActivity extends AppCompatActivity {
    Button btnAddCategory, ibtnBack;
    CategoryAdapterFB adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_category);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        btnAddCategory = findViewById(R.id.btnAddCatMng);
        ibtnBack = findViewById(R.id.ibtnBackMngCat);
        /////
        RecyclerView rcvCat= findViewById(R.id.rcvCat);
        FirebaseDatabase fb=FirebaseDatabase.getInstance();
        DatabaseReference ref= fb.getReference("category");
        FirebaseRecyclerOptions<Category> options=
                new FirebaseRecyclerOptions.
                        Builder<Category>().
                        setQuery(ref,Category.class).build();
        adapter= new
                CategoryAdapterFB(options);
        rcvCat.
                setLayoutManager(new
                        LinearLayoutManager(this));
        rcvCat.setAdapter(adapter);
        btnAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddCategoryActivity.class);
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
}