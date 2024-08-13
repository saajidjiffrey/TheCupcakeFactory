package com.example.thecupcakefactory;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    TextInputLayout tilLogEmail, tilLogPass ;
    EditText edtLogEmail, edtLogPass;
    Button btnLogin;
    TextView txvSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        setUpView();
        // Initialize Firebase components
        firebaseAuth = FirebaseAuth.getInstance();
        txvSignUp.setOnClickListener(this::navigateToSignUp);
        // Check for existing user
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            System.out.println("Email " + currentUser.getEmail());
            Intent intent;
            if (!currentUser.getEmail().equals("admin@gmail.com")){
                intent = new Intent(MainActivity.this, MainActivity2.class);

            }else {
                intent = new Intent(MainActivity.this, AdminActivity.class);

            } startActivity(intent);
            finish();

        }
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String email = edtLogEmail.getText().toString().trim();
                    String password = edtLogPass.getText().toString().trim();

                    // Input validation
                    if (email.isEmpty()) {
                        tilLogEmail.setError("Please enter your email address");
                        return; // Stop execution if name is empty
                    } else {
                        tilLogEmail.setError(null);
                    }
                    if (password.isEmpty()) {
                        tilLogPass.setError("Please enter your Password");
                        return; // Stop execution if name is empty
                    } else {
                        tilLogPass.setError(null);
                    }
                    //

                    // Login user with Firebase Authentication
                    firebaseAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {

                                        if (email.equals("admin@gmail.com")){
                                            Toast.makeText(getApplicationContext(), "Login successfull! Logged in as Admin", Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(MainActivity.this, AdminActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }else{
                                            Toast.makeText(getApplicationContext(), "Login successfull!", Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                                            startActivity(intent);
                                            finish();
                                        }

                                    } else {
                                        // Login failed, handle error
                                        Exception e = task.getException();
                                        Toast.makeText(getApplicationContext(), "Login failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                        Log.e("MainActivity", "Error logging in user", e);
                                    }
                                }
                            });

                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Error logging in user", Toast.LENGTH_LONG).show();
                    Log.e("MainActivity", "Error logging in user", e);
                }
            }
        });
    }
    private void setUpView(){
        tilLogEmail = findViewById(R.id.edtLogEmail);
        edtLogEmail = tilLogEmail.getEditText();
        tilLogPass = findViewById(R.id.edtLogPass);
        edtLogPass = tilLogPass.getEditText();
        btnLogin = findViewById(R.id.btnLogin);
        txvSignUp = findViewById(R.id.logDontHaveAnAccount);

    }

    public void navigateToSignUp(View view) {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }
}