package com.example.spousematch;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class AddPhotos extends AppCompatActivity {

    public Uri filePath;
    FirebaseStorage storage;
    StorageReference storageReference;
    int selectedId;
    HashMap<String, String> user = new HashMap<String, String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addphotos);

        LinearLayout row1 = findViewById(R.id.photosRow1);
        LinearLayout row2 = findViewById(R.id.photosRow2);

        Intent intent = getIntent();

        String birthdate = intent.getStringExtra("birthdate");
        String profession = intent.getStringExtra("profession");
        String education = intent.getStringExtra("education");
        String height = intent.getStringExtra("height");
        String diet = intent.getStringExtra("diet");
        String status = intent.getStringExtra("status");
        String dosh = intent.getStringExtra("dosh");
        String religion = intent.getStringExtra("religion");
        String email = intent.getStringExtra("email");
        String phoneNumber = intent.getStringExtra("phoneNumber");
        String name = intent.getStringExtra("name");
        String gender = intent.getStringExtra("gender");
        String password = intent.getStringExtra("password");
        String aboutme = intent.getStringExtra("aboutme");
        String prefs = intent.getStringExtra("prefs");
        System.out.println("PREFS " + prefs);

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                user.put("email", email);
                user.put("phoneNumber", phoneNumber);
                user.put("name", name);
                user.put("gender", gender);
                user.put("birthdate", birthdate);
                user.put("religion", religion);
                user.put("dosh", dosh);
                user.put("status", status);
                user.put("diet", diet);
                user.put("height", height);
                user.put("education", education);
                user.put("profession", profession);
                user.put("aboutme", aboutme);
                user.put("partnerPreferences", prefs);
                DatabaseReference reference = FirebaseDatabase.getInstance("https://spousemat-ch-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();
                reference.child("users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).setValue(user);

            }
        });


        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        Button button = findViewById(R.id.goNext);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(getApplicationContext(), "User Created!", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(AddPhotos.this, FinishedReg.class);
                        startActivity(intent);
                    }
                });

            }
        });

        for (int i = 1; i<=6; i++){
            if (i <= 3)
                createImageView(i, row1);
            else
                createImageView(i, row2);
        }

    }

    private void createImageView(Integer id, LinearLayout layout) {
        ImageView img = new ImageView(this);
        img.setImageResource(R.drawable.upload);
        img.setLayoutParams(new LinearLayout.LayoutParams(300, 300));
        img.setPadding(20, 20, 20, 20);
        switch (id){
            case 1:
                img.setId(R.id.photo1);
                break;
            case 2:
                img.setId(R.id.photo2);
                break;
            case 3:
                img.setId(R.id.photo3);
                break;
            case 4:
                img.setId(R.id.photo4);
                break;
            case 5:
                img.setId(R.id.photo5);
                break;
            case 6:
                img.setId(R.id.photo6);
                break;
        }

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(img);
            }
        });
        layout.addView(img);
    }

    private void selectImage(ImageView img){
        this.selectedId = img.getId();
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image from here..."), 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100 && resultCode == RESULT_OK && data != null && data.getData() != null){
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                ImageView img = findViewById(this.selectedId);
                img.setImageBitmap(bitmap);
                uploadImage();
            }catch (IOException e){
                e.printStackTrace();
            }
        }

    }


    private void uploadImage(){

        if(filePath != null){
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            String userId = FirebaseAuth.getInstance().getUid();
            String saveName = "images/" + userId + "/" + this.selectedId;
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
                                    reference.child("users").child(FirebaseAuth.getInstance().getUid()).child("imageLink").setValue(uri.toString());
                                }
                            });
                            progressDialog.dismiss();
                            Toast.makeText(AddPhotos.this,"Image Uploaded!!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(AddPhotos.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

}
