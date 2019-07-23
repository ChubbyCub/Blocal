package com.example.blocal;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class SellTransaction extends Fragment {
    private static final String TAG = "SellTransaction";
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate ( R.layout.fragment_sell_transaction, container, false );
        Bundle extras = getActivity().getIntent().getExtras ();
        ArrayList<String> productIds = extras.getStringArrayList ( "productIds" );
        Log.d(TAG, productIds.toString ());
        return view;
    }


}
