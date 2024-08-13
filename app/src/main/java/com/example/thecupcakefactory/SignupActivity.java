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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    TextInputLayout tilSignupEmail, tilSignupPass ;
    EditText edtSignupEmail, edtSignupPass;
    TextView loginTextView;
    Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        tilSignupEmail = findViewById(R.id.edtSignupEmail);
        edtSignupEmail = tilSignupEmail.getEditText();
        tilSignupPass = findViewById(R.id.edtSignupPass);
        edtSignupPass = tilSignupPass.getEditText();
        btnSignUp = findViewById(R.id.btnSignup);
        loginTextView = findViewById(R.id.signupAlreadyHaveAnAccount);
        loginTextView.setOnClickListener(this::navigateToLogin);
        // Initialize Firebase components
        firebaseAuth = FirebaseAuth.getInstance();

        // SignUp button click listener
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String email = edtSignupEmail.getText().toString().trim();
                    String password = edtSignupPass.getText().toString().trim();

                    // Input validation
                    if (email.isEmpty()) {
                        tilSignupEmail.setError("Please enter your email address");
                        return; // Stop execution if name is empty
                    } else {
                        tilSignupEmail.setError(null);
                    }
                    if (email.isEmpty()) {
                        tilSignupPass.setError("Please enter your Password");
                        return; // Stop execution if name is empty
                    } else {
                        tilSignupPass.setError(null);
                    }

                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Registration successful, handle user
                                        Toast.makeText(getApplicationContext(), "Sign Up successful!", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(getApplicationContext(), AlmostThereActivity.class);
                                        startActivity(intent);
                                    } else {
                                        // Registration failed, handle error
                                        Exception e = task.getException();
                                        Toast.makeText(getApplicationContext(), "Registration failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                        Log.e("RegisterActivity", "Error registering user", e);
                                    }
                                }
                            });

                }catch (Exception ex){
                    Toast.makeText(getApplicationContext(), "Error registering user", Toast.LENGTH_LONG).show();
                    Log.e("RegisterActivity", "Error registering user", ex);
                }
            }
        });

    }
    public void navigateToLogin(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}