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

import com.bumptech.glide.Glide;
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

public class UpdateCupcakeActivity extends AppCompatActivity {

    TextView txvCupcakeID;
    TextInputLayout tilCupcakeId,tilCupcakeName, tilCupcakeCategory, tilCupcakeDesc, tilCupcakePrice, tilCupcakeOffer;
    EditText edtCupcakeId, edtCupcakeName, edtCupcakeCategory, edtCupcakeDesc, edtCupcakePrice, edtCupcakeOffer;
    Button btnUpdateCupcake, ibtnEditImg, ibtnRemoveImg, ibtnBack;
    ImageView imvUpdatecupcakeImg;
    Bitmap CupcakeImgBitmap;

    FirebaseDatabase fb;
    private StorageReference storageRef; // Initialize storage reference

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_cupcake);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //Assigning views
        setUpViews();
        //
        SetDB();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference(); // Get a reference to the root
        ////////////
        /// Retrieve Data from intent

        Intent intent = getIntent();
        String Id = intent.getStringExtra("id");
        String name = intent.getStringExtra("name");
        String category = intent.getStringExtra("category");
        String description = intent.getStringExtra("description");
        Double price = intent.getDoubleExtra("price", 0);
        Double offer = intent.getDoubleExtra("offer", 0);
        String imageUrl = intent.getStringExtra("image");
        ////
        txvCupcakeID.setText(Id);
        edtCupcakeId.setText(Id);
        edtCupcakeName.setText(name);
        edtCupcakeCategory.setText(category);
        edtCupcakeDesc.setText(description);
        edtCupcakePrice.setText(String.valueOf(price));
        edtCupcakeOffer.setText(String.valueOf(offer));

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .into(imvUpdatecupcakeImg);
        } else {
            // Set a placeholder image if the URL is not available
            imvUpdatecupcakeImg.setImageResource(R.drawable.ic_launcher_background);
        }
        ////
        System.out.println(Id);
        System.out.println(imageUrl);
        System.out.println(price);

        ActivityResultLauncher launcher=registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                        Intent intent=o.getData();
                        CupcakeImgBitmap=(Bitmap) intent.getExtras().get("data");
                        imvUpdatecupcakeImg.setImageBitmap(CupcakeImgBitmap);
                    }
                });

        ActivityResultLauncher launcher2=registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                        Intent intent = o.getData();
                        Uri uri = intent.getData();
                        imvUpdatecupcakeImg.setImageURI(uri);
                    }

                });
        ibtnEditImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(ibtnEditImg.getContext());
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
        //
        ibtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ManageCupcakeActivity.class);
                intent.putExtra("refreshData", true); // Flag to indicate data refresh
                startActivity(intent);
            }
        });
        //
        btnUpdateCupcake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
