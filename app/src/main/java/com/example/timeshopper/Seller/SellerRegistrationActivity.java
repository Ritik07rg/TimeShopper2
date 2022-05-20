package com.example.timeshopper.Seller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.timeshopper.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.util.HashMap;

public class SellerRegistrationActivity extends AppCompatActivity {

    private Button sellerLoginBegin, registerButton;
    private EditText nameInput, phoneInput, emailInput, passwordInput, addressInput;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    private StorageReference SellerImagesRef;
    private DatabaseReference rootRef;
    private ImageView sellerImage;
    private static final int GalleryPick = 2;
    private Uri ImageUri;
    private String downloadImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_registration);

        mAuth = FirebaseAuth.getInstance();

        sellerLoginBegin = findViewById(R.id.seller_already_account_btn);
        nameInput = findViewById(R.id.seller_name);
        phoneInput = findViewById(R.id.seller_phone);
        emailInput = findViewById(R.id.seller_email);
        passwordInput = findViewById(R.id.seller_password);
        addressInput = findViewById(R.id.seller_address);
        registerButton = findViewById(R.id.seller_register_btn);


        /////////

        // Store Image Reference
        SellerImagesRef = FirebaseStorage.getInstance().getReference().child("Seller Images");


        sellerImage = findViewById(R.id.img_seller);

        loadingBar = new ProgressDialog(this);

        sellerLoginBegin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(SellerRegistrationActivity.this, SellerLoginActivity.class);
                startActivity(intent);
            }
        });

//////////////

        sellerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });



        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                registerSeller();
            }
        });
    }


    //////////////
    private void OpenGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GalleryPick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GalleryPick && resultCode == RESULT_OK && data != null){
            ImageUri = data.getData();
            sellerImage.setImageURI(ImageUri);
        }
    }




    private void registerSeller()
    {
        final String name = nameInput.getText().toString();
        final String phone = phoneInput.getText().toString();
        final String email = emailInput.getText().toString();
        final String password = passwordInput.getText().toString();
        final String address = addressInput.getText().toString();


        if (!name.equals("") && !phone.equals("") && !email.equals("") && !password.equals("") && !address.equals("") && !(ImageUri == null))
        {
            loadingBar.setTitle("Creating Seller Account");
            loadingBar.setMessage("Please wait, while we are checking the credentials!");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();



            // Image Name and filePath
            final StorageReference filePath = SellerImagesRef.child(ImageUri.getLastPathSegment() + 123 + ".jpg");

            // Uploading Image into the StorageSection of the database
            final UploadTask uploadTask = filePath.putFile(ImageUri);

            // On Failure
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    String message = e.toString();
                    Toast.makeText(SellerRegistrationActivity.this, "Error" + message, Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(SellerRegistrationActivity.this, "Product Image uploaded Successfully!", Toast.LENGTH_SHORT).show();

                    // After that we have to get the link of the product to store into the database
                    Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if(!task.isSuccessful())
                            {
                                throw task.getException();
                            }

                            // We get the link of that image for our database
                            downloadImageUrl = filePath.getDownloadUrl().toString();
                            return filePath.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()){

                                downloadImageUrl = task.getResult().toString();

                                Toast.makeText(SellerRegistrationActivity.this, "Got the link of the product Successfully!", Toast.LENGTH_SHORT).show();
                                // After that saving the product details into the database




                                mAuth.createUserWithEmailAndPassword(email,password)
                                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task)
                                            {
                                                if (task.isSuccessful())
                                                {
                                                    // Save the user Info into the database


                                                    rootRef = FirebaseDatabase.getInstance().getReference();

                                                    // Unique id for all seller
                                                    String sid = mAuth.getCurrentUser().getUid();

                                                    HashMap<String, Object> sellerMap = new HashMap<>();
                                                    sellerMap.put("sid", sid);
                                                    sellerMap.put("name", name);
                                                    sellerMap.put("phone", phone);
                                                    sellerMap.put("email", email);
                                                    sellerMap.put("password", password);
                                                    sellerMap.put("address", address);
                                                    sellerMap.put("image", downloadImageUrl);

                                                    rootRef.child("Sellers").child(sid).updateChildren(sellerMap)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task)
                                                                {
                                                                    loadingBar.dismiss();
                                                                    Toast.makeText(SellerRegistrationActivity.this, "You are registered successfully!", Toast.LENGTH_SHORT).show();

                                                                    Intent intent = new Intent(SellerRegistrationActivity.this, SellerHomeActivity.class);
                                                                    startActivity(intent);
                                                                }
                                                            });
                                                }
                                            }
                                        });




                            }
                        }
                    });
                }
            });





        }
        else
        {
            Toast.makeText(this, "Please fill up all the fields!", Toast.LENGTH_SHORT).show();
        }
    }
}
