package com.example.thecupcakefactory.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.thecupcakefactory.MainActivity;
import com.example.thecupcakefactory.R;
import com.example.thecupcakefactory.databinding.FragmentHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference userRef;
    TextView txvUserFullName;
    ImageView imvUserProfile;
    Button ibtnLogout;
    private String userIdFB, userFullName, userProfileUrl ;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        //
        firebaseAuth = FirebaseAuth.getInstance();
        userIdFB = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference("users").child(userIdFB);
        //
        updateUserDetail();
        ibtnLogout = binding.ibtnLogoutHome;
        txvUserFullName = binding.txvHomeUsername;
        imvUserProfile = binding.imvHomeUserProfile;

        ibtnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 1. Get an instance of FirebaseAuth
                FirebaseAuth mAuth = FirebaseAuth.getInstance();

                // 2. Sign out the user
                mAuth.signOut();
                Intent intent = new Intent(getContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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
                        Glide.with(getContext())
                                .load(userProfileUrl)
                                .into(imvUserProfile);
                    } else {
                        imvUserProfile.setImageResource(R.drawable.boy);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}