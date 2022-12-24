package com.example.spousematch;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NotificationGVAdapter extends ArrayAdapter<Notification> {
    public NotificationGVAdapter(@NonNull Context context, @NonNull ArrayList<Notification> notificationArrayList) {
        super(context, 0 , notificationArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listitemView = convertView;
        if (listitemView == null) {
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.notification_card, parent, false);
        }
        Notification notification = getItem(position);
        ImageView userImage = listitemView.findViewById(R.id.notificationUserImage);
        TextView notificationMessage = listitemView.findViewById(R.id.notificationMessage);
        LinearLayout linearLayout = listitemView.findViewById(R.id.linearLayoutGoToProfile);

        Picasso.get().load(notification.getImageLink()).into(userImage);

        String message;
        String type = notification.getNotificationType();
        if(type.equals("REQUESTED"))
            message = notification.getUserName() + " sent you a request! ";
        else
            message = notification.getUserName() + " accepted your request!";
        notificationMessage.setText(message);

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://spousemat-ch-default-rtdb.asia-southeast1.firebasedatabase.app/");
                DatabaseReference databaseReference = firebaseDatabase.getReference("users/" + notification.getUserId() + "/");
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        HashMap<String, String> details = (HashMap<String, String>) snapshot.getValue();
                        Intent intent = new Intent(getContext(),ProfileDetails.class);
                        intent.putExtra("img", notification.getImageLink());
                        intent.putExtra("name", details.get("name"));
                        intent.putExtra("gender", details.get("gender"));
                        intent.putExtra("religion", details.get("religion"));
                        intent.putExtra("dosh", details.get("dosh"));
                        intent.putExtra("status", details.get("status"));
                        intent.putExtra("diet", details.get("diet"));
                        intent.putExtra("education", details.get("education"));
                        intent.putExtra("profession",  details.get("profession"));
                        intent.putExtra("aboutme", details.get("aboutme"));
                        intent.putExtra("partnerPrefs", details.get("partnerPreferences"));
                        intent.putExtra("height", details.get("height"));
                        intent.putExtra("profileId", notification.getUserId());
                        intent.putExtra("phoneNumber", details.get("phoneNumber"));
                        getContext().startActivity(intent);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Fail to get data.", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        return listitemView;
    }
}
