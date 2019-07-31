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

import com.example.blocal.model.Offer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import ui.SellListingRecyclerAdapter;
import ui.SentOffersRecyclerAdapter;

public class BuyTransaction extends Fragment {
    private static final String TAG = "BuyTransaction";
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private String currentUserId;
    private ArrayList<String> sentOffers = new ArrayList<> ();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate ( R.layout.fragment_buy_transaction, container, false );

        db = FirebaseFirestore.getInstance ();
        DocumentReference df = db.collection ( "users" ).document ( currentUserId );
        df.get ()
                .addOnCompleteListener ( new OnCompleteListener<DocumentSnapshot> () {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful ()) {
                            DocumentSnapshot document = task.getResult ();
                            sentOffers = (ArrayList<String>) document.get ( "sentOffers" );
                            if (sentOffers.size () == 0 || sentOffers == null) {
                                sentOffers = new ArrayList<> ();
                            }
                            queryOffers ( sentOffers, view );
                        }
                    }
                } );

        return view;
    }

    private void queryOffers(ArrayList<String> sentOffers, final View view) {
        final ArrayList<Offer> offers = new ArrayList<> ();
        for (String id : sentOffers) {
            db.collection ( "offers" ).document ( id ).get ()
                    .addOnCompleteListener ( new OnCompleteListener<DocumentSnapshot> () {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful ()) {
                                DocumentSnapshot document = task.getResult ();
                                if (document.exists ()) {
                                    Offer offer = document.toObject ( Offer.class );
                                    offers.add ( offer );
                                    sortOffersByUpdate ( offers, view );
                                }
                            }
                        }
                    } );
        }
    }

    private void sortOffersByUpdate(ArrayList<Offer> offers, View view) {
        Collections.sort ( offers, new Comparator<Offer> () {
            @Override
            public int compare(Offer offer1, Offer offer2) {
                long time1 = offer1.getDateUpdated ().getSeconds ();
                long time2 = offer2.getDateCreated ().getSeconds ();

                if (time1 < time2) {
                    return 1;
                }

                if (time1 > time2) {
                    return -1;
                }

                return 0;
            }
        } );

        RecyclerView mRecyclerView = view.findViewById ( R.id.buy_listing_recycler_view );
        mRecyclerView.setHasFixedSize ( true );
        mRecyclerView.setLayoutManager ( new LinearLayoutManager ( getActivity () ) );

        SentOffersRecyclerAdapter mAdapter = new SentOffersRecyclerAdapter ( getActivity (), offers );
        mRecyclerView.setAdapter ( mAdapter );
        mAdapter.notifyDataSetChanged ();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach ( context );
        firebaseAuth = FirebaseAuth.getInstance ();
        user = firebaseAuth.getCurrentUser ();
        currentUserId = user.getUid ();
    }
}
