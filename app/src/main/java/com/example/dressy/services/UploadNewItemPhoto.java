package com.example.dressy.services;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.dressy.classes.Photo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UploadNewItemPhoto extends IntentService {

    private StorageReference storageReference;
    private DatabaseReference database;

    private int photoQuality = 90;
    private Intent resultIntent = new Intent("upload_new_item_photo_result");
    private String user_id = "";

    public UploadNewItemPhoto() {
        super("UploadNewItemPhoto");
    }

    static final String dressyLogTag = "dressyLogs";

    @Override
    protected void onHandleIntent(Intent intent) {

        Bitmap photo = BitmapFactory.decodeFile(intent.getStringExtra("photo"));
        user_id = intent.getStringExtra("user_id");
        final String type = intent.getStringExtra("type");

        storageReference = FirebaseStorage.getInstance().getReference().child(UUID.randomUUID().toString());
        database = FirebaseDatabase.getInstance().getReference();

        //converte bitmap para bytes
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, photoQuality, baos);
        byte[] data = baos.toByteArray();

        Log.d(dressyLogTag, "Image file size: " + photo.getByteCount());

        storageReference.putBytes(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                requestVisionData(uri.toString());
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

    protected void savePhotoToDatabase(final String url, String user, String type){

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

    private void requestVisionData(final String imageUrl){

        Log.d(dressyLogTag, "Requesting Vision data.");
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://vision.googleapis.com/v1/images:annotate?key=AIzaSyAzkHaJQ3KYhyvn_sI5_plpjAOwmRvBpnc";

        JSONObject request  = new JSONObject();
        JSONArray requestArray = new JSONArray();
        JSONObject image  = new JSONObject();
        JSONObject imageInfo = new JSONObject();
        JSONObject source = new JSONObject();
        JSONObject features  = new JSONObject();
        JSONArray featuresFather = new JSONArray();

        try {
            source.put("imageUri", imageUrl.replace("\\", ""));
            image.put("source", source);

            features.put("type", "LABEL_DETECTION");
            features.put("maxResults", 10);
            featuresFather.put(features);

            imageInfo.put("image", image);
            imageInfo.put("features", featuresFather);
            requestArray.put(imageInfo);
            request.put("requests", requestArray);

            Log.d(dressyLogTag, "JSON to send:");
            Log.d(dressyLogTag, request.toString());

        } catch(JSONException error){
            Log.d(dressyLogTag, "[JSON] Unexpected error building JSON object: " + error.getMessage());
        }

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, request, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(dressyLogTag, "Response received from Vision API");
                Log.d(dressyLogTag, response.toString());

                JSONArray labels = new JSONArray();


                try {
                    JSONArray results = response.getJSONArray("responses");
                    JSONObject result = results.getJSONObject(0);
                    labels = result.getJSONArray("labelAnnotations");
                }

                catch (JSONException error){
                    Log.d(dressyLogTag, "[JSON] Unexpected error parsing JSON object: " + error.getMessage());
                }



                savePhotoToDatabase(imageUrl, user_id, categorize(labels));

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(dressyLogTag, "[UPLOAD.Vision] Unexpected error uploading image to Vision API.");
                Log.d(dressyLogTag, error.toString());
                error.printStackTrace();
            }
        });

        queue.add(jsonRequest);
    }

    private String categorize(JSONArray labels) {
        String result = "ERROR";
        JSONObject label;
        List<String[]> categories = new ArrayList<>();

        categories.add(new String[] {"Jeans", "pants"});
        categories.add(new String[] {"Trousers", "pants"});

        categories.add(new String[] {"Footwear", "shoes"});
        categories.add(new String[] {"Shoe", "shoes"});
        categories.add(new String[] {"Sneakers", "shoes"});

        categories.add(new String[] {"Sweater", "sweater"});
        categories.add(new String[] {"Long-sleeved t-shirt", "sweater"});
        categories.add(new String[] {"T-shirt", "sweater"});
        categories.add(new String[] {"Blouse", "sweater"});

        categories.add(new String[] {"Jacket", "jacket"});

            forloop: {
                for (int x = 0; x<labels.length(); x++){

                    try{
                        label = labels.getJSONObject(x);
                        for (int i = 0; i<categories.size(); i++){
                            Log.d(dressyLogTag, categories.get(i)[0]);
                            Log.d(dressyLogTag, label.get("description").toString());
                            if(label.get("description").toString().equals(categories.get(i)[0])){
                                result = categories.get(i)[0];
                                break forloop;
                            }
                        }
                    } catch (JSONException error) {
                        Log.d(dressyLogTag, "[JSON] Unexpected error parsing JSON object: " + error.getMessage());
                    }
                }
            }

        return(result);
    }

}
