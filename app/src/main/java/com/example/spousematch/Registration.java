package com.example.spousematch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.SignInMethodQueryResult;

public class Registration extends AppCompatActivity {

    private EditText emailTextView, passwordTextView, confirmPasswordTextView;
    private Button submitButton;
    private ProgressBar progressbar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();

        emailTextView = findViewById(R.id.email);
        passwordTextView = findViewById(R.id.passwd);
        confirmPasswordTextView = findViewById(R.id.confirmPasswd);
        submitButton = findViewById(R.id.btnregister);
        progressbar = findViewById(R.id.progressbar);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    registerNewUser();
                } catch (FirebaseAuthException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void registerNewUser() throws FirebaseAuthException {
        progressbar.setVisibility(View.VISIBLE);
        String email,password,confirmPassword;

        email = emailTextView.getText().toString();
        password = passwordTextView.getText().toString();
        confirmPassword = confirmPasswordTextView.getText().toString();

        Integer errors = 0;

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(),
                            "Please enter your email!",
                            Toast.LENGTH_LONG)
                    .show();
            errors++;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(),
                            "Please enter your password!",
                            Toast.LENGTH_LONG)
                    .show();
            errors++;
        }
        if (TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(getApplicationContext(),
                            "Please confirm your password!",
                            Toast.LENGTH_LONG)
                    .show();
            errors++;
        }

        if (!confirmPassword.equals(password)){
            Toast.makeText(getApplicationContext(),
                    "Passwords don't match",
                    Toast.LENGTH_LONG)
                    .show();
            errors++;
        }

        if(errors == 0) {
                mAuth
                    .createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                progressbar.setVisibility(View.GONE);
                                mAuth.getCurrentUser().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Intent intent = new Intent(Registration.this, CompleteRegistration.class);
                                        intent.putExtra("email", email);
                                        intent.putExtra("password", password);
                                        startActivity(intent);
                                    }
                                });
                            } else {
                                Toast.makeText(getApplicationContext(),
                                                "There was an error! Check your data and try again! Email is taken or password is not secured enough",
                                                Toast.LENGTH_LONG)
                                        .show();
                                progressbar.setVisibility(View.GONE);
                            }
                        }
                    });
        }
        }

    }
