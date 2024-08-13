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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class AddCupcakeActivity extends AppCompatActivity {
    TextView txvCupcakeID;
    TextInputLayout tilCupcakeId,tilCupcakeName, tilCupcakeCategory, tilCupcakeDesc, tilCupcakePrice, tilCupcakeOffer;
    EditText edtCupcakeId, edtCupcakeName, edtCupcakeCategory, edtCupcakeDesc, edtCupcakePrice, edtCupcakeOffer;
    Button btnAddCupcake, ibtnEditImg, ibtnRemoveImg, ibtnBack;
    ImageView imvAddcupcakeImg;
    Bitmap CupcakeImgBitmap;
    private String imageUrl;


    FirebaseDatabase fb; // declare variable for database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_cupcake);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        SetDB();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        ////////////
        setUpViews();
        ////////////
        ActivityResultLauncher launcher=registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                        Intent intent=o.getData();
                        CupcakeImgBitmap=(Bitmap) intent.getExtras().get("data");
                        imvAddcupcakeImg.setImageBitmap(CupcakeImgBitmap);
                    }
                });

        ActivityResultLauncher launcher2=registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                        Intent intent = o.getData();
                        Uri uri = intent.getData();
                        imvAddcupcakeImg.setImageURI(uri);
                    }

                });
        ibtnEditImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(ibtnEditImg.getContext());
                builder.setMessage("Please select an option to use");
                builder.setTitle("Select an image for the Cupcake");
                builder.setPositiveButton("Use Camera", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        launcher.launch(intent);
                    }
                });

                builder.setNegativeButton("Use Gallery",
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
        ibtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ManageCupcakeActivity.class);
                intent.putExtra("refreshData", true); // Flag to indicate data refresh
                startActivity(intent);
            }
        });
        btnAddCupcake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // Get cupcake details from input fields
                    String cupcakeId = edtCupcakeId.getText().toString();
                    String cupcakeName = edtCupcakeName.getText().toString().trim();
                    String cupcakeCategory = edtCupcakeCategory.getText().toString().trim();
                    String cupcakeDescription = edtCupcakeDesc.getText().toString().trim();
                    Double cupcakePrice = Double.valueOf(edtCupcakePrice.getText().toString());
                    Double cupcakeOffers = Double.valueOf(edtCupcakeOffer.getText().toString());

                    Bitmap imageBitmap = ((BitmapDrawable) imvAddcupcakeImg.getDrawable()).getBitmap();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] cupcakeImage = baos.toByteArray();

                    // Validation: Check for empty fields or invalid values
                    if (cupcakeId.isEmpty()) {
                        tilCupcakeId.setError("Please enter a Cupcake Id");
                        return;
                    } else {
                        tilCupcakeId.setError(null);
                    }
                    if (cupcakeName.isEmpty()) {
                        tilCupcakeName.setError("Please enter a cupcake name");
                        return;
                    }else {
                        tilCupcakeName.setError(null);
                    }
                    if (cupcakeCategory.isEmpty()) {
                        tilCupcakeCategory.setError("Please enter a cupcake category");
                        return;
                    }else {
                        tilCupcakeCategory.setError(null);
                    }
                    if (cupcakeDescription.isEmpty()) {
                        tilCupcakeDesc.setError("Please enter a description ");
                        return;
                    }else {
                        tilCupcakeDesc.setError(null);
                    }

                    if (cupcakePrice <= 0) {
                        tilCupcakePrice.setError("Please enter a valid price");
                        return;
                    }else {
                        tilCupcakePrice.setError(null);
                    }

                    if (cupcakeOffers <= 0) {
                        tilCupcakeOffer.setError("Please enter a valid Offer rate");
                        return;
                    }else {
                        tilCupcakeOffer.setError(null);
                    }

                    // Create Cupcake object and add to Firebase
                    Cupcake cupcake = new Cupcake();
                    cupcake.setCupcakeID(cupcakeId);
                    cupcake.setCupcakeName(cupcakeName);
                    cupcake.setCupcakeCategory(cupcakeCategory);
                    cupcake.setCupcakeDescription(cupcakeDescription);
                    cupcake.setCupcakePrice(cupcakePrice);
                    cupcake.setCupcakeOffers(cupcakeOffers);
                    //adding image to firebase storage--
                    uploadImage(fb, cupcake, cupcakeImage, storage);


                    Toast.makeText(getApplicationContext(), "Hooray! Your cupcake has been added successfully!", Toast.LENGTH_LONG).show();

                    // Reset input fields for the next cupcake
                    edtCupcakeId.setText("");
                    edtCupcakeName.setText("");
                    edtCupcakeCategory.setText("");
                    edtCupcakeDesc.setText("");
                    edtCupcakePrice.setText("");
                    edtCupcakeOffer.setText("");
                    imvAddcupcakeImg.setImageResource(R.drawable.cupcake_placeholder);
                    Intent intent = new Intent(getApplicationContext(), ManageCupcakeActivity.class);
                    intent.putExtra("refreshData", true);
                    startActivity(intent);

                } catch (Exception ex) {
                    Toast.makeText(getApplicationContext(), "Oops! An error occurred while adding your cupcake. Please try again.", Toast.LENGTH_LONG).show();
                    Log.e("CupcakeActivity", "Error adding cupcake", ex); // Log the exception for debugging
                }
            }
        });
    }
    public void uploadImage(FirebaseDatabase fb, Cupcake cupcake, byte[] cupcakeImage, FirebaseStorage storage) {
        if (cupcakeImage != null) { // Check if there's an image to upload
            StorageReference imageRef = storage.getReference().child("cupcake/" + cupcake.getCupcakeID() + ".jpg");
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
                                        imageUrl = downloadUri.toString();
                                        cupcake.setCupcakeImage(imageUrl);
                                        cupcake.Add(fb);
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
        } else {
            // If no image, directly add the cupcake without image URL
            cupcake.Add(fb);
        }
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
    private  void setUpViews(){
        tilCupcakeId = findViewById(R.id.edtCupcakeId);
        edtCupcakeId = tilCupcakeId.getEditText();

        tilCupcakeName = findViewById(R.id.edtCupcakeName);
        edtCupcakeName = tilCupcakeName.getEditText();

        tilCupcakeCategory = findViewById(R.id.edtCupcakeCategory);
        edtCupcakeCategory = tilCupcakeCategory.getEditText();

        tilCupcakeDesc = findViewById(R.id.edtCupcakeDesc);
        edtCupcakeDesc = tilCupcakeDesc.getEditText();

        tilCupcakePrice = findViewById(R.id.edtCupcakePrice);
        edtCupcakePrice = tilCupcakePrice.getEditText();

        tilCupcakeOffer = findViewById(R.id.edtCupcakeOffer);
        edtCupcakeOffer = tilCupcakeOffer.getEditText();

        txvCupcakeID = findViewById(R.id.txvCupcakeID);
        btnAddCupcake = findViewById(R.id.btnAddCupcakeAdmin);
        ibtnEditImg = findViewById(R.id.ibtnEditCupcakeImg);
        ibtnRemoveImg = findViewById(R.id.ibtnRemoveCupcakeImg);
        ibtnBack = findViewById(R.id.ibtnBackAddCup);
        imvAddcupcakeImg = findViewById(R.id.imvAddCupcakeImg);
    }
}