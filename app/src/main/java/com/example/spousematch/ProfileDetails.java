package com.example.spousematch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;

public class ProfileDetails extends AppCompatActivity {

    float x1,x2,y1,y2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_details);

        Intent intent = getIntent();
        String profession = intent.getStringExtra("profession");
        String education = intent.getStringExtra("education");
        String height = intent.getStringExtra("height");
        String diet = intent.getStringExtra("diet");
        String status = intent.getStringExtra("status");
        String dosh = intent.getStringExtra("dosh");
        String religion = intent.getStringExtra("religion");
        String name = intent.getStringExtra("name");
        String gender = intent.getStringExtra("gender");
        String img = intent.getStringExtra("img");
        String aboutme = intent.getStringExtra("aboutme");
        String prefs = intent.getStringExtra("partnerPrefs");
        String profileId = intent.getStringExtra("profileId");
        String phoneNumber = intent.getStringExtra("phoneNumber");

        ImageView imageView = findViewById(R.id.profileDetailsImg);
        TextView nameView = findViewById(R.id.profileDetailsName);
        LinearLayout descView = findViewById(R.id.profileDetailsDesc);
        TextView aboutView = findViewById(R.id.profileDetailsAbout);
        TextView prefsView = findViewById(R.id.profileDetailsPrefs);

        Button sendReq = findViewById(R.id.sendRequestBtn);
        Button cancelReq = findViewById(R.id.cancelRequestBtn);
        Button contact = findViewById(R.id.contactBtn);

        LinearLayout buttonsLinearLayout = findViewById(R.id.accept_decline_buttons);
        Button accept = findViewById(R.id.acceptButton);
        Button decline = findViewById(R.id.declineButton);

        nameView.setText(name);
        generateDetail("height",height, descView);
        generateDetail("gender",gender, descView);
        generateDetail("job",profession, descView);
        generateDetail("education",education, descView);
        generateDetail("diet",diet, descView);
        generateDetail("dosh",dosh, descView);
        generateDetail("religion",religion, descView);
        generateDetail("status",status, descView);
        aboutView.setText(aboutme);
        prefsView.setText(prefs);

        if(!img.equals("") && !img.equals("nophoto"))
            Picasso.get().load(img).into(imageView);
        else
            imageView.setImageResource(R.drawable.nophoto);
        //Picasso.get().load(img).into(imageView);

        /*
            Mai intai cautam la requests, la profile id daca uuid-ul actual este acceptat / requested
            daca este requested => cancel request
            daca este accepted =>  contact
            else => send request

            mai intai verificam asta apoi ailalta de mai sus
            cand userul intra pe un profil:
            daca profileId se afla in requests la FirebaseAuth id => accept decline buttons
        */
        String uid = FirebaseAuth.getInstance().getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance("https://spousemat-ch-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();

        reference.child("requests/").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sendReq.setVisibility(View.INVISIBLE);
                cancelReq.setVisibility(View.INVISIBLE);
                contact.setVisibility(View.INVISIBLE);
                buttonsLinearLayout.setVisibility(View.INVISIBLE);
                if(snapshot.exists()){
                    HashMap<String, HashMap<String, String>> allRequests = (HashMap<String, HashMap<String, String>>) snapshot.getValue();

                    if(allRequests.get(uid).get(profileId) != null && allRequests.get(uid).get(profileId).equals("REQUESTED")){
                        buttonsLinearLayout.setVisibility(View.VISIBLE);
                        accept.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                reference.child("requests").child(FirebaseAuth.getInstance().getUid()).child(profileId).setValue("ACCEPTED");
                            }
                        });
                        decline.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                reference.child(FirebaseAuth.getInstance().getUid()).child(profileId).removeValue();
                            }
                        });
                        return;
                    }

                    Iterator requestOwner = allRequests.keySet().iterator();
                    if(allRequests.get(profileId) == null){
                        System.out.println("This profile has no requests");
                        sendReq.setVisibility(View.VISIBLE);
                    }

                    while(requestOwner.hasNext()){
                        String actualOwner = requestOwner.next().toString();
                        if(actualOwner.equals(profileId)) {
                            HashMap<String, String> requestsOfActualOwner = allRequests.get(actualOwner);
                            if(requestsOfActualOwner.get(uid) == null)
                                sendReq.setVisibility(View.VISIBLE);
                            else{
                                if(requestsOfActualOwner.get(uid).equals("REQUESTED"))
                                    cancelReq.setVisibility(View.VISIBLE);
                                else
                                    contact.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }else{
                    sendReq.setVisibility(View.VISIBLE);
                }

                sendReq.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        reference.child("requests").child(profileId).child(uid).setValue("REQUESTED");
                    }
                });

                contact.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        System.out.println(phoneNumber);
                        intent.setData(Uri.parse("tel:" + phoneNumber));
                        startActivity(intent);
                        System.out.println("Contact " + profileId + " by " + uid);
                    }
                });

                cancelReq.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        reference.child("requests").child(profileId).child(uid).removeValue();
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void generateDetail(String name, String s, LinearLayout descView) {

        LinearLayout detail = new LinearLayout(getApplicationContext());
        detail.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        detail.setOrientation(LinearLayout.HORIZONTAL);

        TextView text = new TextView(getApplicationContext());
        text.setTextColor(Color.BLACK);
        text.setTextSize(20);
        text.setText(s);

        ImageView img = new ImageView(getApplicationContext());
        img.setLayoutParams(new LinearLayout.LayoutParams(70, 70));

        switch (name){
            case "height":
                img.setImageResource(R.drawable.height);
                break;
            case "job":
                img.setImageResource(R.drawable.job);
                break;
            case "education":
                img.setImageResource(R.drawable.education);
                break;
            case "diet":
                img.setImageResource(R.drawable.diet);
                break;
            case "dosh":
                img.setImageResource(R.drawable.dosh);
                break;
            case "gender":
                img.setImageResource(R.drawable.gender);
                break;
            case "status":
                img.setImageResource(R.drawable.status);
                break;
            case "religion":
                img.setImageResource(R.drawable.religion);
                break;
        }

        detail.addView(img);
        detail.addView(text);
        descView.addView(detail);


    }
}