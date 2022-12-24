package com.example.spousematch;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

public class ProfileGVAdapter extends ArrayAdapter<ProfileModel> {
    public ProfileGVAdapter(Context context, ArrayList<ProfileModel> profileModelArrayList) {
        super(context, 0 , profileModelArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listItemView = convertView;
        if (listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.profile_card, parent, false);
        }
        ProfileModel profileModel = getItem(position);
        LinearLayout profDetails = listItemView.findViewById(R.id.profileDetailsLayout);
        ImageView profile = listItemView.findViewById(R.id.profileImgId);
        String locDetail = "";
        String display = "";
        try {
            if(locDetail.equals("") && display.equals("")){
                TextView name = new TextView(getContext());
                name.setTextSize(12);
                name.setTextColor(Color.BLACK);
                LinearLayout loc = new LinearLayout(getContext());
                loc.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                TextView location = new TextView(getContext());
                location.setTextSize(12);
                location.setTextColor(Color.BLACK);
                ImageView locIcon = new ImageView(getContext());
                locIcon.setImageResource(R.drawable.locpin);
                locIcon.setLayoutParams(new ViewGroup.LayoutParams(50,50));
                locDetail = profileModel.getDistance().toString();
                location.setText(locDetail);
                display = profileModel.getName() + "," + profileModel.getAge();
                name.setText(display);
                loc.addView(locIcon);
                loc.addView(location);
                profDetails.addView(name);
                profDetails.addView(loc);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        System.out.println(profDetails.getHeight() + " " + profileModel.getName());
        if(profDetails.getHeight() > 1)
            profDetails.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 10));

        if(profileModel.getImgLink() != null && !profileModel.getImgLink().equals("") && !profileModel.getImgLink().equals("nophoto"))
            Picasso.get().load(profileModel.getImgLink()).into(profile);
        else
            profile.setImageResource(R.drawable.nophoto);
        //Picasso.get().load(profileModel.getImgLink()).into(profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),ProfileDetails.class);
                intent.putExtra("img", profileModel.getImgLink());
                intent.putExtra("name", profileModel.getName());
                intent.putExtra("gender", profileModel.getGender());
                intent.putExtra("religion", profileModel.getReligion());
                intent.putExtra("dosh", profileModel.getDosh());
                intent.putExtra("status", profileModel.getStatus());
                intent.putExtra("diet", profileModel.getDiet());
                intent.putExtra("education", profileModel.getEducation());
                intent.putExtra("profession", profileModel.getProfession());
                intent.putExtra("aboutme", profileModel.getAboutme());
                intent.putExtra("partnerPrefs", profileModel.getPartnerPrefs());
                intent.putExtra("height", profileModel.getHeight());
                intent.putExtra("profileId", profileModel.getProfileId());
                intent.putExtra("phoneNumber", profileModel.getPhoneNumber());
                getContext().startActivity(intent);
            }
        });
        return listItemView;
    }
}
