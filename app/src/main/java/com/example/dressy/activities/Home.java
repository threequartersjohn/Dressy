package com.example.dressy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.example.dressy.R;
import com.example.dressy.classes.Photo;
import com.example.dressy.fragments.closetFragment;
import com.example.dressy.fragments.favoritesFragment;
import com.example.dressy.fragments.homeFragment;
import com.google.android.gms.auth.api.Auth;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class Home extends AppCompatActivity {


    private final String TAG = "dressyLogs";
    private DatabaseReference databaseReference;
    public static List<Photo> photos = new ArrayList<>();
    public static String user_id = "admin";
    public static Integer width;
    public final static ArrayList<ArrayList<ArrayList<String>>> listOfCachedFiles = new ArrayList<>();
    public final static ArrayList<ArrayList<String>> favorites = new ArrayList<>();


    private final ArrayList<ArrayList<String>> filesByCategory = new ArrayList<>();
    private final ArrayList<String> pants = new ArrayList<>();
    private final ArrayList<String> jacket = new ArrayList<>();
    private final ArrayList<String> shoes = new ArrayList<>();
    private final ArrayList<String> sweater = new ArrayList<>();

    private ArrayList<String[]> categories = new ArrayList<>();

    private void populateCategories(){

        categories.add(new String[] {"Jeans", "pants"});
        categories.add(new String[] {"Trousers", "pants"});

        categories.add(new String[] {"Footwear", "shoes"});
        categories.add(new String[] {"Shoe", "shoe"});

        categories.add(new String[] {"Sweater", "sweater"});
        categories.add(new String[] {"Long-sleeved t-shirt", "sweater"});
        categories.add(new String[] {"Blouse", "sweater"});

        categories.add(new String[] {"Jacket", "jacket"});

    }

    private Integer loadedFiles = 0;
    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            Fragment selectedFragment = null;

            switch (item.getItemId()) {
                case R.id.navCloset:
                    selectedFragment = new closetFragment();
                    switchIcons(R.id.navCloset);
                    break;
                case R.id.navHome:
                    selectedFragment = new homeFragment();
                    switchIcons(R.id.navHome);
                    break;
                case R.id.navFavorites:
                    selectedFragment = new favoritesFragment();
                    switchIcons(R.id.navFavorites);
                    break;
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, selectedFragment).commit();

            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        populateCategories();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        Intent intent = getIntent();
        user_id = intent.getStringExtra("user").substring(0, intent.getStringExtra("user").indexOf('@'));

        width = displayMetrics.widthPixels/4;

        ImageView logout = (ImageView) findViewById(R.id.btnOptions);
        logout.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View v) {
                                          logout();
                                      }

                                      ;
                                  });

        //hides title bar
        getSupportActionBar().hide();

        databaseReference = FirebaseDatabase.getInstance().getReference().child(user_id);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.navHome);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new homeFragment()).commit();
    }

    public void logout(){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(Home.this, Login.class));

    }

    private ArrayList<String> selectRandomPhotos(){
        ArrayList<String> tempSelectedPhotos = new ArrayList();
        Random random = new Random();

        for(ArrayList<String> list: filesByCategory){
            if (list.size()==0){
                continue;
            }
            else if (list.size()==1){
                tempSelectedPhotos.add(list.get(0));
            }
            else {
                Integer rnd = random.nextInt(Math.round(list.size()));
                tempSelectedPhotos.add(list.get(rnd));
                Log.d(TAG, "list size: " + list.size());
            }
        }

        return tempSelectedPhotos;
    }

    public void loadFilesIntoCache() throws IOException {

        Integer attempt = 0;

        while(listOfCachedFiles.size()<=3 && attempt<3){
            final ArrayList<ArrayList<String>> items = new ArrayList<>();
            final ArrayList<String> photos = selectRandomPhotos();
            attempt++;

            for(int x = 0; x<photos.size(); x++){

                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "JPEG_" + timeStamp + "_";
                File storageDir = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

                final File image = File.createTempFile(
                        imageFileName,  /* prefix */
                        ".jpg",   /* suffix */
                        storageDir      /* directory */
                );
                image.deleteOnExit();

                final int iteration = x;

                StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(photos.get(x));
                storageReference.getFile(image).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Log.d(TAG, "Successful load into local file, file size is: " + image.length());
                        loadedFiles++;
                        Log.d(TAG, loadedFiles + " files loaded into memory, " + listOfCachedFiles.size() + " sets of photos in memory.");
                        ArrayList<String> tempItem = new ArrayList();
                        tempItem.add(image.getAbsolutePath());
                        tempItem.add(photos.get(iteration));
                        items.add(tempItem);
                        if (items.size() == 4){
                            listOfCachedFiles.add(items);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "[Storage] Error downloading photo into local file: " + e.getMessage());
                    }
                });
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        loadDataFromDatabase();

    }

    public void loadDataFromDatabase(){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Photo> tempPhotoHolder = new ArrayList<>();

                //load favorites
                favorites.clear();
                for (DataSnapshot ds : dataSnapshot.child("favorites").getChildren()){
                    ArrayList<String> items = new ArrayList<>();
                    for(DataSnapshot item : ds.getChildren()) {
                        items.add(item.getValue().toString());
                    }
                    favorites.add(items);
                }

                Log.d(TAG, "Favorites: " + favorites.toString());


                //load photos
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Photo photo = new Photo();

                    try{
                        photo.setPhoto_url(ds.child("photo_url").getValue().toString());
                        photo.setType(ds.child("type").getValue().toString());

                        tempPhotoHolder.add(photo);
                    } catch(Exception error) {
                        continue;
                    }
                }

                photos = new ArrayList<>(tempPhotoHolder);
                populateCategoryList();
                try{
                    loadFilesIntoCache();
                } catch(IOException error) {
                    Log.d(TAG, "[Storage] Unexpected error attempting loading photos to local files: " + error.getMessage());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "[NETWORK.Database] Unexpected error occurred while fetching database content: " + databaseError.getMessage());
            }
        });
    }

    private void populateCategoryList(){

        String type;

        for(Photo photo:photos){
            type = photo.getType();
            Log.d(TAG, type);

            switch (type){
                case "shoes":
                    shoes.add(photo.getPhoto_url());
                    break;
                case "pants":
                    pants.add(photo.getPhoto_url());
                    break;
                case "jacket":
                    jacket.add(photo.getPhoto_url());
                    break;
                case "sweater":
                    sweater.add(photo.getPhoto_url());
                    break;
            }
        }

        filesByCategory.add(jacket);
        filesByCategory.add(sweater);
        filesByCategory.add(pants);
        filesByCategory.add(shoes);

    }

    private void switchIcons(Integer ID) {
        BottomNavigationView navigation = findViewById(R.id.navigation);
        Menu menu = navigation.getMenu();
        MenuItem home = menu.findItem(R.id.navHome);
        MenuItem favorites = menu.findItem(R.id.navFavorites);
        MenuItem closet = menu.findItem(R.id.navCloset);

        switch (ID) {
            case R.id.navHome:
                home.setIcon(R.drawable.baseline_home_gold_18dp);
                favorites.setIcon(R.drawable.baseline_favorite_black_18dp);
                closet.setIcon(R.drawable.baseline_photo_library_black_18dp);
                break;
            case R.id.navFavorites:
                home.setIcon(R.drawable.baseline_home_black_18dp);
                favorites.setIcon(R.drawable.baseline_favorite_gold_18dp);
                closet.setIcon(R.drawable.baseline_photo_library_black_18dp);
                break;
            case R.id.navCloset:
                home.setIcon(R.drawable.baseline_home_black_18dp);
                favorites.setIcon(R.drawable.baseline_favorite_black_18dp);
                closet.setIcon(R.drawable.baseline_photo_library_gold_18dp);
                break;

        }
    }

}

