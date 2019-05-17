package com.example.dressy.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.dressy.R;
import com.example.dressy.classes.Photo;
import com.example.dressy.services.UploadNewItemPhoto;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.app.Activity.RESULT_OK;

public class closetFragment extends Fragment
{

    static final int REQUEST_NEW_ITEM_PHOTO = 1;
    static final String dressyLogTag = "dressylogs";
    static final FirebaseDatabase database = FirebaseDatabase.getInstance();
    static final DatabaseReference ref = database.getReference("photo_collection");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_closet, container, false);
        Button button = rootView.findViewById(R.id.btnNewPhoto);

        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                requestNewItemPhoto(v);
            }
        });

        return rootView;
    }

    public void requestNewItemPhoto(View  view){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_NEW_ITEM_PHOTO);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //new item photo service result
        if (requestCode == REQUEST_NEW_ITEM_PHOTO && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap newItemPhoto = (Bitmap) extras.get("data");

            Intent intent = new Intent(getActivity(), UploadNewItemPhoto.class);
            intent.putExtra("photo", newItemPhoto);
            intent.putExtra("user_id", "admin");
            intent.putExtra("type", "t-shirt");
            getActivity().startService(intent);
        }
    }

    //Local Broadcast Logic para receber resultado do serviço
    private BroadcastReceiver  bReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(dressyLogTag, "save photo result: " + intent.getBooleanExtra("success", false));
        }
    };

    public void onResume(){
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(bReceiver, new IntentFilter("upload_new_item_photo_result"));
    }

    public void onPause (){
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(bReceiver);
    }
}