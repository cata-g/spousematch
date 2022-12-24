package com.example.spousematch;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class Preferences extends AppCompatActivity {

    EditText aboutMe, partnerPreferences;
    Button nextPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        aboutMe = findViewById(R.id.aboutmetext);
        partnerPreferences = findViewById(R.id.preferencestext);
        nextPage = findViewById(R.id.nextpagepref);

        nextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerPreferences();
            }
        });
    }

    private void registerPreferences(){

        Integer errors = 0;
        String aboutMeText = aboutMe.getText().toString();
        String partnerPreferencesText = partnerPreferences.getText().toString();

        if(TextUtils.isEmpty(aboutMeText)){
            Toast.makeText(getApplicationContext(), "Please Complete The About Me Section!", Toast.LENGTH_LONG).show();
            errors++;
        }

        if(TextUtils.isEmpty(partnerPreferencesText)){
            Toast.makeText(getApplicationContext(), "Please Complete Your Partner Preferences!", Toast.LENGTH_LONG).show();
            errors++;
        }

        if(errors == 0){
            Intent intent = getIntent();
            String birthdate = intent.getStringExtra("birthdate");
            String profession = intent.getStringExtra("profession");
            String education = intent.getStringExtra("education");
            String height = intent.getStringExtra("height");
            String diet = intent.getStringExtra("diet");
            String status = intent.getStringExtra("status");
            String dosh = intent.getStringExtra("dosh");
            String religion = intent.getStringExtra("religion");
            String email = intent.getStringExtra("email");
            String phoneNumber = intent.getStringExtra("phoneNumber");
            String name = intent.getStringExtra("name");
            String gender = intent.getStringExtra("gender");
            String password = intent.getStringExtra("password");


            intent = new Intent(Preferences.this, AddPhotos.class);
            intent.putExtra("birthdate", birthdate);
            intent.putExtra("email", email);
            intent.putExtra("phoneNumber", phoneNumber);
            intent.putExtra("name", name);
            intent.putExtra("gender", gender);
            intent.putExtra("religion", religion);
            intent.putExtra("dosh", dosh);
            intent.putExtra("status", status);
            intent.putExtra("diet", diet);
            intent.putExtra("height", height);
            intent.putExtra("education", education);
            intent.putExtra("profession", profession);
            intent.putExtra("password", password);
            intent.putExtra("aboutme", aboutMeText);
            intent.putExtra("prefs", partnerPreferencesText);
            System.out.println(partnerPreferencesText + "PREFS");
            startActivity(intent);


        }

    }


}
