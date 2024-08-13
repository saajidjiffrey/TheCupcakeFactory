package com.example.thecupcakefactory.ui.orders;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thecupcakefactory.Order;
import com.example.thecupcakefactory.OrderHomeAdapter;
import com.example.thecupcakefactory.databinding.FragmentOrdersBinding;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class OrdersFragment extends Fragment {

    private FragmentOrdersBinding binding;
    OrderHomeAdapter adapterOrder;
    RecyclerView rcvOrder;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference userRef;
    FirebaseDatabase fb;
    private String userID;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        OrdersViewModel ordersViewModel =
                new ViewModelProvider(this).get(OrdersViewModel.class);

        binding = FragmentOrdersBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        //
        fb = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //
        rcvOrder = binding.rcvOrders;//UI referencing
        //
        FirebaseDatabase fb=FirebaseDatabase.getInstance();
        DatabaseReference ref= fb.getReference("order");
        Query query = ref.orderByChild("userID").equalTo(userID);
        FirebaseRecyclerOptions<Order> options = new FirebaseRecyclerOptions.Builder<Order>().setQuery(query,Order.class).build();
        adapterOrder= new OrderHomeAdapter(options);
        rcvOrder.setLayoutManager(new LinearLayoutManager(getContext()));
        rcvOrder.setAdapter(adapterOrder);
        //
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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