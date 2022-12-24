package com.example.spousematch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import java.util.HashMap;

public class GenderPref extends AppCompatActivity {
    HashMap<String, Boolean> preferences = new HashMap<String, Boolean>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gender_pref);

        LinearLayout layout = findViewById(R.id.genderButtonsLayout);
        String action = getIntent().getStringExtra("action");

        Button submitBtn = new Button(this);
        submitBtn.setText("Submit your preference");
        submitBtn.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                intent.putExtra("prefs", preferences);
                intent.putExtra("intent", "gender");
                intent.putExtra("textViewId", intent.getIntExtra("textViewId", 0));
                setResult(50, intent);
                finish();
            }
        });
        layout.addView(submitBtn);

        CheckBox male = findViewById(R.id.malePref);
        male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(male.isChecked())
                    preferences.put("Male", true);
                else
                    preferences.remove("Male");
            }
        });
        CheckBox female = findViewById(R.id.femalePred);
        female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(female.isChecked())
                    preferences.put("Female", true);
                else
                    preferences.remove("Female");
            }
        });
    }
}