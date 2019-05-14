package com.example.dressy.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dressy.R;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class homeFragment extends Fragment {

    //array de combinações
    //cada item tem as 4 imagens
    ArrayList<Array> combinationsList[];

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    private void loadImages(){

    }


}
