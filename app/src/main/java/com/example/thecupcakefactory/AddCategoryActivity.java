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
import android.widget.TextView;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AddCategoryActivity extends AppCompatActivity {
    TextView txvCatID;
    TextInputLayout tilCatId,tilCatName, tilCatDesc;
    EditText edtCatId, edtCatName, edtCatDesc;
    Button btnAddCat, ibtnEditImgCat, ibtnRemoveImgCat, ibtnBackCat;
    ImageView imvAddCatImg;
    Bitmap catImgBitmap;
    public String userProfileUrl;


    FirebaseDatabase fb; // declare variable for database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_category);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        SetDB();
        FirebaseStorage storage = FirebaseStorage.getInstance();

        ////////////
        setupViews();
        //end of view assigning
        ////////////
        ActivityResultLauncher launcher=registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                        Intent intent=o.getData();
                        catImgBitmap=(Bitmap) intent.getExtras().get("data");
                        imvAddCatImg.setImageBitmap(catImgBitmap);
                    }
                });

        ActivityResultLauncher launcher2=registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                        Intent intent = o.getData();
                        Uri uri = intent.getData();
                        imvAddCatImg.setImageURI(uri);
                    }

                });
        ibtnEditImgCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(ibtnEditImgCat.getContext());
                builder.setMessage("Please select the option to use");
                builder.setTitle("Select a Category image");
                builder.setPositiveButton("Use camera", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        launcher.launch(intent);
                    }
                });

                builder.setNegativeButton("Use gallery",
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
        ibtnBackCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnAddCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // Get category details from input fields
                    String catId = edtCatId.getText().toString().trim();
                    String catName = edtCatName.getText().toString().trim();
                    String catDescription = edtCatDesc.getText().toString().trim();

                    // Extract image data (assuming a Bitmap is available from imvAddcupcakeImg)
                    Bitmap imageBitmap = ((BitmapDrawable) imvAddCatImg.getDrawable()).getBitmap();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] cupcakeImage = baos.toByteArray();

                    // Validation: Check for empty fields or invalid values
                    if (catId.isEmpty()) {
                        tilCatId.setError("Please enter a Category id");
                        return; // Stop execution if name is empty
                    }else {
                        tilCatId.setError(null);
                    }

                    if (catName.isEmpty()) {
                        tilCatName.setError("Please enter a Category name");
                        return; // Stop execution if name is empty
                    }else {
                        tilCatName.setError(null);
                    }

                    if (catDescription.isEmpty()) {
                        tilCatDesc.setError("Please enter a description ");
                        return; // Stop execution if name is empty
                    }else {
                        tilCatDesc.setError(null);
                    }

                    // Create Cupcake object and add to Firebase
                    Category category = new Category();
                    category.setCategoryID(catId);
                    category.setCategoryName(catName);
                    category.setCategoryDescription(catDescription);

                    //adding image to firebase storage--
                    uploadCupcake(storage,cupcakeImage,catId,catName, catDescription );

//                    StorageReference imageRef = storage.getReference().child("categories/" + UUID.randomUUID().toString()); // Generate unique filename
//                    imageRef.putBytes(cupcakeImage)
//                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                                @Override
//                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                                    // Get the uploaded image URL after successful upload
//                                    Task<Uri> downloadUrlTask = taskSnapshot.getStorage().getDownloadUrl();
//                                    downloadUrlTask.addOnCompleteListener(new OnCompleteListener<Uri>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<Uri> task) {
//                                            if (task.isSuccessful()) {
//                                                Uri downloadUri = task.getResult();
//                                                category.setCategoryImage(downloadUri.toString()); // Set image URL in Cupcake object
//
//                                                // Add cupcake to Firebase Realtime Database with the image URL
//                                                category.Add(fb);
//
//
//                                            } else {
//                                                // Handle image upload failure
//                                                Toast.makeText(getApplicationContext(), "Image upload failed. Please try again.", Toast.LENGTH_LONG).show();
//                                            }
//                                        }
//                                    });
//                                }
//                            })
//                            .addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    // Handle image upload error
//                                    Toast.makeText(getApplicationContext(), "Error uploading image. Please try again.", Toast.LENGTH_LONG).show();
//                                    Log.e("CupcakeActivity", "Error uploading image", e);
//                                }
//                            });
                    Toast.makeText(getApplicationContext(), "Hooray! Your Category has been added successfully!", Toast.LENGTH_LONG).show();

                    // Reset input fields for the next cupcake
                    edtCatId.setText("");
                    edtCatName.setText("");
                    edtCatDesc.setText("");
                    imvAddCatImg.setImageResource(R.drawable.ic_launcher_background);// Consider also resetting the image view

                } catch (Exception ex) {
                    Toast.makeText(getApplicationContext(), "Oops! An error occurred while adding your Category. Please try again.", Toast.LENGTH_LONG).show();
                    Log.e("CupcakeActivity", "Error adding Category.", ex); // Log the exception for debugging
                }
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
    private void setupViews(){
        tilCatId = findViewById(R.id.edtCatId);
        edtCatId = tilCatId.getEditText();

        tilCatName = findViewById(R.id.edtCatName);
        edtCatName = tilCatName.getEditText();

        tilCatDesc = findViewById(R.id.edtCatDesc);
        edtCatDesc = tilCatDesc.getEditText();

        txvCatID = findViewById(R.id.txvCatID);
        btnAddCat= findViewById(R.id.btnAddCatAdmin);
        ibtnEditImgCat = findViewById(R.id.ibtnEditCatImg);
        ibtnRemoveImgCat = findViewById(R.id.ibtnRemoveCatImg);
        ibtnBackCat = findViewById(R.id.ibtnBackAddCat );
        imvAddCatImg = findViewById(R.id.imvAddCatImg);
    }

    private void uploadCupcake(FirebaseStorage storage,byte[] cupcakeImage, String catId, String catName, String catDescription ){
        DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference("category").child(catId);
        StorageReference imageRef = storage.getReference().child("categories/" + UUID.randomUUID().toString()); // Generate unique filename
        imageRef.putBytes(cupcakeImage)
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
                                    userData.put("categoryId",catId);
                                    userData.put("categoryName", catName);
                                    userData.put("categoryDesc", catDescription);
                                    userData.put("userProfileUrl",userProfileUrl);
                                    categoryRef.setValue(userData)
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


                                } else {

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
    }
}