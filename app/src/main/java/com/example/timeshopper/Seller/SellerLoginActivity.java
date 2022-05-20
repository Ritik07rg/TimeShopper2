package com.example.timeshopper.Seller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.timeshopper.Prevalent.Prevalent;
import com.example.timeshopper.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import io.paperdb.Paper;

public class SellerLoginActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private Button loginSellerButton;
    private ProgressDialog loadingBar;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_login);

        emailInput = findViewById(R.id.seller_login_email);
        passwordInput = findViewById(R.id.seller_login_password);
        loginSellerButton = findViewById(R.id.seller_login_btn);
        loadingBar = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();


        loginSellerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                loginSeller();
            }
        });




        Paper.init(this);
    }

    private void loginSeller()
    {
        final String email = emailInput.getText().toString();
        final String password = passwordInput.getText().toString();

        if (!email.equals("") && !password.equals(""))
        {
            loadingBar.setTitle("Login Account");
            loadingBar.setMessage("Please wait, while we are checking the credentials!");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if (task.isSuccessful())
                            {

                                Paper.book().write(Prevalent.SellerEmailKey, email);
                                Paper.book().write(Prevalent.SellerPasswordKey, password);



                                Intent intent = new Intent(SellerLoginActivity.this, SellerHomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                                loadingBar.dismiss();
                            }
                        }
                    });

        }
        else
        {
            Toast.makeText(this, "Please fill up all the fields!", Toast.LENGTH_SHORT).show();
        }

   }
}