//                     Get cupcake details from input fields
                    String cupcakeId = Id;
                    String cupcakeName = edtCupcakeName.getText().toString().trim();
                    String cupcakeCategory = edtCupcakeCategory.getText().toString().trim();
                    String cupcakeDescription = edtCupcakeDesc.getText().toString().trim();
                    Double cupcakePrice = Double.valueOf(edtCupcakePrice.getText().toString());
                    Double cupcakeOffers = Double.valueOf(edtCupcakeOffer.getText().toString());

                    // Extract image data (if a new image is provided)
                    byte[] cupcakeImage = null;
                    if (imvUpdatecupcakeImg.getDrawable() != null) {
                        Bitmap imageBitmap = ((BitmapDrawable) imvUpdatecupcakeImg.getDrawable()).getBitmap();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        cupcakeImage = baos.toByteArray();
                    }

                    // Validation: Check for empty fields or invalid values
                    if (cupcakeId.isEmpty()) {
                        tilCupcakeId.setError("Please enter Previous Cupcake Id");
                        return; // Stop execution if name is empty
                    } else {
                        tilCupcakeId.setError(null);
                    }
                    if (cupcakeName.isEmpty()) {
                        tilCupcakeName.setError("Please enter a cupcake name");
                        return; // Stop execution if name is empty
                    } else {
                        tilCupcakeName.setError(null);
                    }
                    if (cupcakeCategory.isEmpty()) {
                        tilCupcakeCategory.setError("Please enter a cupcake category");
                        return; // Stop execution if name is empty
                    }else {
                        tilCupcakeCategory.setError(null);
                    }
                    if (cupcakeDescription.isEmpty()) {
                        tilCupcakeDesc.setError("Please enter a description ");
                        return; // Stop execution if name is empty
                    }else {
                        tilCupcakeDesc.setError(null);
                    }

                    if (cupcakePrice <= 0) {
                        tilCupcakePrice.setError("Please enter a valid price");
                        return; // Stop execution if price is invalid
                    }else {
                        tilCupcakePrice.setError(null);
                    }

                    if (cupcakeOffers <= 0) {
                        tilCupcakeOffer.setError("Please enter a valid Offer rate");
                        return; // Stop execution if price is invalid
                    }else {
                        tilCupcakeOffer.setError(null);
                    }


                    Cupcake cupcake = new Cupcake();
                    cupcake.setCupcakeID(cupcakeId);
                    cupcake.setCupcakeName(cupcakeName);
                    cupcake.setCupcakeCategory(cupcakeCategory);
                    cupcake.setCupcakeDescription(cupcakeDescription);
                    cupcake.setCupcakePrice(cupcakePrice);
                    cupcake.setCupcakeOffers(cupcakeOffers);

                    // Update image in Firebase Storage if a new image is provided
                    if (cupcakeImage != null) {
                        StorageReference imageRef = storage.getReference().child("cupcakes/" + cupcakeId + "updated" + ".jpg"); // Use cupcake ID as filename
                        imageRef.putBytes(cupcakeImage)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        // Get the updated image URL
                                        Task<Uri> downloadUrlTask = taskSnapshot.getStorage().getDownloadUrl();
                                        downloadUrlTask.addOnCompleteListener(new OnCompleteListener<Uri>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Uri> task) {
                                                if (task.isSuccessful()) {
                                                    Uri downloadUri = task.getResult();
                                                    cupcake.setCupcakeImage(downloadUri.toString()); // Set image URL in Cupcake object
                                                    System.out.println(cupcake.getCupcakeID());
                                                    System.out.println(cupcake.getCupcakeImage());
                                                    // Update cupcake in Firebase Realtime Database
                                                    cupcake.Update(fb, getApplicationContext());

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
                                        // Handle image upload failure
                                        Log.e("UpdateCupcakeActivity", "Error updating image", e);
                                        Toast.makeText(getApplicationContext(), "Error updating image. Please try again.", Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                    // Create a Cupcake object with updated details





                    Toast.makeText(getApplicationContext(), "Cupcake updated successfully!", Toast.LENGTH_LONG).show();

                    // Clear input fields for the next cupcake
//                    edtCupcakeName.setText("");
//                    edtCupcakeCategory.setText("");
//                    edtCupcakeDesc.setText("");
//                    edtCupcakePrice.setText("");
//                    edtCupcakeOffer.setText("");
//                    imvUpdatecupcakeImg.setImageResource(R.drawable.ic_launcher_background);// Consider also resetting the image view
                    Intent intent = new Intent(getApplicationContext(), ManageCupcakeActivity.class);
                    intent.putExtra("refreshData", true); // Flag to indicate data refresh
                    startActivity(intent);
                    finish();

                } catch (Exception ec) {
                    Log.e("UpdateCupcakeActivity", "Error updating cupcake", ec); // Log exception details
                    Toast.makeText(getApplicationContext(), "Error updating your Cupcake!", Toast.LENGTH_LONG).show();
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
    private  void setUpViews(){
        tilCupcakeId = findViewById(R.id.edtCupcakeIdUpd);
        edtCupcakeId = tilCupcakeId.getEditText();

        tilCupcakeName = findViewById(R.id.edtCupcakeNameUpd);
        edtCupcakeName = tilCupcakeName.getEditText();

        tilCupcakeCategory = findViewById(R.id.edtCupcakeCategoryUpd);
        edtCupcakeCategory = tilCupcakeCategory.getEditText();

        tilCupcakeDesc = findViewById(R.id.edtCupcakeDescUpd);
        edtCupcakeDesc = tilCupcakeDesc.getEditText();

        tilCupcakePrice = findViewById(R.id.edtCupcakePriceUpd);
        edtCupcakePrice = tilCupcakePrice.getEditText();

        tilCupcakeOffer = findViewById(R.id.edtCupcakeOfferUpd);
        edtCupcakeOffer = tilCupcakeOffer.getEditText();

        txvCupcakeID = findViewById(R.id.txvCupcakeIDUpd);
        btnUpdateCupcake = findViewById(R.id.btnUpdateCupcakeAdmin);
        ibtnEditImg = findViewById(R.id.ibtnEditCupcakeImgUpd);
        ibtnRemoveImg = findViewById(R.id.ibtnRemoveCupcakeImgUpd);
        ibtnBack = findViewById(R.id.ibtnBackUpdateCup);
        imvUpdatecupcakeImg = findViewById(R.id.imvUpdateCupcakeImg);
    }
}