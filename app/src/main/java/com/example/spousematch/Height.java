package com.example.spousematch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import java.util.HashMap;
import java.util.Random;

public class Height extends AppCompatActivity {

    String selectedHeight = "";
    HashMap<String, Boolean> preferences = new HashMap<String, Boolean>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_height);

        LinearLayout layout = findViewById(R.id.heightButtonsLayout);
        Button submitBtn = new Button(this);
        String action = getIntent().getStringExtra("action");
        submitBtn.setText("Submit your preference");
        submitBtn.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                intent.putExtra("prefs", preferences);
                intent.putExtra("intent", "height");
                intent.putExtra("textViewId", intent.getIntExtra("textViewId", 0));
                setResult(50, intent);
                finish();
            }
        });
        String[] heights = {"4.1", "4.2", "4.3", "4.4", "4.5", "4.6", "4.7", "4.8", "4.9", "5.0", "5.1"};
        for (String name : heights){
            if (action == null || !action.equals("setPreference"))
                createButton(name, layout);
            else
                createCheckbox(name, layout);
        }
        if(action != null && action.equals("setPreference"))
            layout.addView(submitBtn);
    }

    private void createCheckbox(String name, LinearLayout layout) {
        CheckBox button = new CheckBox(this);
        button.setText(name);
        button.setId(new Random().nextInt());
        button.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(button.isChecked())
                    preferences.put(name, true);
                else
                    preferences.remove(name);
            }
        });

        layout.addView(button);

    }
    private void createButton(String name, LinearLayout layout){
        Button button = new Button(this);
        button.setText(name);
        button.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedHeight = name;
                Intent intent = getIntent();
                if(intent.getStringExtra("action") != null && intent.getStringExtra("action").equals("editing")) {
                    intent.putExtra("newValue", selectedHeight);
                    intent.putExtra("intent", "height");
                    intent.putExtra("textViewId", intent.getIntExtra("textViewId", 0));
                    setResult(100, intent);
                    finish();
                }else {
                    String birthdate = intent.getStringExtra("birthdate");
                    String diet = intent.getStringExtra("diet");
                    String status = intent.getStringExtra("status");
                    String dosh = intent.getStringExtra("dosh");
                    String religion = intent.getStringExtra("religion");
                    String email = intent.getStringExtra("email");
                    String phoneNumber = intent.getStringExtra("phoneNumber");
                    String name = intent.getStringExtra("name");
                    String gender = intent.getStringExtra("gender");
                    String password = intent.getStringExtra("password");

                    intent = new Intent(Height.this, Education.class);
                    intent.putExtra("email", email);
                    intent.putExtra("phoneNumber", phoneNumber);
                    intent.putExtra("name", name);
                    intent.putExtra("gender", gender);
                    intent.putExtra("religion", religion);
                    intent.putExtra("dosh", dosh);
                    intent.putExtra("status", status);
                    intent.putExtra("diet", diet);
                    intent.putExtra("height", selectedHeight);
                    intent.putExtra("password", password);
                    intent.putExtra("birthdate", birthdate);
                    startActivity(intent);
                }
            }
        });
        layout.addView(button);
    }
}