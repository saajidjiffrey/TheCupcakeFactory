package com.example.thecupcakefactory;

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

public class AdminOrdersActivity extends AppCompatActivity {
    OrderHomeAdapter adapterOrder;
    RecyclerView rcvOrderAdmin;
    Button ibtnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_orders);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        rcvOrderAdmin = findViewById(R.id.rcvOrdersAdmin);
        ibtnBack = findViewById(R.id.ibtnBackMngOrders);
        //
        FirebaseDatabase fb=FirebaseDatabase.getInstance();
        DatabaseReference ref= fb.getReference("order");
        FirebaseRecyclerOptions<Order> options = new FirebaseRecyclerOptions.Builder<Order>().setQuery(ref,Order.class).build();
        adapterOrder= new OrderHomeAdapter(options);
        rcvOrderAdmin.setLayoutManager(new LinearLayoutManager(this));
        rcvOrderAdmin.setAdapter(adapterOrder);

        ibtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    @Override public void onStart()
    {
        super.onStart();
        adapterOrder.startListening();
    }
    @Override public void onStop()
    {
        super.onStop();
        adapterOrder.stopListening();
    }
}