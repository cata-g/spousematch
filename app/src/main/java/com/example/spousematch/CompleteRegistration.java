package com.example.spousematch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.hbb20.CountryCodePicker;

import java.time.LocalDate;
import java.util.Calendar;

public class CompleteRegistration extends AppCompatActivity {

    CountryCodePicker countryCodePicker;
    EditText phoneTextView, nameTextView;
    DatePicker birthDate;
    RadioButton male, female;
    Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_registration);

        countryCodePicker = findViewById(R.id.countrycode);
        phoneTextView = findViewById(R.id.phoneno);
        nameTextView = findViewById(R.id.fullname);
        birthDate = findViewById(R.id.birthdate);
        male = findViewById(R.id.radioMale);
        female = findViewById(R.id.radioFemale);
        nextButton = findViewById(R.id.nexttoreligion);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerPreferences();
            }
        });

    }

    private void registerPreferences(){

        Intent intent = getIntent();
        String email = intent.getStringExtra("email");
        String password = intent.getStringExtra("password");
        String countryCode = countryCodePicker.getSelectedCountryCode().toString();
        String phoneNumber = phoneTextView.getText().toString();
        String name = nameTextView.getText().toString();
        Integer birthday = birthDate.getDayOfMonth();
        Integer birthmonth = birthDate.getMonth();
        Integer birthyear = birthDate.getYear();
        String birthdate = String.valueOf(birthday) + '/' + birthmonth + '/' + birthyear;
        String sex="";
        Integer errors = 0;

        if(male.isChecked())
            sex = "Male";
        else
            sex = "Female";

        if(TextUtils.isEmpty(countryCode)) {
            Toast.makeText(getApplicationContext(), "Please select your country code!", Toast.LENGTH_LONG).show();
            errors ++;
        }
        if(TextUtils.isEmpty(phoneNumber)) {
            Toast.makeText(getApplicationContext(), "Please enter your phone number!", Toast.LENGTH_LONG).show();
            errors ++;
        }
        if(TextUtils.isEmpty(name)) {
            Toast.makeText(getApplicationContext(), "Please enter your name!", Toast.LENGTH_LONG).show();
            errors ++;
        }
        if(TextUtils.isEmpty(sex)) {
            Toast.makeText(getApplicationContext(), "Please select your gender!", Toast.LENGTH_LONG).show();
            errors ++;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if(LocalDate.now().getYear() - birthyear < 18){
                Toast.makeText(getApplicationContext(), "You must be 18 years old!", Toast.LENGTH_LONG).show();
                errors ++;
            }else if(LocalDate.now().getYear() - birthyear == 18 && LocalDate.now().getMonthValue() < birthmonth){
                Toast.makeText(getApplicationContext(), "You must be 18 years old!", Toast.LENGTH_LONG).show();
                errors ++;
            }else if(LocalDate.now().getYear() - birthyear == 18 && LocalDate.now().getMonthValue() == birthmonth && LocalDate.now().getDayOfMonth() < birthday){
                Toast.makeText(getApplicationContext(), "You must be 18 years old!", Toast.LENGTH_LONG).show();
                errors ++;
            }
        }

        if(errors == 0){

            String phoneNumberFinal = countryCode + phoneNumber;

            intent = new Intent(CompleteRegistration.this, Religion.class);
            intent.putExtra("email", email);
            intent.putExtra("password", password);
            intent.putExtra("phoneNumber", phoneNumberFinal);
            intent.putExtra("name", name);
            intent.putExtra("gender", sex);
            intent.putExtra("birthdate", birthdate);
            startActivity(intent);
        }
    }

}