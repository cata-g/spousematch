package com.example.spousematch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Main extends AppCompatActivity {

    Button registerButton, loginButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
        String loggedIn = preferences.getString("remember", "false");
        if(loggedIn.equals("true")){
            startActivity(new Intent(Main.this, Home.class));
        }
        registerButton = findViewById(R.id.btncreateaccount);
        loginButton = findViewById(R.id.btnlogintoaccount);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToRegisterPage();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToLoginPage();
            }
        });
    }

    private void goToRegisterPage(){
        Intent intent = new Intent(Main.this, Registration.class);
        startActivity(intent);
    }
    private void goToLoginPage(){
        Intent intent = new Intent(Main.this, Login.class);
        startActivity(intent);
    }
}