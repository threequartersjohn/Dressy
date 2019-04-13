package com.example.dressy.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.dressy.R;
import com.example.dressy.services.UploadNewItemPhoto;
import com.google.firebase.FirebaseApp;

public class Closet extends AppCompatActivity {

    static final int REQUEST_NEW_ITEM_PHOTO = 1;
    static final String dressyLogTag = "dressylogs";

    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navCloset:
                    mTextMessage.setText(R.string.title_closet);
                    return true;
                case R.id.navHome:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navFavorites:
                    mTextMessage.setText(R.string.title_favorites);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_closet);

        //hides title bar
        getSupportActionBar().hide();

        mTextMessage = findViewById(R.id.message);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Log.d(dressyLogTag, "is it logging????");
    }

    public void requestNewItemPhoto(View view){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            Log.d(dressyLogTag, "starting take picture intent");
            startActivityForResult(takePictureIntent, REQUEST_NEW_ITEM_PHOTO);
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //new item photo service result
        if (requestCode == REQUEST_NEW_ITEM_PHOTO && resultCode == RESULT_OK) {

            Log.d(dressyLogTag, "got picture, uploading??");


            Bundle extras = data.getExtras();
            Bitmap newItemPhoto = (Bitmap) extras.get("data");

            Intent intent = new Intent(this, UploadNewItemPhoto.class);
            intent.putExtra("photo", newItemPhoto);
            intent.putExtra("user_id", "admin");
            intent.putExtra("type", "t-shirt");
            startService(intent);
        }
    }

    //Local Broadcast Logic para receber resultado do serviço
    private BroadcastReceiver  bReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(dressyLogTag, "save photo result: " + intent.getBooleanExtra("success", false));
        }
    };

    protected void onResume(){
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(bReceiver, new IntentFilter("upload_new_item_photo_result"));
    }

    protected void onPause (){
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(bReceiver);
    }

}
