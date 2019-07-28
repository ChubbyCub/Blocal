package com.example.blocal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;

import com.example.blocal.model.Product;

public class ViewUserAccountActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ViewUserAccountActivity";
    private TextView editProfile;
    private TextView manageTransaction;
    private Button signOutButton;
    private ImageView userAvatar;

    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    String currentUserId;
    Uri userProfileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_view_user_account );
        setRequestedOrientation ( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );

        db = FirebaseFirestore.getInstance ();

        manageTransaction = findViewById ( R.id.manage_transaction_btn );
        editProfile = findViewById ( R.id.edit_profile_btn );
        signOutButton = findViewById ( R.id.sign_out_btn );
        userAvatar = findViewById ( R.id.user_avatar );

        Picasso.get ().load ( userProfileUri ).placeholder ( R.drawable.ic_profile_placeholder ).fit ().centerCrop ().into ( userAvatar );

        manageTransaction.setOnClickListener ( this );
        editProfile.setOnClickListener ( this );
        signOutButton.setOnClickListener ( this );
    }


    @Override
    public void onClick(View view) {
        switch (view.getId ()) {
            case R.id.manage_transaction_btn:
                Intent intent = new Intent ( ViewUserAccountActivity.this, ManageTransactionActivity.class );
                startActivity(intent);
                break;
            case R.id.edit_profile_btn:
                break;
            case R.id.sign_out_btn:
                firebaseAuth.signOut ();
                startActivity ( new Intent ( this, SignInActivity.class ) );
                break;
        }
    }


    @Override
    protected void onStart() {
        super.onStart ();
        firebaseAuth = FirebaseAuth.getInstance ();
        user = firebaseAuth.getCurrentUser ();
        currentUserId = user.getUid ();
        userProfileUri = user.getPhotoUrl ();
    }
}
