package com.example.thecupcakefactory;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AlmostThereActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseStorage storage;
    private DatabaseReference userRef;
    TextInputLayout tilFullName, tilPhone, tilBirthday ;
    EditText edtFullName, edtPhone, edtBirthday ;
    Button btnAlmostContinue,ibtnEdit, ibtnRemove;
    ImageView imvUserProfile;
    Bitmap userImgBitmap;
    private String userIdFB, userEmail;
    private String userProfileUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_almost_there);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        tilFullName = findViewById(R.id.edtFullname);
        edtFullName = tilFullName.getEditText();
        tilPhone = findViewById(R.id.edtPhone);
        edtPhone = tilPhone.getEditText();
        tilBirthday = findViewById(R.id.edtBirthday);
        edtBirthday = tilBirthday.getEditText();
        btnAlmostContinue = findViewById(R.id.btnAlmostContinue);
        imvUserProfile = findViewById(R.id.imvUserProfile);
        ibtnEdit = findViewById(R.id.ibtnEditUserProfile);
        ibtnRemove = findViewById(R.id.ibtnRemoveUserProfile);
        // Initialize Firebase components
        firebaseAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        userIdFB = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        userRef = FirebaseDatabase.getInstance().getReference("users").child(userIdFB);
        //
        ActivityResultLauncher launcher=registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                        Intent intent=o.getData();
                        userImgBitmap=(Bitmap) intent.getExtras().get("data");
                        imvUserProfile.setImageBitmap(userImgBitmap);
                    }
                });

        ActivityResultLauncher launcher2=registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                        Intent intent = o.getData();
                        Uri uri = intent.getData();
                        imvUserProfile.setImageURI(uri);

                    }

                });
        ibtnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(ibtnEdit.getContext());
                builder.setMessage("Please select the option you with to use");
                builder.setTitle("Select a book image");
                builder.setPositiveButton("Use the camera", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        launcher.launch(intent);
                    }
                });

                builder.setNegativeButton("Use the gallery",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                                intent.setType("image/");
                                launcher2.launch(intent);
                            }
                        });
                AlertDialog dialog= builder.create();
                dialog.show();
            }
        });
        // Button Listener
        btnAlmostContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String fullName = edtFullName.getText().toString().trim();
                    int phone = Integer.parseInt(edtPhone.getText().toString().trim());
                    String birthday = edtBirthday.getText().toString().trim();

                    // Input validation (optional)
                    if (fullName.isEmpty()) {
                        tilFullName.setError("Please enter your Full Name");
                        return; // Stop execution if name is empty
                    } else {
                        tilFullName.setError(null);
                    }
                    if (phone <= 0) {
                        tilPhone.setError("Please enter your email Phone Number");
                        return; // Stop execution if name is empty
                    } else {
                        tilPhone.setError(null);
                    }
                    if (birthday.isEmpty()) {
                        tilBirthday.setError("Please enter your Birthday");
                        return; // Stop execution if name is empty
                    } else {
                        tilBirthday.setError(null);
                    }

                    Bitmap imageBitmap = ((BitmapDrawable) imvUserProfile.getDrawable()).getBitmap();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] userImage = baos.toByteArray();
                    //
                    StorageReference imageRef = storage.getReference().child("user/" + UUID.randomUUID().toString()); // Generate unique filename
                    imageRef.putBytes(userImage)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    // Get the uploaded image URL after successful upload
                                    Task<Uri> downloadUrlTask = taskSnapshot.getStorage().getDownloadUrl();
                                    downloadUrlTask.addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            if (task.isSuccessful()) {
                                                Uri downloadUri = task.getResult();
                                                userProfileUrl = downloadUri.toString();
                                                System.out.println("Profile Picture : " + userProfileUrl);
                                                Map<String, Object> userData = new HashMap<>();
                                                userData.put("email", userEmail);
                                                userData.put("fullname",fullName);
                                                userData.put("phone", phone);
                                                userData.put("birthday", birthday);
                                                userData.put("userProfileUrl",userProfileUrl);

                                                userRef.setValue(userData)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Log.d("TAG", "User data saved successfully!");
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Log.w("TAG", "Error saving user data", e);
                                                            }
                                                        });

                                                Intent intent = new Intent(AlmostThereActivity.this, MainActivity.class);
                                                startActivity(intent);

                                            } else {
                                                // Handle image upload failure
                                                Toast.makeText(getApplicationContext(), "Image upload failed. Please try again.", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Handle image upload error
                                    Toast.makeText(getApplicationContext(), "Error uploading image. Please try again.", Toast.LENGTH_LONG).show();
                                    Log.e("CupcakeActivity", "Error uploading image", e);
                                }
                            });
                    Toast.makeText(getApplicationContext(), "User Added successfully!", Toast.LENGTH_LONG).show();



                } catch(Exception e) {
                Toast.makeText(getApplicationContext(), "Error storing user data", Toast.LENGTH_LONG).show();
                Log.e("AlmostThereActivity", "Error storing user data", e);
            }
            }
        });
    }

}

