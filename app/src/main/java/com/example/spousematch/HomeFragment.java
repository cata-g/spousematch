package com.example.spousematch;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ProgressBar;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        // Inflate the layout for this fragment'
        return inflater.inflate(R.layout.fragment_home, container, false);

    }
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://spousemat-ch-default-rtdb.asia-southeast1.firebasedatabase.app/");
    DatabaseReference databaseReference = firebaseDatabase.getReference("users/");
    FirebaseStorage storage = FirebaseStorage.getInstance();
    int totalProfiles;
    LocationManager locationManager;
    FusedLocationProviderClient client;
    HashMap<String, Long> coordonates;
    HashMap<String, Double> coordsForProfile = new HashMap<>();
    ProgressBar progressBar;
    GridView gridView;
    HashMap<String, String> details;
    ArrayList<ProfileModel> profileModelArrayList;

   // String userId;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Home.bottomNavigationView.setVisibility(View.INVISIBLE);
        gridView = getView().findViewById(R.id.gridProfile);
        progressBar = getView().findViewById(R.id.loadingProfiles);
        progressBar.setVisibility(View.VISIBLE);

        profileModelArrayList = new ArrayList<ProfileModel>();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String, HashMap<String, String>> values = (HashMap<String, HashMap<String, String>>) snapshot.getValue();
                Iterator iterator = values.keySet().iterator();
                totalProfiles = values.size();
                while (iterator.hasNext()) {
                    String userId = (String) iterator.next();
                    if (!Objects.equals(FirebaseAuth.getInstance().getUid(), userId)) {
                        details = values.get(userId);
                        client = LocationServices.getFusedLocationProviderClient(getActivity());
                        checkPermission(snapshot, userId, details);
                        //getCurrentLocation(snapshot, userId, details);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private double distance(long lat2, long lon2, DataSnapshot snapshot, String userId){
        long lat1;
        long lon1;
        if(!snapshot.child(userId).hasChild("lastLoc")) {
            HashMap<String, Double> defaultCoords = new HashMap<>();
            defaultCoords.put("Longitude", 0.0);
            defaultCoords.put("Latitude", 0.0);
            databaseReference.child(userId).child("lastLoc").setValue(defaultCoords);
            lat1 = 0;
            lon1 = 0;
        }else{
            HashMap<String, Long> coord = (HashMap<String, Long>) snapshot.child(userId).child("lastLoc").getValue();
            lat1 = coord.get("Latitude");
            lon1 = coord.get("Longitude");
        }
        lon1 = (long) Math.toRadians(lon1);
        lon2 = (long) Math.toRadians(lon2);
        lat1 = (long) Math.toRadians(lat1);
        lat2 = (long) Math.toRadians(lat2);

        // Haversine formula
        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;
        double a = Math.pow(Math.sin(dlat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(dlon / 2),2);

        double c = 2 * Math.asin(Math.sqrt(a));

        // Radius of earth in kilometers. Use 3956
        // for miles
        double r = 6371;

        // calculate the result
        return(c * r);
    }


    private void checkPermission(DataSnapshot snapshot, String userId,HashMap<String, String> details){
        if(ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            getCurrentLocation(snapshot, userId, details);
        } else{
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
        }
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation(DataSnapshot snapshot, String userId, HashMap<String, String> details) {
        LocationManager locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            client.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();

                    if(location != null){
                        double lat = location.getLatitude();
                        double lng = location.getLongitude();
                        coordonates = new HashMap<String, Long>();
                        coordonates.put("Latitude", (long) lat);
                        coordonates.put("Longitude", (long) lng);
                        databaseReference.child(FirebaseAuth.getInstance().getUid()).child("lastLoc").setValue(coordonates);
                        System.out.println(coordonates + "LOVIN");
                        long lat1 = 0, lat2, lon1 = 0, lon2;

                        //System.out.println("COORDONATE PROF " + coordsForProfile + " COORDS USER " + coordonates);
                        lat2 = coordonates.get("Latitude");
                        lon2 = coordonates.get("Longitude");
                        Double distanceBetween = distance(lat2, lon2, snapshot, userId);
                        ProfileModel add = new ProfileModel(
                                details.get("name"),
                                details.get("imageLink"),
                                details.get("aboutme"),
                                details.get("diet"),
                                details.get("dosh"),
                                details.get("education"),
                                details.get("gender"),
                                details.get("height"),
                                details.get("partnerPreferences"),
                                details.get("profession"),
                                details.get("religion"),
                                details.get("status"),
                                details.get("birthdate"),
                                details.get("phoneNumber"),
                                userId,
                                distanceBetween
                        );
                        //System.out.println(details);
                        //System.out.println(add.getName());
                        HashMap<String, String> prefs = null;
                        if(snapshot.child(FirebaseAuth.getInstance().getUid()).hasChild("prefs"))
                            prefs = (HashMap<String, String>) snapshot.child(FirebaseAuth.getInstance().getUid()).child("prefs").getValue();

                        if (prefs == null) {
                            System.out.println("VERIFICAM PT PROF");
                            profileModelArrayList.add(add);
                        }else{
                            if(checkIfCorrespond(add, prefs))
                                profileModelArrayList.add(add);
                            else
                                totalProfiles--;
                        }
                        if(totalProfiles == 1){
                            Home.bottomNavigationView.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);
                            getView().findViewById(R.id.noaccounts).setVisibility(View.VISIBLE);
                        }
                        else if (profileModelArrayList.size() == totalProfiles - 1 && totalProfiles > 0) {
                            Home.bottomNavigationView.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);
                            if(getActivity() != null && profileModelArrayList.size() != 0) {
                                ProfileGVAdapter adapter = new ProfileGVAdapter(getActivity(), profileModelArrayList);
                                gridView.setAdapter(adapter);
                            }

                        }
                    }else{
                        LocationRequest locationRequest = new LocationRequest()
                                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                                .setInterval(10000)
                                .setFastestInterval(1000)
                                .setNumUpdates(1);

                        LocationCallback locationCallback = new LocationCallback() {
                            @Override
                            public void onLocationResult(@NonNull LocationResult locationResult) {
                                Location location1 = locationResult.getLastLocation();

                                double lat = location1.getLatitude();
                                double lng = location1.getLongitude();
                                coordonates = new HashMap<String, Long>();
                                coordonates.put("Latitude", (long) lat);
                                coordonates.put("Longitude", (long) lng);
                                databaseReference.child(FirebaseAuth.getInstance().getUid()).child("lastLoc").setValue(coordonates);
                                //System.out.println(coordonates + "LOVIN");
                                //finishProfileCards(snapshot, userId);
                            }
                        };

                        client.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                    }
                }
            });
        } else{
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }

    }
    private boolean checkIfCorrespond(ProfileModel profile, HashMap<String, String> prefs){
        // DIET DOSH EDUCATION GENDER HEIGHT PROFESSION RELIGION STATUS AGE
        int yes = 0, no = 0;
        if(prefs.containsKey("age")) {
            String[] desiredAge = Objects.requireNonNull(prefs.get("age")).split("-");
            Integer minim = Integer.parseInt(desiredAge[0]);
            Integer maxim = Integer.parseInt(desiredAge[1]);
            try {
                if (minim <= profile.getAgeInt() && profile.getAgeInt() <= maxim)
                    yes++;
                else {
                    return false;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if(prefs.containsKey("height")) {
            String[] desiredHeight = Objects.requireNonNull(prefs.get("height")).split(",");
            if (searchInStringArray(desiredHeight, profile.getHeight()))
                yes++;
            else
                no++;
        }
        if(prefs.containsKey("diet")) {
            String[] desiredDiet = Objects.requireNonNull(prefs.get("diet")).split(",");
            if (searchInStringArray(desiredDiet, profile.getDiet()))
                yes++;
            else
                no++;
        }

        if(prefs.containsKey("dosh")) {
            String[] desiredDosh = Objects.requireNonNull(prefs.get("dosh")).split(",");
            if (searchInStringArray(desiredDosh, profile.getDosh()))
                yes++;
            else
                no++;
        }

        if(prefs.containsKey("education")) {
            String[] desiredEducation = Objects.requireNonNull(prefs.get("education")).split(",");
            if (searchInStringArray(desiredEducation, profile.getEducation()))
                yes++;
            else
                no++;
        }

        if(prefs.containsKey("gender")) {
            String[] desireGender = Objects.requireNonNull(prefs.get("gender")).split(",");
            if (searchInStringArray(desireGender, profile.getGender()))
                yes++;
            else {
                return false;
            }
        }
        if(prefs.containsKey("profession")) {
            String[] desiredProfession = Objects.requireNonNull(prefs.get("profession")).split(",");
            if (searchInStringArray(desiredProfession, profile.getProfession()))
                yes++;
            else
                no++;
        }
        if(prefs.containsKey("religion")) {
            String[] desiredReligion = Objects.requireNonNull(prefs.get("religion")).split(",");
            if (searchInStringArray(desiredReligion, profile.getReligion()))
                yes++;
            else
                no++;
        }
        if(prefs.containsKey("status")) {
            String[] desiredStatus = Objects.requireNonNull(prefs.get("status")).split(",");
            if (searchInStringArray(desiredStatus, profile.getStatus()))
                yes++;
            else
                no++;
        }
        System.out.println("YES " + yes + " NO" + no);
        return (yes + 2) >= no;
    }

    private boolean searchInStringArray(String[] where, String what){

        for(String str : where)
            if(str.equals(what))
                return true;
        return false;

    }
}