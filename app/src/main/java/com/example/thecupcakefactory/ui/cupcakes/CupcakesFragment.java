package com.example.thecupcakefactory.ui.cupcakes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thecupcakefactory.Cupcake;
import com.example.thecupcakefactory.CupcakeHomeAdapterFB;
import com.example.thecupcakefactory.databinding.FragmentCupcakesBinding;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CupcakesFragment extends Fragment {

    private FragmentCupcakesBinding binding;
    CupcakeHomeAdapterFB adapterCup;
    RecyclerView rcvCup;
    FirebaseDatabase fb;
    DatabaseReference ref;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        CupcakeViewModel cupcakeViewModel =
                new ViewModelProvider(this).get(CupcakeViewModel.class);

        binding = FragmentCupcakesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        /////
        rcvCup= binding.rcvHomeCup;
        //
        fb=FirebaseDatabase.getInstance();
        ref= fb.getReference("cupcake");
        FirebaseRecyclerOptions<Cupcake> options = new FirebaseRecyclerOptions.Builder<Cupcake>().setQuery(ref,Cupcake.class).build();
        adapterCup= new CupcakeHomeAdapterFB(options);
        rcvCup.setLayoutManager(new LinearLayoutManager(getContext()));
        rcvCup.setAdapter(adapterCup);
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
        adapterCup.startListening();
    }
    @Override public void onStop()
    {
        super.onStop();
        adapterCup.stopListening();
    }

}