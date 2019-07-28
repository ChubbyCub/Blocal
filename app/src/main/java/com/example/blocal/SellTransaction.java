package com.example.blocal;

import android.content.Context;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import com.example.blocal.model.Product;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import ui.SellListingRecyclerAdapter;

public class SellTransaction extends Fragment {
    private static final String TAG = "SellTransaction";
    private ArrayList<Product> listings = new ArrayList<> ();
    private RecyclerView recyclerView;

    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    String currentUserId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate ( R.layout.fragment_sell_transaction, container, false );


        db = FirebaseFirestore.getInstance ();

        Query query = db.collection ( "products" ).whereEqualTo ( "userId", currentUserId );
        query.get ()
                .addOnCompleteListener ( new OnCompleteListener<QuerySnapshot> () {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful ()) {
                            QuerySnapshot qs = task.getResult ();
                            for (QueryDocumentSnapshot document : qs) {
                                Product product = new Product ();
                                product.setProductId ( document.getId () );
                                product.setName ( document.get ( "name" ).toString () );
                                product.setPhotoURL ( document.get ( "photoURL" ).toString () );
                                ArrayList<String> pendingOffers = (ArrayList<String>) document.get ( "pendingOffers" );

                                // pending offers from the database can be empty here...
                                if (pendingOffers == null || pendingOffers.size () == 0) {
                                    product.setPendingOffers ( new ArrayList<String> () );
                                } else {
                                    product.setPendingOffers ( pendingOffers );
                                }
                                listings.add ( product );
                                recyclerView = (RecyclerView) view.findViewById ( R.id.sell_listing_recycler_view );
                                recyclerView.setHasFixedSize ( true );
                                recyclerView.setLayoutManager ( new LinearLayoutManager (getActivity ()));

                                SellListingRecyclerAdapter mAdapter = new SellListingRecyclerAdapter ( getActivity (), listings);
                                recyclerView.setAdapter ( mAdapter );
                                mAdapter.notifyDataSetChanged ();
                            }

                        } else {
                            Log.e ( TAG, task.getException ().getMessage () );
                        }
                    }
                } );

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach ( context );
        firebaseAuth = FirebaseAuth.getInstance ();
        user = firebaseAuth.getCurrentUser ();
        currentUserId = user.getUid();
    }
}
