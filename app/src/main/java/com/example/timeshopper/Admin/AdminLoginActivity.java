package com.example.timeshopper.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.timeshopper.Model.Admin;
import com.example.timeshopper.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminLoginActivity extends AppCompatActivity {

    private EditText adminEmail, adminPassword;
    private Button adminLoginBtn;
    private ProgressDialog loadingBar;
    //private String parentDbName = "Admins";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        adminEmail = findViewById(R.id.admin_login_email);
        adminPassword = findViewById(R.id.admin_login_password);
        adminLoginBtn = findViewById(R.id.admin_login_btn);
        loadingBar = new ProgressDialog(AdminLoginActivity.this);

        adminLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                LoginUser();
            }
        });

    }

    private void LoginUser() {
        String email = adminEmail.getText().toString();
        String password = adminPassword.getText().toString();

        if (TextUtils.isEmpty(email))
        {
            Toast.makeText(this, "Please enter your phone number!", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Please enter your Password!", Toast.LENGTH_SHORT).show();
        }
        else
        {

            loadingBar.setTitle("Login Account");
            loadingBar.setMessage("Please wait while we are checking the credentials.");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            AllowAccessToAccount(email, password);
        }
    }

    private void AllowAccessToAccount(final String email, final String password){

        // Getting reference
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Checking user exists or not
                if (dataSnapshot.child("Admins").exists()){
                    // Passing value in the Users class
                    Admin adminData = dataSnapshot.child("Admins").getValue(Admin.class);

                    if (adminData.getEmail().equals(email))
                    {
                        if (adminData.getPassword().equals(password))
                        {
                            // Checking whether it is admin or user
                           //if (parentDbName.equals("Admins")){

                                Toast.makeText(AdminLoginActivity.this, "Welcome Admin, You are Logged in successfully!", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                                Intent intent = new Intent(AdminLoginActivity.this, AdminHomeActivity.class);
                                startActivity(intent);
                           // }


                        }
                        else
                        {
                            loadingBar.dismiss();
                            Toast.makeText(AdminLoginActivity.this, "Invalid Number/Password!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
