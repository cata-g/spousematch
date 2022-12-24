package com.example.spousematch;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class NotificationsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public NotificationsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MatchesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NotificationsFragment newInstance(String param1, String param2) {
        NotificationsFragment fragment = new NotificationsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://spousemat-ch-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference databaseReference = firebaseDatabase.getReference("requests/");


        GridView gridView = getView().findViewById(R.id.notificationsViewId);
        ProgressBar progressBar = getView().findViewById(R.id.loadingNotifications);
        progressBar.setVisibility(View.VISIBLE);

        TextView noNotif = getView().findViewById(R.id.nonotifId);
        noNotif.setVisibility(View.INVISIBLE);
        System.out.println("DIN NOU AICI");

        ArrayList<Notification> notificationArrayList = new ArrayList<Notification>();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    HashMap<String, HashMap<String, String>> values = (HashMap<String, HashMap<String, String>>) snapshot.getValue();
                    Iterator iterator = values.keySet().iterator();
                    String actualUser = FirebaseAuth.getInstance().getUid();
                    String notifType = "";
                    final String[] username = new String[1];
                    final String[] imageLink = new String[1];
                    while (iterator.hasNext()) {
                        String userId = iterator.next().toString();
                        HashMap<String, String> response = (HashMap<String, String>) values.get(userId);
                        for (String id : response.keySet()) {
                            int notifCount = 0;
                            String userIdToSearch = "";
                            if (id.equals(actualUser) && response.get(id).equals("ACCEPTED")) {
                                System.out.println(userId + "accepted your request " + actualUser + "(" + FirebaseAuth.getInstance().getUid() + ")");
                                userIdToSearch = userId;
                                notifType = "ACCEPTED";
                                notifCount++;
                            } else if (userId.equals(actualUser) && response.get(id).equals("REQUESTED")) {
                                System.out.println(id + " sent you a friend request " + userId);
                                userIdToSearch = id;
                                notifType = "REQUESTED";
                                notifCount++;
                            }

                            if (notifCount == 0) {
                                progressBar.setVisibility(View.INVISIBLE);
                                noNotif.setVisibility(View.VISIBLE);
                            }
                            if (!userIdToSearch.isEmpty()) {
                                String finalUserIdToSearch = userIdToSearch;
                                String finalNotifType = notifType;
                                int finalNotifCount = notifCount;
                                firebaseDatabase.getReference("users").child(userIdToSearch).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        HashMap<String, String> response = (HashMap<String, String>) snapshot.getValue();
                                        String username = response.get("name");
                                        String imageLink = response.get("imageLink");
                                        notificationArrayList.add(new Notification(imageLink, finalUserIdToSearch, finalNotifType, username));
                                        if (notificationArrayList.size() == finalNotifCount) {
                                            NotificationGVAdapter adapter = new NotificationGVAdapter(getActivity(), notificationArrayList);
                                            gridView.setAdapter(adapter);
                                            progressBar.setVisibility(View.INVISIBLE);
                                            noNotif.setVisibility(View.INVISIBLE);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println(error.getMessage());
            }
        });
    }


}