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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int FIREBASE_LOGIN_CODE = 5566; // any num you want

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
            displayImage();
            displaySignOutButton();
        }
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
                displayImage();
                displaySignOutButton();
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

    private void displayImage() {
        downloadedImage = (ImageView) findViewById(R.id.imageView);

        ImageDownloader task = new ImageDownloader();
        String link = "https://firebasestorage.googleapis.com/v0/b/blocal-47851.appspot.com/o/Test%2" +
                "Fshoes.jpg?alt=media&token=79a6155b-13f5-4bad-b3ad-acb61fd02b86";

        try {
            Bitmap myImage = task.execute(link).get();
            downloadedImage.setImageBitmap(myImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private class ImageDownloader extends AsyncTask<String, Void, Bitmap> {

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
