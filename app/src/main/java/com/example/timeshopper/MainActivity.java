package com.example.timeshopper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.timeshopper.Admin.AdminLoginActivity;
import com.example.timeshopper.Buyer.HomeActivity;
import com.example.timeshopper.Prevalent.Prevalent;
import com.example.timeshopper.Seller.SellerHomeActivity;
import com.example.timeshopper.Seller.SellerRegistrationActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hbb20.CountryCodePicker;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    private CountryCodePicker ccp;
    private EditText phoneText;
    private EditText codeText;
    private Button continueAndNextBtn;
    private String checker = "", phoneNumber = "";
    private RelativeLayout relativeLayout;
    private TextView AdminLogin, SellerLogin;

    // Variable for phone Authentication
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private String mVerification;
    private ProgressDialog loadingBar;
    private DatabaseReference userRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Paper.init(this);


        userRef = FirebaseDatabase.getInstance().getReference().child("Users");

        mAuth = FirebaseAuth.getInstance();
        phoneText = findViewById(R.id.phoneText);
        codeText = findViewById(R.id.codeText);
        continueAndNextBtn = findViewById(R.id.continueNextButton);
        relativeLayout = findViewById(R.id.phoneAuth);

        AdminLogin = findViewById(R.id.admin_panel_link);
        SellerLogin = findViewById(R.id.seller_panel_link);

        loadingBar = new ProgressDialog(this);
        ccp = findViewById(R.id.ccp);
        // Register number (editText with country code)
        ccp.registerCarrierNumberEditText(phoneText);

        continueAndNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (continueAndNextBtn.getText().equals("Submit") || checker.equals("Code sent"))
                {
                    String verificationCode = codeText.getText().toString();
                    if (verificationCode.equals(""))
                    {
                        Toast.makeText(MainActivity.this, "Please enter verification code!", Toast.LENGTH_SHORT).show();
                    } else
                        {

                        loadingBar.setTitle("Code Verification");
                        loadingBar.setMessage("Please wait, while we are verifying your code!");
                        loadingBar.setCanceledOnTouchOutside(false);
                        loadingBar.show();

                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerification, verificationCode);
                        signInWithPhoneAuthCredential(credential);
                    }
                } else {
                    phoneNumber = ccp.getFullNumberWithPlus();
                    if (!phoneNumber.equals("")) {
                        // Now verification process
                        loadingBar.setTitle("Phone Number Verification");
                        loadingBar.setMessage("Please wait, while we are verifying your number!");
                        loadingBar.setCanceledOnTouchOutside(false);
                        loadingBar.show();

                        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber, 60, TimeUnit.SECONDS,MainActivity.this,mCallbacks);


                    } else {
                        Toast.makeText(MainActivity.this, "Please write valid phone number", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(MainActivity.this, "Invalid Phone number", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
                relativeLayout.setVisibility(View.VISIBLE);

                //
                continueAndNextBtn.setText("Continue");
                codeText.setVisibility(View.GONE);
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

                mVerification = s;
                mResendToken = forceResendingToken;

                relativeLayout.setVisibility(View.GONE);
                checker = "Code Sent";
                continueAndNextBtn.setText("Submit");
                codeText.setVisibility(View.VISIBLE);

                loadingBar.dismiss();
                Toast.makeText(MainActivity.this, "Code has been sent to your number!", Toast.LENGTH_SHORT).show();
            }
        };

        // Admin Login Activity

        AdminLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(MainActivity.this, AdminLoginActivity.class);
                startActivity(intent);
            }
        });

        // Seller Login Activity

        SellerLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(MainActivity.this, SellerRegistrationActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        // No need to verification again and again
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        String SellerEmailKey = Paper.book().read(Prevalent.SellerEmailKey);
        String SellerPasswordKey = Paper.book().read(Prevalent.SellerPasswordKey);

        if (!(SellerEmailKey == null) && !(SellerPasswordKey == null)){
            // Confirm again it is empty or not
            loadingBar.setTitle("Already logged in!");
            loadingBar.setMessage("Please Wait...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            mAuth.signInWithEmailAndPassword(SellerEmailKey, SellerPasswordKey)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if (task.isSuccessful())
                            {

                                Intent intent = new Intent(MainActivity.this, SellerHomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                                loadingBar.dismiss();
                            }
                        }
                    });

        }else if (firebaseUser != null){

                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }

    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            HashMap<String, Object> userMap = new HashMap<>();
                            userMap.put("phone", phoneNumber);

                            userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                   .updateChildren(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if (task.isSuccessful())
                                    {
                                        loadingBar.dismiss();
                                        Toast.makeText(MainActivity.this, "Congratulation, Your account created successfully!", Toast.LENGTH_SHORT).show();
                                        sendUserToMainActivity();
                                    }
                                }
                            });

                        } else {
                            loadingBar.dismiss();
                            Toast.makeText(MainActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void sendUserToMainActivity() {
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

}
