package com.example.blocal;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import com.example.blocal.model.Product;
import ui.SellListingRecyclerAdapter;

public class SellTransaction extends Fragment {
    private static final String TAG = "SellTransaction";
    private ArrayList<Product> listings;
    private RecyclerView recyclerView;

    FirebaseFirestore db;
    CollectionReference products;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate ( R.layout.fragment_sell_transaction, container, false );
        Bundle extras = getActivity().getIntent().getExtras ();
        listings = extras.getParcelableArrayList ( "listings" );

        db = FirebaseFirestore.getInstance ();
        products = db.collection ( "products" );

        recyclerView = (RecyclerView) view.findViewById ( R.id.sell_listing_recycler_view );
        recyclerView.setHasFixedSize ( true );
        recyclerView.setLayoutManager ( new LinearLayoutManager (getActivity ()));

        SellListingRecyclerAdapter mAdapter = new SellListingRecyclerAdapter ( getActivity (), listings);
        recyclerView.setAdapter ( mAdapter );
        mAdapter.notifyDataSetChanged ();
        return view;
    }


}
