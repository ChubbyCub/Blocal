package com.example.blocal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int FIREBASE_LOGIN_CODE = 5566; // any num you want
    private FirebaseFirestore db;
    ImageView downloadedImage;

    private boolean isSignedIn() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user != null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("onCreate is called", "yes");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!isSignedIn()) {
            showSignInOptions();
        } else {
            displayBottomNav();
            getAllProducts();
            // displayImage();
            // displaySignOutButton();
        }
    }

    private void displayBottomNav() {
        BottomNavigationView bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId()) {
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

    private void getAllProducts() {
        final List<String> products = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        db.collection("products")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot document: task.getResult()) {
                                products.add("" + document.get("name"));
                                // Log.d("Document downloaded: ", document.getId() + " => " + document.getData());
                            }
                            displayBottomNav();
                            // displayProductList(products);
                        } else {
                            Log.w("ERROR: ", task.getException());
                        }
                    }
                });
    }

    private void displayProductList() {

    }

//    private void displayProductList(List<String> products) {
//        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
//                this,
//                android.R.layout.simple_list_item_1,
//                products
//        );
//
//        ListView productList = (ListView)findViewById(R.id.product_list);
//        productList.setAdapter(arrayAdapter);
//        productList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                // display product details when clicked...
//            }
//        });
//    }


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
                // displayImage();
                displaySignOutButton();
                getAllProducts();
            } else {
                Toast.makeText(this, "" + response.getError().getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void displaySignOutButton() {
        final Button signOutButton = (Button) findViewById(R.id.btn_sign_out);
        signOutButton.setVisibility(View.VISIBLE);
        signOutButton.setEnabled(true);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AuthUI.getInstance()
                        .signOut(MainActivity.this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                showSignInOptions();
                                signOutButton.setEnabled(false);
                                signOutButton.setVisibility(View.INVISIBLE);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "" + e.getMessage(),
                                Toast.LENGTH_SHORT);
                    }
                });
            }
        });
    }

//    private void displayImage() {
//        // reset this somehow to display the image
//        downloadedImage = (ImageView) findViewById(R.id.imageView);
//
//        ImageDownloader task = new ImageDownloader();
//        String link = "https://firebasestorage.googleapis.com/v0/b/blocal-47851.appspot.com/o/Test%2" +
//                "Fshoes.jpg?alt=media&token=79a6155b-13f5-4bad-b3ad-acb61fd02b86";
//
//        try {
//            Bitmap myImage = task.execute(link).get();
//            downloadedImage.setImageBitmap(myImage);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


    private static class ImageDownloader extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                // convert the data that was downloaded to an image
                Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);
                return myBitmap;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
