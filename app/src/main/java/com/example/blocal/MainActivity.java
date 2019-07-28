package com.example.blocal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.example.blocal.model.DistanceCalculator;
import com.example.blocal.model.Product;
import ui.ProductRecyclerAdapter;


public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private static final int LOCATION_ACCESS_CODE = 777;
    private static final String TAG = "MainActivity";
    private static boolean isPlacesInitialized = false;

    private RecyclerView recyclerView;
    private ProductRecyclerAdapter productRecyclerAdapter;
    private FirebaseFirestore db;
    private CollectionReference collectionReferenceProduct;


    private SwipeRefreshLayout swipeRefreshLayout;
    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_main );
        setRequestedOrientation ( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );


        db = FirebaseFirestore.getInstance ();

        collectionReferenceProduct = db.collection ( "products" );

        recyclerView = findViewById ( R.id.product_recycler_view );
        recyclerView.setVisibility ( View.INVISIBLE );
        recyclerView.setHasFixedSize ( true );
        recyclerView.setLayoutManager ( new GridLayoutManager ( this, 2 ) );

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById ( R.id.swipe_container );
        swipeRefreshLayout.setOnRefreshListener ( this );

        if (!isPlacesInitialized) {
            // Initialize the SDK
            Places.initialize ( getApplicationContext (), getString(R.string.google_maps_api_key));
            isPlacesInitialized = true;
        }
        requestLocationPermission ();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient ( this );

        int permission = ContextCompat.checkSelfPermission ( this, Manifest.permission.ACCESS_FINE_LOCATION );
        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i ( TAG, "Permission was not granted" );
            return;
        } else {
            displayAllProducts ();
            displayBottomNav ();
            swipeRefreshLayout.setRefreshing ( false );
        }
    }

    private void requestLocationPermission() {
        int permission = ContextCompat.checkSelfPermission ( this, Manifest.permission.ACCESS_FINE_LOCATION );
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions ( this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_ACCESS_CODE );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult ( requestCode, permissions, grantResults );
        switch(requestCode) {
            case LOCATION_ACCESS_CODE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    displayAllProducts ();
                    displayBottomNav ();
                } else {
                    Toast.makeText ( this, "This app needs to access your location to run properly.", Toast.LENGTH_SHORT ).show();
                }
        }
    }

    private void displayBottomNav() {
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById ( R.id.bottom_navigation );
        bottomNavigationView.setVisibility ( View.VISIBLE );
        bottomNavigationView.setOnNavigationItemSelectedListener ( new BottomNavigationView.OnNavigationItemSelectedListener () {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId ()) {
                    case R.id.action_add:
                        startActivity ( new Intent ( getApplicationContext (), PostProductActivity.class ) );
                        return true;
                    case R.id.action_account:
                        startActivity(new Intent (getApplicationContext (), ViewUserAccountActivity.class));
                        return true;
                    case R.id.action_home:
                        return true;
                }
                return false;
            }
        } );
    }

    private void displayAllProducts() {
        final List<Product> productList = new ArrayList<> ();
        collectionReferenceProduct
                .get ()
                .addOnSuccessListener ( new OnSuccessListener<QuerySnapshot> () {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty ()) {
                            for (QueryDocumentSnapshot productDocument : queryDocumentSnapshots) {
                                if(productDocument.get("sold").toString ().equals("true") ) {
                                    continue;
                                }

                                Product product = productDocument.toObject ( Product.class );

                                // set an id here to be able to retrieve the product later when
                                // the user click make offer
                                product.setProductId ( productDocument.getId () );
                                productList.add ( product );
                            }

                            getLocationAndSort ( productList );

                        } else {
                            Log.d ( "Document returned: ", "empty" );
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

    private void getLocationAndSort(final List<Product> list) {
        SharedPreferences sharedPref = this.getSharedPreferences ( getString ( R.string.preference_file_key ), Context.MODE_PRIVATE );
        sharedPref.edit ().remove ( "curr_lat" ).apply ();
        sharedPref.edit ().remove ( "curr_lon" ).apply ();

        int permission = ContextCompat.checkSelfPermission ( this, Manifest.permission.ACCESS_FINE_LOCATION );
        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i ( TAG, "Permission was not granted" );
            return;
        }

        fusedLocationProviderClient.getLastLocation ().addOnCompleteListener ( this, new OnCompleteListener<Location> () {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Log.d ( "task", "task complete" );
                if (task.isSuccessful () && task.getResult () != null) {
                    // put this in a separate function and call it sort.
                    Log.d ( "task", "success" );
                    Location location = task.getResult ();

                    final double currLat = location.getLatitude ();
                    final double currLon = location.getLongitude ();

                    SharedPreferences sharedPref = getSharedPreferences ( getString ( R.string.preference_file_key ), Context.MODE_PRIVATE );
                    SharedPreferences.Editor editor = sharedPref.edit ();
                    editor.putLong ( "curr_lat", Double.doubleToRawLongBits ( currLat ) );
                    editor.putLong ( "curr_lon", Double.doubleToRawLongBits ( currLon ) );
                    editor.commit ();

                    Collections.sort ( list, new Comparator<Product> () {
                        @Override
                        public int compare(Product product1, Product product2) {
                            double lat1 = product1.getCoordinates ().getLatitude ();
                            double lon1 = product1.getCoordinates ().getLongitude ();
                            double lat2 = product2.getCoordinates ().getLatitude ();
                            double lon2 = product2.getCoordinates ().getLongitude ();

                            double distant1 = DistanceCalculator.calculateDistanceMiles ( lat1, lon1, currLat, currLon );
                            double distant2 = DistanceCalculator.calculateDistanceMiles ( lat2, lon2, currLat, currLon );

                            double diff = distant1 - distant2;

                            if (diff == 0) {
                                return 0;
                            }

                            if (diff < 0) {
                                return -1;
                            }

                            return 1;
                        }
                    } );
                }
                productRecyclerAdapter = new ProductRecyclerAdapter ( MainActivity.this, list );
                recyclerView.setAdapter ( productRecyclerAdapter );
                recyclerView.setVisibility ( View.VISIBLE );
                productRecyclerAdapter.notifyDataSetChanged ();

            }
        } );
    }

    @Override
    public void onRefresh() {
        displayAllProducts ();
        swipeRefreshLayout.setRefreshing ( false );
    }

}
