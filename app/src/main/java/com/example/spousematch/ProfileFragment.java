package com.example.spousematch;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
        return inflater.inflate(R.layout.fragment_profile, container, false);

    }
    Boolean editMode=false;
    List<Button> editButtons = new ArrayList<Button>();
    HashMap<String, String> editedDetails = new HashMap<String, String>();
    HashMap<String, String> editedPrefs = new HashMap<String, String>();
    EditText aboutmeE, prefsE;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://spousemat-ch-default-rtdb.asia-southeast1.firebasedatabase.app/");
    DatabaseReference databaseReference = firebaseDatabase.getReference("users/" + FirebaseAuth.getInstance().getUid() + "/");
    List<String> skipDetails = new ArrayList<String>();
    Button saveChanges = null;
    ImageView profilePicture = null;
    public Uri filePath;
    FirebaseStorage storage;
    StorageReference storageReference;
    LinearLayout editPhotoBtns;
    Button uploadPhoto, removePhoto;
    LinearLayout preferences;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        skipDetails.add("imageLink");
        skipDetails.add("email");
        skipDetails.add("phoneNumber");
        skipDetails.add("aboutme");
        skipDetails.add("partnerPreferences");
        skipDetails.add("name");
        skipDetails.add("prefs");
        skipDetails.add("birthdate");
        skipDetails.add("lastLoc");

        aboutmeE = requireView().findViewById(R.id.aboutmeEdit);
        prefsE = requireView().findViewById(R.id.prefsEdit);
        profilePicture = requireView().findViewById(R.id.profilePic);
        Button editBtn = requireView().findViewById(R.id.editProfileBtn);
        LinearLayout layout = requireView().findViewById(R.id.detailsButtons);
        editPhotoBtns = requireView().findViewById(R.id.editImageBtns);
        uploadPhoto = requireView().findViewById(R.id.addANewPhotoBtn);
        removePhoto = requireView().findViewById(R.id.removePhotoBtn);
        editPhotoBtns.setVisibility(View.INVISIBLE);
        preferences = requireView().findViewById(R.id.preferencesButtons);

        requireView().findViewById(R.id.logoutUser).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String, HashMap<String, String>> values = (HashMap<String, HashMap<String, String>>) snapshot.getValue();
                HashMap<String, String> value = new HashMap<String, String>();
                HashMap<String, String> prefsHM = new HashMap<String, String>();
                for(String title: values.keySet())
                {
                    HashMap<String, String> data;
                    if(!title.equals("prefs")) {
                            value.put(title, String.valueOf(values.get(title)));
                    }else{
                        value.put("prefs", "y");
                        prefsHM = values.get(title);
                    }
                }
                value.put("age", "y");
                Iterator iterator = value.keySet().iterator();
                while(iterator.hasNext()){
                    String title = iterator.next().toString();
                    if(!skipDetails.contains(title)) {
                        String data = value.get(title);
                        String prefsData;
                        System.out.println(value);
                        if(!value.containsKey("prefs"))
                            prefsData = "No preference";
                        else {
                            prefsData = prefsHM.get(title);
                        }
                        createDetail(layout, title, data,prefsData, preferences);
                    }
                }
                saveChanges = view.findViewById(R.id.saveProfileBtn);
                if(saveChanges == null) {
                    LinearLayout layout = getView().findViewById(R.id.linearlayoutProfile);
                    createSaveChangesButton(layout);
                    saveChanges = requireView().findViewById(R.id.saveProfileBtn);
                }
                editBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editMode = true;
                        if(saveChanges != null)
                            saveChanges.setVisibility(View.VISIBLE);
                        prefsE.setEnabled(true);
                        aboutmeE.setEnabled(true);
                        for(Button btn : editButtons)
                            btn.setVisibility(View.VISIBLE);
                        editPhotoBtns.setVisibility(View.VISIBLE);
                    }
                });
                String image = value.get("imageLink");
                if(!image.equals("nophoto"))
                    Picasso.get().load(image).into(profilePicture);
                else
                    profilePicture.setImageResource(R.drawable.nophoto);
                String aboutMe = value.get("aboutme");
                String prefs = value.get("partnerPreferences");
                aboutmeE.setText(aboutMe);
                aboutmeE.setEnabled(false);
                prefsE.setText(prefs);
                prefsE.setEnabled(false);
                aboutmeE.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if(editMode){
                            if(hasFocus)
                                aboutmeE.setInputType(InputType.TYPE_CLASS_TEXT);
                            else {
                                aboutmeE.setInputType(InputType.TYPE_NULL);
                                String textToChange = aboutmeE.getText().toString();
                                if(!textToChange.equals(value.get("aboutme"))){
                                    aboutmeE.setText(textToChange);
                                    editedDetails.put("aboutme", textToChange);
                                }
                            }
                        }
                    }
                });

                prefsE.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if(editMode){

                            if(hasFocus)
                                prefsE.setInputType(InputType.TYPE_CLASS_TEXT);
                            else {
                                prefsE.setInputType(InputType.TYPE_NULL);
                                String textToChange = prefsE.getText().toString();
                                if(!textToChange.equals(value.get("partnerPreferences"))){
                                    prefsE.setText(textToChange);
                                    editedDetails.put("partnerPreferences", textToChange);
                                }
                            }
                        }
                    }
                });
                removePhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        removePhoto();
                    }
                });
                uploadPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectImage();
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Fail to get data.", Toast.LENGTH_SHORT).show();
            }
        });


    }
    private void createDetail(LinearLayout layout, String name, String data,String prefData, LinearLayout preferencesLayout){
        skipDetails.add(name);
        LinearLayout detail = new LinearLayout(getActivity());
        detail.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.HORIZONTAL));
        detail.setOrientation(LinearLayout.HORIZONTAL);
        detail.setWeightSum(3);
        TextView detailName = new TextView(getActivity());
        detailName.setTextSize(20);
        detailName.setTextColor(Color.BLACK);
        detailName.setText(name);

        ImageView img = new ImageView(getActivity());
        img.setLayoutParams(new LinearLayout.LayoutParams(100, 100));
        img.setColorFilter(Color.BLACK);
        TextView detailData = new TextView(getActivity());
        detailData.setTextSize(20);
        detailData.setText(data);
        detailData.setTextColor(Color.BLACK);
        Button editMe = new Button(getActivity());
        editMe.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        editMe.setText("Edit");
        editMe.setTextColor(Color.WHITE);
        editMe.setBackgroundResource(R.drawable.rounded_button);
        editMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editIntent = null;

                switch(name) {
                    case "diet":
                        editIntent = new Intent(getActivity(), Diet.class);
                        detailData.setId(R.id.diet);
                        break;
                    case "dosh":
                        editIntent = new Intent(getActivity(), Dosh.class);
                        detailData.setId(R.id.dosh);
                        break;
                    case "education":
                        editIntent = new Intent(getActivity(), Education.class);
                        detailData.setId(R.id.education);
                        break;
                    case "height":
                        editIntent = new Intent(getActivity(), Height.class);
                        detailData.setId(R.id.height);
                        break;
                    case "profession":
                        editIntent = new Intent(getActivity(), Profession.class);
                        detailData.setId(R.id.profession);
                        break;
                    case "religion":
                        editIntent = new Intent(getActivity(), Religion.class);
                        detailData.setId(R.id.religion);
                        break;
                    case "status":
                        editIntent = new Intent(getActivity(), MaritalStatus.class);
                        detailData.setId(R.id.status);
                        break;
                    default:
                        break;
                }
                editIntent.putExtra("action", "editing");
                editIntent.putExtra("textViewId", detailData.getId());
                editIntent.setAction(Intent.ACTION_GET_CONTENT);
                if(editIntent != null)
                    startActivityForResult(editIntent, 100);
            }
        });
        switch (name){
            case "height":
                img.setImageResource(R.drawable.height);
                break;
            case "profession":
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

        // CHANGE PREFERENCES
        LinearLayout preferences = new LinearLayout(getActivity());
        preferences.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.HORIZONTAL));
        preferences.setOrientation(LinearLayout.VERTICAL);
        TextView changeName = new TextView(getActivity());
        changeName.setTextSize(20);
        changeName.setText(name);

        LinearLayout changeData = new LinearLayout(getActivity());
        changeData.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        changeData.setOrientation(LinearLayout.VERTICAL);
        if(prefData != null) {
            String[] items = prefData.split(",");
            for (String s : items) {
                TextView dataText = new TextView(getActivity());
                dataText.setTextSize(18);
                dataText.setText(s);
                dataText.setTextColor(Color.BLACK);
                changeData.addView(dataText);
            }
        }
        ImageView imgPref = new ImageView(getActivity());
        imgPref.setLayoutParams(new LinearLayout.LayoutParams(100, 100));
        imgPref.setColorFilter(Color.BLACK);

        switch (name){
            case "height":
                imgPref.setImageResource(R.drawable.height);
                break;
            case "profession":
                imgPref.setImageResource(R.drawable.job);
                break;
            case "education":
                imgPref.setImageResource(R.drawable.education);
                break;
            case "diet":
                imgPref.setImageResource(R.drawable.diet);
                break;
            case "dosh":
                imgPref.setImageResource(R.drawable.dosh);
                break;
            case "gender":
                imgPref.setImageResource(R.drawable.gender);
                break;
            case "status":
                imgPref.setImageResource(R.drawable.status);
                break;
            case "religion":
                imgPref.setImageResource(R.drawable.religion);
                break;
            case "age":
                imgPref.setImageResource(R.drawable.age);
                break;
        }

        Button changeMe = new Button(getActivity());
        changeMe.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        changeMe.setText("Change Preferences");
        changeMe.setTextColor(Color.WHITE);
        changeMe.setBackgroundResource(R.drawable.rounded_button);
        changeMe.setPadding(15,15,15,15);
        changeMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent changeIntent = null;

                switch(name) {
                    case "diet":
                        changeIntent = new Intent(getActivity(), Diet.class);
                        changeData.setId(R.id.prefdiet);
                        break;
                    case "dosh":
                        changeIntent = new Intent(getActivity(), Dosh.class);
                        changeData.setId(R.id.prefdosh);
                        break;
                    case "education":
                        changeIntent = new Intent(getActivity(), Education.class);
                        changeData.setId(R.id.prefeducation);
                        break;
                    case "height":
                        changeIntent = new Intent(getActivity(), Height.class);
                        changeData.setId(R.id.prefheight);
                        break;
                    case "profession":
                        changeIntent = new Intent(getActivity(), Profession.class);
                        changeData.setId(R.id.prefprofession);
                        break;
                    case "religion":
                        changeIntent = new Intent(getActivity(), Religion.class);
                        changeData.setId(R.id.prefreligion);
                        break;
                    case "status":
                        changeIntent = new Intent(getActivity(), MaritalStatus.class);
                        changeData.setId(R.id.prefstatus);
                        break;
                    case "age":
                        changeIntent = new Intent(getActivity(), AgePref.class);
                        changeData.setId(R.id.prefage);
                        break;
                    case "gender":
                        changeIntent = new Intent(getActivity(), GenderPref.class);
                        changeData.setId(R.id.prefgender);
                        break;

                }
                changeIntent.putExtra("action", "setPreference");
                changeIntent.putExtra("textViewId", changeData.getId());
                changeIntent.setAction(Intent.ACTION_GET_CONTENT);
                if(changeIntent != null)
                    startActivityForResult(changeIntent, 50);
            }
        });


        editButtons.add(changeMe);
        if(!name.equals("age")) {
            editButtons.add(editMe);
            editMe.setVisibility(View.INVISIBLE);
            detail.addView(img);
            //detail.addView(detailName);
            detail.addView(detailData);
            editMe.setRight(0);
            detail.addView(editMe);
            layout.addView(detail);
        }

        changeName.setTextColor(Color.BLACK);
        preferences.addView(imgPref);
        preferences.addView(changeName);
        preferences.addView(changeData);
        preferences.addView(changeMe);
        changeMe.setVisibility(View.INVISIBLE);
        preferencesLayout.addView(preferences);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 100){
            String newText = data.getStringExtra("newValue");
            String intent = data.getStringExtra("intent");
            int idButton = data.getIntExtra("textViewId", 0);
            if(idButton != 0) {
                LinearLayout view = requireView().findViewById(idButton);
                String[] items = newText.split(",");
                for(String s: items){
                    TextView dataText = new TextView(getActivity());
                    dataText.setTextSize(20);
                    dataText.setText(s);
                    view.addView(dataText);
                }
                editedDetails.put(intent, newText);
            }
        }
        if(resultCode == 50){
            String intent = data.getStringExtra("intent");
            if(intent.equals("age")) {
                HashMap<String, String> prefs2 = (HashMap<String, String>) data.getSerializableExtra("prefs");

                int idButton2 = data.getIntExtra("textViewId", 0);
                if (idButton2 != 0) {
                    LinearLayout view = requireView().findViewById(idButton2);
                    view.removeAllViews();
                    String text = prefs2.get("MinAge") + "-" + prefs2.get("MaxAge");
                    TextView dataText = new TextView(getActivity());
                    dataText.setTextSize(20);
                    dataText.setText(text);
                    view.addView(dataText);
                    editedPrefs.put(intent, text);
                }
            }
            else {
                HashMap<String, Boolean> prefs = (HashMap<String, Boolean>) data.getSerializableExtra("prefs");
                String newText = new String();
                for (String pref : prefs.keySet())
                    if (Boolean.TRUE.equals(prefs.get(pref)))
                        newText += pref + ",";
                int idButton = data.getIntExtra("textViewId", 0);
                if (idButton != 0) {
                    LinearLayout view = requireView().findViewById(idButton);
                    String[] items = newText.split(",");
                    view.removeAllViews();
                    for (String s : items) {
                        TextView dataText = new TextView(getActivity());
                        dataText.setTextSize(20);
                        dataText.setText(s);
                        view.addView(dataText);
                    }
                    editedPrefs.put(intent, newText);
                }
            }

        }
        if(requestCode == 30 && resultCode == RESULT_OK && data != null && data.getData() != null){
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                profilePicture.setImageBitmap(bitmap);
                uploadImage();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    private void createSaveChangesButton(LinearLayout layout){
        Button save = new Button(getActivity());
        save.setId(R.id.saveProfileBtn);
        save.setText("Save The Changes");
        save.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        save.setTextColor(Color.WHITE);
        save.setBackgroundResource(R.drawable.rounded_button);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editedDetails.put("aboutme", aboutmeE.getText().toString());
                editedDetails.put("partnerPreferences", prefsE.getText().toString());

                for (String title : editedDetails.keySet()) {
                    String value = editedDetails.get(title);
                    databaseReference.child(title).setValue(value);
                }
                for (String title : editedPrefs.keySet()) {
                    String value = editedPrefs.get(title);
                    databaseReference.child("prefs").child(title).setValue(value);
                }

                editMode = false;
                for(Button btn : editButtons)
                    btn.setVisibility(View.INVISIBLE);
                prefsE.setEnabled(false);
                aboutmeE.setEnabled(false);
                save.setVisibility(View.INVISIBLE);
                editPhotoBtns.setVisibility(View.INVISIBLE);

            }
        });
        save.setVisibility(View.INVISIBLE);
        layout.addView(save);
    }

    private void removePhoto(){
        databaseReference.child("imageLink").setValue("nophoto");
        if(profilePicture != null)
            profilePicture.setImageResource(R.drawable.nophoto);
    }

    private void selectImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image from here..."), 30);
    }



    private void uploadImage(){

        if(filePath != null){
            ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            String userId = FirebaseAuth.getInstance().getUid();
            String saveName = "images/" + userId + "/" + 1;
            storage = FirebaseStorage.getInstance();
            storageReference = storage.getReference();
            StorageReference reference = storageReference.child(saveName);
            reference.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                        {
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    DatabaseReference reference = FirebaseDatabase.getInstance("https://spousemat-ch-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();
                                    reference.child("users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).child("imageLink").setValue(uri.toString());
                                }
                            });
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(),"Image Uploaded!!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            double progress = (100.0 * snapshot.getBytesTransferred() /snapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int)progress + "%");
                        }
                    });

        }

    }

    public void logoutUser() {
        FirebaseAuth.getInstance().signOut();
        SharedPreferences preferences = getContext().getSharedPreferences("checkbox", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("remember", "false");
        editor.apply();
        startActivity(new Intent(getActivity(), Main.class));
    }
}