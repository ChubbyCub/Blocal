package com.example.blocal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
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
import com.google.firebase.firestore.Query;
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
import util.ProductApi;

public class MainActivity extends AppCompatActivity {
    private static final int FIREBASE_LOGIN_CODE = 5566; // any num you want
    private static final String TAG = "MainActivity";
    private List<Product> productList;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ProductRecyclerAdapter productRecyclerAdapter;
    private FirebaseFirestore db;
    private CollectionReference collectionReferenceProduct;
    private CollectionReference collectionReferenceUser;
    private FirebaseAuth firebaseAuth;


    private boolean isSignedIn() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user != null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("onCreate is called", "yes");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        collectionReferenceProduct = db.collection("products");
        collectionReferenceUser = db.collection("users");

        productList = new ArrayList<>();
        recyclerView = findViewById(R.id.product_recycler_view);
        recyclerView.setVisibility(View.INVISIBLE);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        progressBar = findViewById(R.id.progress_bar_list);
        progressBar.setVisibility(View.VISIBLE);

        if (!isSignedIn()) {
            showSignInOptions();
        } else {
            displayBottomNav();
            displayAllProducts();
        }
    }

    private void displayBottomNav() {
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setVisibility(View.VISIBLE);
        bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_add:
                        Toast.makeText(getApplicationContext(), "Action Add Clicked", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_account:
                        Toast.makeText(getApplicationContext(), "Action Account clicked", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_home:
                        Toast.makeText(getApplicationContext(), "Action Home Clicked", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    private void displayAllProducts() {
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
                            progressBar.setVisibility(View.INVISIBLE);
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
            // TODO: create a user right here
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                addNewUser(firebaseAuth);
                displayBottomNav();
                displayAllProducts();
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

        // you cannot overwrite the user every single time. bad idea.
        userDf.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(Objects.requireNonNull(task.getResult().exists())) {
                    return;
                } else {
                    userDf.set(userObj).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            ProductApi productApi = ProductApi.getInstance();
                            productApi.setUserId(userId);
                            productApi.setUserEmail(userDisplayName);
                            productApi.setUserEmail(userEmail);
                        }
                    });
                }
            }
        });
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
