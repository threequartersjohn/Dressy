package com.example.dressy.services;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.dressy.classes.Photo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class UploadNewItemPhoto extends IntentService {

    private StorageReference storageReference;
    private DatabaseReference database;

    private int photoQuality = 90;
    private Intent resultIntent = new Intent("upload_new_item_photo_result");


    public UploadNewItemPhoto() {
        super("UploadNewItemPhoto");
    }

    static final String dressyLogTag = "dressylogs";

    @Override
    protected void onHandleIntent(Intent intent) {

        Bitmap photo = intent.getParcelableExtra("photo");
        final String user_id = intent.getStringExtra("user_id");
        final String type = intent.getStringExtra("type");

        storageReference = FirebaseStorage.getInstance().getReference().child(UUID.randomUUID().toString());
        database = FirebaseDatabase.getInstance().getReference();

        //converte bitmap para bytes
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, photoQuality, baos);
        byte[] data = baos.toByteArray();

        Log.d(dressyLogTag, "trying to upload!");

        storageReference.putBytes(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                savePhotoToDatabase(uri.toString(), user_id, type);
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {

                        Log.d(dressyLogTag, "didn't work :(!");
                        resultIntent.putExtra("success", false);
                    }
                });
    }

    protected void savePhotoToDatabase(String url, String user, String type){

        Photo photo = new Photo(url, type);

        database.child(user).push().setValue(photo, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    System.out.println("Data could not be saved " + databaseError.getMessage());
                } else {
                    System.out.println("Data saved successfully.");
                }
            }
        });
        resultIntent.putExtra("success", true);
    }

}
