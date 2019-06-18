package com.example.dressy.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.dressy.R;

public class Dressy extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //hides title bar
        getSupportActionBar().hide();
    }

    //check for internet connection
    //app does not work without internet connection
    @Override
    protected void onStart() {
        super.onStart();

        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        Log.d("dressyLogs", "entered on start");

        if (!mWifi.isConnected()) {

            new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Application cannot run without a WiFi connection! ")

                    // A null listener allows the button to dismiss the dialog and take no further action.
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

        else {

            Log.d("dressyLogs", "animation should start");
            final ImageView img = findViewById(R.id.logo);
            Animation aniFade = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_out);
            aniFade.setAnimationListener(new Animation.AnimationListener(){
                @Override
                public void onAnimationStart(Animation arg0) {
                }
                @Override
                public void onAnimationRepeat(Animation arg0) {
                }
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onAnimationEnd(Animation arg0) {
                    img.setImageAlpha(0);
                    startActivity(new Intent(Dressy.this, Login.class));
                }
            });
            img.startAnimation(aniFade);
        }
    }

}
