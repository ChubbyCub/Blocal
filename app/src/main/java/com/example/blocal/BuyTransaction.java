package com.example.blocal;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import ui.SellListingRecyclerAdapter;
import ui.SentOffersRecyclerAdapter;

public class BuyTransaction extends Fragment {
    private static final String TAG = "BuyTransaction";
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private String currentUserId;
    private ArrayList<String> sentOffers;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate ( R.layout.fragment_buy_transaction, container, false );

        Bundle extras = getActivity().getIntent().getExtras ();
        sentOffers = extras.getStringArrayList ( "sentOffers" );


        final RecyclerView mRecyclerView = view.findViewById ( R.id.buy_listing_recycler_view );
        mRecyclerView.setHasFixedSize ( true );
        mRecyclerView.setLayoutManager ( new LinearLayoutManager (getActivity ()));

        SentOffersRecyclerAdapter mAdapter = new SentOffersRecyclerAdapter ( getActivity (), sentOffers);
        mRecyclerView.setAdapter ( mAdapter );
        mAdapter.notifyDataSetChanged ();

//        db = FirebaseFirestore.getInstance ();
//        DocumentReference df = db.collection("users").document (currentUserId);
//        df.get()
//                .addOnCompleteListener ( new OnCompleteListener<DocumentSnapshot> () {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                        if(task.isSuccessful ()) {
//                            DocumentSnapshot document = task.getResult ();
//                            ArrayList<String> sentOffers = (ArrayList<String>)document.get("sentOffers");
//
//                            if(sentOffers.size() == 0 || sentOffers == null) {
//                                sentOffers = new ArrayList<>();
//                            } else {
//
//                            }
//
//                        }
//                    }
//                } );

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach ( context );
        firebaseAuth = FirebaseAuth.getInstance ();
        user = firebaseAuth.getCurrentUser ();
        currentUserId = user.getUid ();
    }
}
