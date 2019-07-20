package com.example.blocal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
// import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import model.Product;
import ui.ProductRecyclerAdapter;

import static com.google.gson.internal.$Gson$Types.arrayOf;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private static final int FIREBASE_LOGIN_CODE = 5566; // any num you want
    private static final int LOCATION_ACCESS_CODE = 777;
    private static final String TAG = "MainActivity";

    private RecyclerView recyclerView;
    private ProductRecyclerAdapter productRecyclerAdapter;
    private FirebaseFirestore db;
    private CollectionReference collectionReferenceProduct;
    private CollectionReference collectionReferenceUser;
    private FirebaseAuth firebaseAuth;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        collectionReferenceProduct = db.collection("products");
        collectionReferenceUser = db.collection("users");

        recyclerView = findViewById(R.id.product_recycler_view);
        recyclerView.setVisibility(View.INVISIBLE);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeRefreshLayout.setRefreshing(true);
        swipeRefreshLayout.setOnRefreshListener(this);

        checkPermission();

        if (!isSignedIn()) {
            showSignInOptions();
        } else {
            displayBottomNav();
            displayAllProducts();
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private boolean isSignedIn() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user != null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult ( requestCode, permissions, grantResults );
        switch (requestCode) {
            case LOCATION_ACCESS_CODE: {
                if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "Permission has been denied by user");
                } else {
                    Log.i(TAG, "Permission has been granted by user");
                    LocationManager locationManager = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);
                    Boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                    Log.d("GPS enabled: ", String.valueOf(isGpsEnabled));

                    checkPermission ();
                    Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    Log.i("loc: ", String.valueOf(location.getLatitude()));
                    Log.i("loc: ", String.valueOf(location.getLongitude()));
                }
                return;
            }
        }
    }

    private void checkPermission() {
        int permission = ContextCompat.checkSelfPermission ( this, Manifest.permission.ACCESS_FINE_LOCATION );
        if(permission != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission to access location denied");
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, LOCATION_ACCESS_CODE);
        }
    }

    private void displayBottomNav() {
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setVisibility(View.VISIBLE);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_add:
                        startActivity(new Intent(getApplicationContext(), PostProductActivity.class));
                        Toast.makeText(getApplicationContext(), "Action Add Clicked", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.action_account:
                        Toast.makeText(getApplicationContext(), "Action Account clicked", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.action_home:
                        Toast.makeText(getApplicationContext(), "Action Home Clicked", Toast.LENGTH_SHORT).show();
                        return true;
                }
                return false;
            }
        });
    }

    private void displayAllProducts() {

        final List<Product> productList = new ArrayList<>();
        collectionReferenceProduct
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (QueryDocumentSnapshot products : queryDocumentSnapshots) {
                                Product product = products.toObject(Product.class);
                                productList.add(product);
                            }
                            productRecyclerAdapter = new ProductRecyclerAdapter(MainActivity.this,
                                    productList);
                            recyclerView.setAdapter(productRecyclerAdapter);
                            // progressBar.setVisibility(View.INVISIBLE);
                            recyclerView.setVisibility(View.VISIBLE);
                            productRecyclerAdapter.notifyDataSetChanged();
                        } else {
                            Log.d("Document returned: ", "empty");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("ERROR: ", e.getMessage());
                    }
                });
    }

    private void showSignInOptions() {
        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder()
                        .setAvailableProviders(Arrays.asList(
                                new AuthUI.IdpConfig.EmailBuilder().build(),
                                new AuthUI.IdpConfig.FacebookBuilder().build(),
                                new AuthUI.IdpConfig.GoogleBuilder().build()
                        ))
                        .setLogo(R.drawable.bart)
                        .setTheme(R.style.MyTheme)
                        .build(), FIREBASE_LOGIN_CODE
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FIREBASE_LOGIN_CODE) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                addNewUser(firebaseAuth);
                displayBottomNav();
                displayAllProducts();
                swipeRefreshLayout.setRefreshing(false);
            } else {
                Toast.makeText(this, "" + response.getError().getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void addNewUser(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser();

        final String userId = user.getUid();
        final String userDisplayName = user.getDisplayName();
        final String userEmail = user.getEmail();

        // Create a user Map so we can create a user in the user collection
        final Map<String, String> userObj = new HashMap<>();
        userObj.put("userId", userId);
        userObj.put("userDisplayName", userDisplayName);
        userObj.put("userEmail", userEmail);

        final DocumentReference userDf = collectionReferenceUser.document(userId);

        userDf.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(Objects.requireNonNull(task.getResult().exists())) {
                    return;
                } else {
                    userDf.set(userObj).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("Success: ", "Added a new user");
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        displayAllProducts();
        swipeRefreshLayout.setRefreshing(false);
    }


    // rewrite this method to use the udemy signout code
//    private void displaySignOutButton() {
//        final Button signOutButton = (Button) findViewById(R.id.btn_sign_out);
//        signOutButton.setVisibility(View.VISIBLE);
//        signOutButton.setEnabled(true);
//        signOutButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                AuthUI.getInstance()
//                        .signOut(MainActivity.this)
//                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                showSignInOptions();
//                                signOutButton.setEnabled(false);
//                                signOutButton.setVisibility(View.INVISIBLE);
//                            }
//                        }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(MainActivity.this, "" + e.getMessage(),
//                                Toast.LENGTH_SHORT);
//                    }
//                });
//            }
//        });
//    }
}
