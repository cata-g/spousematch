package com.example.spousematch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import org.florescu.android.rangeseekbar.RangeSeekBar;

import java.util.HashMap;

public class AgePref extends AppCompatActivity {

    HashMap<String, String> preferences = new HashMap<String, String>();
    String MinAge, MaxAge;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_age_pref);
        LinearLayout layout = findViewById(R.id.linearLayoutAgePrefs);
        String action = getIntent().getStringExtra("action");

        Button submitBtn = new Button(this);
        submitBtn.setText("Submit your preference");
        submitBtn.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                intent.putExtra("prefs", preferences);
                intent.putExtra("intent", "age");
                intent.putExtra("textViewId", intent.getIntExtra("textViewId", 0));
                setResult(50, intent);
                finish();
            }
        });
        layout.addView(submitBtn);
        RangeSeekBar bar = findViewById(R.id.ageRangeBar);
        preferences.put("MinAge", "18");
        preferences.put("MaxAge", "99");
        bar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar bar, Object minValue, Object maxValue) {
                MinAge = String.valueOf(minValue);
                MaxAge = String.valueOf(maxValue);
                preferences.put("MinAge", MinAge);
                preferences.put("MaxAge", MaxAge);
            }
        });

    }
}