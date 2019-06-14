package com.example.dressy.fragments;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dressy.R;
import com.example.dressy.activities.Home;
import com.example.dressy.classes.Photo;
import com.example.dressy.services.UploadNewItemPhoto;
import com.example.dressy.util.MyRecyclerViewAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.example.dressy.activities.Home.photos;
import static com.example.dressy.activities.Home.user_id;
import static com.example.dressy.util.MyRecyclerViewAdapter.photosFiltered;

public class closetFragment extends Fragment implements MyRecyclerViewAdapter.ItemClickListener
{

    static final int REQUEST_NEW_ITEM_PHOTO = 1;
    static final String TAG = "dressylogs";
    private MyRecyclerViewAdapter  adapter;
    private RecyclerView recyclerView;
    private View rootView;
    private String currentPhotoPath;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_closet, container, false);
        Button button = rootView.findViewById(R.id.btnNewPhoto);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                requestNewItemPhoto(v);
            }
        });

        TextView txtFilterPants = rootView.findViewById(R.id.txtFiltrarPants);
        TextView txtFilterShoes = rootView.findViewById(R.id.txtFiltrarShoes);
        TextView txtFilterSweater = rootView.findViewById(R.id.txtFiltrarSweater);
        TextView txtFilterJacket = rootView.findViewById(R.id.txtFiltrarJacket);
        TextView txtFilterTudo = rootView.findViewById(R.id.txtFiltrarTudo);

        txtFilterJacket.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                filterItemsInList("jacket");
            }
        });
        txtFilterPants.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                filterItemsInList("pants");
            }
        });
        txtFilterShoes.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                filterItemsInList("shoes");
            }
        });
        txtFilterSweater.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                filterItemsInList("sweater");
            }
        });
        txtFilterTudo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                filterItemsInList("");
            }
        });

        return rootView;
    }

    private void populateWithPhotos(){
        recyclerView = getActivity().findViewById(R.id.rvPhotos);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        adapter = new MyRecyclerViewAdapter(getActivity(), photos, this);
        recyclerView.setAdapter(adapter);

        Log.d(TAG, "Items in recycler view: " + adapter.getItemCount());
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        currentPhotoPath = image.getAbsolutePath();
        Log.d(TAG, "Path to temporary file: " + currentPhotoPath);
        return image;
    }


    public void requestNewItemPhoto(View  view){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch(IOException error){
                Log.d(TAG, "[FILE] Unexpected error creating image file: " + error.getMessage());
            }

            if (photoFile!= null){
                Uri photoURI = FileProvider.getUriForFile(getActivity(), "com.example.android.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_NEW_ITEM_PHOTO);
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //new item photo service result
        if (requestCode == REQUEST_NEW_ITEM_PHOTO && resultCode == RESULT_OK) {


            Intent intent = new Intent(getActivity(), UploadNewItemPhoto.class);
            intent.putExtra("photo", currentPhotoPath);
            intent.putExtra("user_id", user_id );
            intent.putExtra("type", "t-shirt");
            getActivity().startService(intent);
        }
    }

    //Local Broadcast Logic para receber resultado do serviço
    private BroadcastReceiver  bReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "save photo result: " + intent.getBooleanExtra("success", false));
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        populateWithPhotos();
    }

    public void onResume(){
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(bReceiver, new IntentFilter("upload_new_item_photo_result"));

    }

    public void onPause (){
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(bReceiver);
    }

    @Override
    public void onItemClick(final int position) {

        final String url = photosFiltered.get(position).getPhoto_url();

        AlertDialog.Builder ImageDialog = new AlertDialog.Builder(getActivity());
        ImageDialog.setTitle("Apagar?");
        ImageView showImage = new ImageView(getActivity());
        Picasso.get().load(url).resize(400, 640).centerInside().into(showImage);
        ImageDialog.setView(showImage);


        ImageDialog.setNegativeButton("yes pls", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface arg0, int arg1)
            {
            DatabaseReference ref =  FirebaseDatabase.getInstance().getReference();
            Query databaseQuery = ref.child(user_id).orderByChild("photo_url").equalTo(url);

            databaseQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.d(TAG, "Item in database to erase: " + dataSnapshot.toString());
                    photos.remove(position);
                    recyclerView.removeViewAt(position);
                    adapter.notifyItemRemoved(position);
                    adapter.notifyItemRangeChanged(position, photos.size());
                    adapter.notifyDataSetChanged();

                    for(DataSnapshot item: dataSnapshot.getChildren()){
                        item.getRef().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(url);
                                storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "Photo deleted from database!");
                                        ((Home)getActivity()).loadDataFromDatabase();
                                    }
                                });
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });


            }
        });
        ImageDialog.setPositiveButton("não", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface arg0, int arg1)
            {
            }
        });
        ImageDialog.show();

    }

    public void filterItemsInList(String type){
        adapter.getFilter().filter(type);
    }
}