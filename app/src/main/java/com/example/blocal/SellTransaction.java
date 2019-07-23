package com.example.blocal;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import model.Product;

public class SellTransaction extends Fragment {
    private static final String TAG = "SellTransaction";
    private ArrayList<String> productIds;
    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate ( R.layout.fragment_sell_transaction, container, false );
        Bundle extras = getActivity().getIntent().getExtras ();
        productIds = extras.getStringArrayList ( "productIds" );
        Log.d(TAG, productIds.toString ());

        recyclerView = (RecyclerView) view.findViewById ( R.id.sell_listing_recycler_view );
        recyclerView.setHasFixedSize ( true );
        recyclerView.setLayoutManager ( new LinearLayoutManager (getActivity ()));

        return view;
    }



}
