package com.example.blocal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
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
        switch(view.getId ()) {
            case R.id.make_offer_button:
                CollectionReference offers = db.collection ( "offers" );
                Offer offer = new Offer();
                // TODO: COME BACK TO THIS FEATURE LATER
                break;
            case R.id.ask_seller_button:
                break;
        }

    }

    @Override
    protected void onStart() {
        super.onStart ();
        firebaseAuth = FirebaseAuth.getInstance ();
        user = firebaseAuth.getCurrentUser ();
        currentUserId = user.getUid ();
    }
}
