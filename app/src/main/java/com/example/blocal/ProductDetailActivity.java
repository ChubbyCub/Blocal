package com.example.blocal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;

import com.example.blocal.model.Offer;
import com.example.blocal.model.Product;

import javax.annotation.Nullable;

public class ProductDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView productName;
    private TextView productTimestamp;
    private TextView productDescription;
    private TextView productPrice;
    private ImageView productImage;
    private Button makeOfferButton;
    private Button askSellerButton;
    private Product product;

    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_product_detail );
        setRequestedOrientation ( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );

        product = getIntent ().getParcelableExtra ( "product" );

        productName = (TextView) findViewById ( R.id.product_name_main );
        productTimestamp = (TextView) findViewById ( R.id.product_timestamp_main );
        productImage = (ImageView) findViewById ( R.id.product_image_main );
        productDescription = findViewById ( R.id.product_description_main );
        productPrice = findViewById ( R.id.product_price_main );
        makeOfferButton = findViewById ( R.id.make_offer_button );
        askSellerButton = findViewById ( R.id.ask_seller_button );

        productName.setText ( product.getName () );
        productDescription.setText ( product.getDescription () );
        makeOfferButton.setOnClickListener ( this );
        askSellerButton.setOnClickListener ( this );

        DecimalFormat df = new DecimalFormat ( "#.##" );
        productPrice.setText ( "$" + df.format ( product.getPrice () ) );

        setUserName ( product.getUserId () );

        String timeAgo = (String) DateUtils.getRelativeTimeSpanString ( product.getDateAdded ().getSeconds () * 1000 );
        productTimestamp.setText ( timeAgo );

        String photoURL = product.getPhotoURL ();
        Picasso.get ().load ( photoURL ).fit ().centerCrop ().into ( productImage );
    }

    private void setUserName(String userId) {
        db = FirebaseFirestore.getInstance ();
        CollectionReference users = db.collection ( "users" );
        Query query = users.whereEqualTo ( "userId", userId );

        query.get ()
                .addOnCompleteListener ( new OnCompleteListener<QuerySnapshot> () {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful ()) {
                            for (QueryDocumentSnapshot document : task.getResult ()) {
                                String userName = document.get ( "userDisplayName" ).toString ();
                                TextView sellerName = findViewById ( R.id.seller_name_main );
                                sellerName.setText ( userName );
                            }
                        }
                    }
                } )
                .addOnFailureListener ( new OnFailureListener () {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d ( "ERROR: ", e.getMessage () );
                    }
                } );
    }

    @Override
    public void onClick(View view) {
        switch (view.getId ()) {
            case R.id.make_offer_button:
                if (currentUserId.equals ( product.getUserId () )) {
                    Toast.makeText ( getApplicationContext (), "Cannot make an offer on your own product", Toast.LENGTH_SHORT ).show ();
                } else {
                    LayoutInflater li = LayoutInflater.from ( getApplicationContext () );
                    View promptView = li.inflate ( R.layout.offer_prompt, (ViewGroup) null );

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder ( ProductDetailActivity.this, R.style.MyDialogTheme );

                    alertDialogBuilder.setView ( promptView );

                    final EditText userInput = (EditText) promptView.findViewById ( R.id.offer_amount_edit_text );

                    alertDialogBuilder
                            .setCancelable ( false )
                            .setPositiveButton ( "Submit",
                                    new DialogInterface.OnClickListener () {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            createOffer ( Double.parseDouble ( userInput.getText ().toString () ) );
                                        }
                                    } )
                            .setNegativeButton ( "Cancel",
                                    new DialogInterface.OnClickListener () {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.cancel ();
                                        }
                                    } );

                    AlertDialog alertDialog = alertDialogBuilder.create ();
                    alertDialog.show ();
                    Window window = alertDialog.getWindow ();
                    window.setLayout ( ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT );
                }
                break;
            case R.id.ask_seller_button:
                break;
        }

    }

    private void createOffer(final double price) {
        db = FirebaseFirestore.getInstance ();
        final CollectionReference offers = db.collection ( "offers" );

        offers.get ()
                .addOnCompleteListener ( new OnCompleteListener<QuerySnapshot> () {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful ()) {
                            final Offer offer = new Offer (
                                    price,
                                    currentUserId,
                                    product.getUserId (),
                                    product.getProductId ());

                            // stop one user make double offer
                            Query query = offers.whereEqualTo ( "buyerId", currentUserId );
                            query.get().addOnSuccessListener ( new OnSuccessListener<QuerySnapshot> () {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    if(queryDocumentSnapshots != null) {
                                        Toast.makeText ( ProductDetailActivity.this, "You already offered on this product", Toast.LENGTH_SHORT ).show();
                                        return;
                                    } else {
                                        offers.add ( offer ).addOnSuccessListener ( new OnSuccessListener<DocumentReference> () {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                Toast.makeText(getApplicationContext (), "Successfully made an offer", Toast.LENGTH_SHORT).show();
                                            }
                                        } );
                                    }
                                }
                            } );
                        }
                    }
                } );
    }

    @Override
    protected void onStart() {
        super.onStart ();
        firebaseAuth = FirebaseAuth.getInstance ();
        user = firebaseAuth.getCurrentUser ();
        currentUserId = user.getUid ();
    }
}
