package com.example.blocal;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ViewUserAccountActivity extends AppCompatActivity implements View.OnClickListener {
    private Button manageTransactionButton;
    private Button editProfile;
    private Button signOutButton;

    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_view_user_account );
        manageTransactionButton = findViewById ( R.id.manage_transaction_btn );
        editProfile = findViewById ( R.id.edit_profile_btn );
        signOutButton = findViewById ( R.id.sign_out_btn );

        manageTransactionButton.setOnClickListener ( this );
        editProfile.setOnClickListener ( this );
        signOutButton.setOnClickListener ( this );

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.manage_transaction_btn:
                break;
            case R.id.edit_profile_btn:
                break;
            case R.id.sign_out_btn:
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart ();

    }
}
