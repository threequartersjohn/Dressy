package com.example.dressy.services;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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

import org.json.JSONArray;
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

        requestVisionData(url);
        resultIntent.putExtra("success", true);
    }

    private void requestVisionData(String imageUrl){

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://vision.googleapis.com/v1/images:annotate?key=AIzaSyAzkHaJQ3KYhyvn_sI5_plpjAOwmRvBpnc";

        JSONObject request  = new JSONObject();
        JSONArray requestArray = new JSONArray();
        JSONObject image  = new JSONObject();
        JSONObject source = new JSONObject();
        JSONObject imageFather  = new JSONObject();
        JSONObject features  = new JSONObject();
        JSONObject featuresFather = new JSONObject();

        try {

            Log.d(dressyLogTag, "Building JSON");
            source.put("imageUri", imageUrl);
            image.put("source", source);
            imageFather.put("image", image);

            features.put("type", "LABEL_DETECTION");
            features.put("maxResults", 1);
            featuresFather.put("features", features);

            requestArray.put(imageFather);
            requestArray.put(featuresFather);
            request.put("request", requestArray);

            Log.d(dressyLogTag, "JSON to send:");
            Log.d(dressyLogTag, request.toString());

        } catch(JSONException error){
            Log.d(dressyLogTag, "[JSON] Unexpected error building JSON object: " + error.getMessage());
        }

        Log.d(dressyLogTag, "Attempting Request");

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, request, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(dressyLogTag, "Response received from Vision API");
                Log.d(dressyLogTag, response.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(dressyLogTag, "[UPLOAD.Vision] Unexpected error uploading image to Vision API.");
                Log.d(dressyLogTag, error.toString());
            }
        });

        queue.add(jsonRequest);
    }

}
