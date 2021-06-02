package com.myapplicationdev.android.jobby;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {


    // Todo: declaring objects
    EditText myEmailEditText, myPasswordEditText;
    Button myLoginButton;
    TextView iForgetPasswordTextView, mySignupHereTextView;
    ProgressDialog myProgressDialog;

    // Todo: Firebase objects
    FirebaseAuth myFirebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Returns an instance of this class corresponding
        // to the default FirebaseApp instance.
        myFirebaseAuth = FirebaseAuth.getInstance();

        if (myFirebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(MainActivity.this, HomeActivity.class));
        }

        myProgressDialog = new ProgressDialog(MainActivity.this);

        loginDetails();

    }

    // Todo: Login Method
    void loginDetails() {

// Todo: Binding UI elements
        myEmailEditText = findViewById(R.id.email_login);
        myPasswordEditText = findViewById(R.id.password_login);
        myLoginButton = findViewById(R.id.btn_login);
        iForgetPasswordTextView = findViewById(R.id.forget_password);
        mySignupHereTextView = findViewById(R.id.signup_reg);


        // Todo: Login Method
        myLoginButton.setOnClickListener(view -> {

            String email = myEmailEditText.getText().toString().trim();
            String pass = myPasswordEditText.getText().toString().trim();

            // Verification for user inputs
            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)) {
                myEmailEditText.setError("This field is required...");
                return;
            }

            myProgressDialog.setMessage("Processing...");
            myProgressDialog.show();

            myFirebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {

                    myProgressDialog.dismiss();
                    startActivity(new Intent(MainActivity.this, HomeActivity.class));
                    Toast.makeText(MainActivity.this, "Login successful...", Toast.LENGTH_SHORT).show();
                } else {
                    myProgressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Login failed...", Toast.LENGTH_SHORT).show();

                }
            });

        });

        //Todo:  Registration activity

        mySignupHereTextView.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, RegistrationActivity.class)));

        //Todo: Reset password activity..

        iForgetPasswordTextView.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, ResetActivity.class)));

    }


}
