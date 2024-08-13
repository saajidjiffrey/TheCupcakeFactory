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

public class UploadProfileActivity extends AppCompatActivity {
    ImageView imvUserProfile;
    Button ibtnEdit, ibtnRemove, btnFinish, btnSkip, btnBack;
    Bitmap userImgBitmap;
    private String userProfileUrl;
    FirebaseDatabase fb;
    private DatabaseReference userRef;
    private FirebaseAuth firebaseAuth;
    private FirebaseStorage storage;

    private String userIdFB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_upload_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        SetDB();
        storage = FirebaseStorage.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        userIdFB = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference("users").child(userIdFB);
        //
        imvUserProfile = findViewById(R.id.imvUserProfile);
        ibtnEdit = findViewById(R.id.ibtnEditUserProfile);
        ibtnRemove = findViewById(R.id.ibtnRemoveUserProfile);
        btnFinish = findViewById(R.id.btnFinish);
        btnSkip = findViewById(R.id.btnSkip);
        btnBack = findViewById(R.id.ibtnBackUserProfile);
        /// Retrieve Data from intent

        Intent intent = getIntent();

//        String fullName = intent.getStringExtra("fullName");
//        int phone = Integer.parseInt(String.valueOf(intent.getIntExtra("phone", 0)));
//        String email = intent.getStringExtra("email");
//        String birthday = intent.getStringExtra("birthday");
//        String userId = intent.getStringExtra("userid");

        ////
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
        /////
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                User user = new User();
//                user.setUserId(userId);
//                user.setUserFullName(fullName);
//                user.setUserPhone(phone);
//                user.setUserBirthday(birthday);
//                user.setUserEmail(email);
                //
                Bitmap imageBitmap = ((BitmapDrawable) imvUserProfile.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] userImage = baos.toByteArray();

                //adding image to firebase storage--
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
//                                            user.setUserImage(userProfileUrl); // Set image URL in Cupcake object


                                            // Store user data in the database
//                                            user.AddUser(fb);
                                            Intent intent = new Intent(UploadProfileActivity.this, MainActivity.class);
//                                            intent.putExtra("fullName", fullName);
//                                            intent.putExtra("phone", phone);
//                                            intent.putExtra("birthday", birthday);
//                                            intent.putExtra("email", email);
//                                            intent.putExtra("userid", userId);
//                                            intent.putExtra("imageURL", userProfileUrl);
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



                Toast.makeText(getApplicationContext(), "Hooray! Your cupcake has been added successfully!", Toast.LENGTH_LONG).show();
            }
        });
        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                User user = new User();
//                user.setUserId(userId);
//                user.setUserFullName(fullName);
//                user.setUserPhone(phone);
//                user.setUserBirthday(birthday);
//                user.setUserEmail(email);
//
//                user.AddUser(fb);

                Intent intent = new Intent(UploadProfileActivity.this, MainActivity.class);
//                intent.putExtra("fullName", fullName);
//                intent.putExtra("phone", phone);
//                intent.putExtra("birthday", birthday);
//                intent.putExtra("email", email);
//                intent.putExtra("userid", userId);
                startActivity(intent);
            }
        });
    }
    private void SetDB(){
        try {
            fb = FirebaseDatabase.getInstance(); //assign the database instance for the variable

        }
        catch (Exception ex)
        {
            Toast.makeText(getApplicationContext(), "Error occurred in Database" + ex.getMessage(), Toast.LENGTH_LONG).show();

        }
    }
}