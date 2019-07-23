package com.example.blocal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.media.Image;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.DecimalFormat;

import model.Product;

public class ProductDetailActivity extends AppCompatActivity {
    private TextView productName;
    private TextView productLocation;
    private TextView productTimestamp;
    private TextView productDescription;
    private TextView productPrice;
    private ImageView productImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_product_detail );
        setRequestedOrientation ( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );

        Product product = getIntent ().getParcelableExtra ( "product" );

        productName = (TextView) findViewById ( R.id.product_name_main );
        productLocation = (TextView) findViewById ( R.id.product_location_main );
        productTimestamp = (TextView) findViewById ( R.id.product_timestamp_main );
        productImage = (ImageView) findViewById ( R.id.product_image_main );
        productDescription = findViewById ( R.id.product_description_main );
        productPrice = findViewById ( R.id.product_price_main );

        productName.setText ( product.getName () );
        productLocation.setText ( product.getLocation () );
        productDescription.setText ( product.getDescription () );

        DecimalFormat df = new DecimalFormat ( "#.##" );
        productPrice.setText ( "$" + df.format ( product.getPrice () ) );

        setUserName ( product.getUserId () );

        String timeAgo = (String) DateUtils.getRelativeTimeSpanString ( product.getDateAdded ().getSeconds () * 1000 );
        productTimestamp.setText ( timeAgo );

        String photoURL = product.getPhotoURL ();
        Picasso.get ().load ( photoURL ).fit ().centerCrop ().into ( productImage );
    }

    private void setUserName(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance ();
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
}
