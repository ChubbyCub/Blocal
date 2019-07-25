package com.example.blocal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignInActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private static final int FIREBASE_LOGIN_CODE = 5566; // any num you want
    private FirebaseFirestore db;
    private CollectionReference collectionReferenceUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_sign_in );
        setRequestedOrientation ( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );
        db = FirebaseFirestore.getInstance ();
        firebaseAuth = FirebaseAuth.getInstance ();
        collectionReferenceUser = db.collection ( "users" );

        if(isSignedIn ()) {
            startActivity(new Intent(this, MainActivity.class));
        } else {
            showSignInOptions ();
        }

    }

    private boolean isSignedIn() {
        FirebaseUser user = firebaseAuth.getCurrentUser ();
        return user != null;
    }

    private void showSignInOptions() {
        startActivityForResult (
                AuthUI.getInstance ().createSignInIntentBuilder ()
                        .setAvailableProviders ( Arrays.asList (
                                new AuthUI.IdpConfig.EmailBuilder ().build (),
                                new AuthUI.IdpConfig.FacebookBuilder ().build (),
                                new AuthUI.IdpConfig.GoogleBuilder ().build ()
                        ) )
                        .setLogo ( R.drawable.bart )
                        .setTheme ( R.style.MyTheme )
                        .build (), FIREBASE_LOGIN_CODE
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult ( requestCode, resultCode, data );
        if (requestCode == FIREBASE_LOGIN_CODE) {
            IdpResponse response = IdpResponse.fromResultIntent ( data );
            if (resultCode == RESULT_OK) {
                addNewUser ( firebaseAuth );
                startActivity(new Intent(this, MainActivity.class));
            } else {
                Toast.makeText ( this, "" + response.getError ().getMessage (),
                        Toast.LENGTH_SHORT ).show ();
            }
        }
    }

    private void addNewUser(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser ();

        final String userId = user.getUid ();
        final String userDisplayName = user.getDisplayName ();
        final String userEmail = user.getEmail ();

        // Create a user Map so we can create a user in the user collection
        final Map<String, Object> userObj = new HashMap<> ();
        userObj.put ( "userId", userId );
        userObj.put ( "userDisplayName", userDisplayName );
        userObj.put ( "userEmail", userEmail );

        ArrayList<String> acceptedOffers = new ArrayList<> ();
        userObj.put ("acceptedOffers", acceptedOffers);

        ArrayList<String> rejectedOffers = new ArrayList<> ();
        userObj.put ("rejectedOffers", rejectedOffers);

        ArrayList<String> receivedOffers = new ArrayList<> ();
        userObj.put("receivedOffers", receivedOffers);

        ArrayList<String> sentOffers = new ArrayList<> ();
        userObj.put("sentOffers", sentOffers);

        final DocumentReference userDf = collectionReferenceUser.document ( userId );

        userDf.get ().addOnCompleteListener ( new OnCompleteListener<DocumentSnapshot> () {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (Objects.requireNonNull ( task.getResult ().exists () )) {
                    return;
                } else {
                    userDf.set ( userObj ).addOnSuccessListener ( new OnSuccessListener<Void> () {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d ( "Success: ", "Added a new user" );
                        }
                    } );
                }
            }
        } );
    }
}
