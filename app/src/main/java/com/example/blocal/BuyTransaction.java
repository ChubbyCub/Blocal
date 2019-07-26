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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class BuyTransaction extends Fragment {
    private static final String TAG = "BuyTransaction";
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private String currentUserId;
    private TableLayout tableLayout;

    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate ( R.layout.fragment_buy_transaction, container, false );
        db = FirebaseFirestore.getInstance ();
        DocumentReference df = db.collection("users").document (currentUserId);
        final CollectionReference offers = db.collection("offers");

        tableLayout = getActivity ().findViewById ( R.id.sent_offer_table );

        // query the list of offer sent
        df.get()
                .addOnCompleteListener ( new OnCompleteListener<DocumentSnapshot> () {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful ()) {
                            DocumentSnapshot document = task.getResult ();
                            ArrayList<String> sentOffers = (ArrayList<String>) document.get("sentOffers");
                            if(sentOffers.size() == 0 || sentOffers == null) {
                                return;
                            } else {
                                for(String id : sentOffers) {
                                    offers.document (id).get()
                                    .addOnCompleteListener ( new OnCompleteListener<DocumentSnapshot> () {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if(task.isSuccessful ()) {
                                                DocumentSnapshot document = task.getResult ();
                                                String productId = document.get("productId").toString ();
                                            }
                                        }
                                    } );
                                }


                                // loop thru the sent offer array, query out the following info:
                                // 1. product id => from here... need to fetch the product info
                                // 2. offer price
                                // 3. status --> add status // how to know the status? check to see if the status is part of the accepted of rejected array
                                // if it belongs to neither, that means, the status is pending.
                                // if it belongs to accepted, then put status as accepted
                                // if it belongs to rejected, then put status as rejected
                            }
                        }
                    }
                } );

        // send the list to the adapter

        return view;
    }

    private void queryProductInfo(final String productId) {
        db.collection ( "products" );
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach ( context );
        firebaseAuth = FirebaseAuth.getInstance ();
        user = firebaseAuth.getCurrentUser ();
        currentUserId = user.getUid ();
    }
}
