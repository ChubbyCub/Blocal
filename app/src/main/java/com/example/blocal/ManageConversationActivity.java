package com.example.blocal;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ManageConversationActivity extends AppCompatActivity {
    private String currentUserId;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_manage_conversation );

        db = FirebaseFirestore.getInstance ();
    }

    private void queryConversations() {

        // create a chat object with specified fields matching things showing in Firestore
        // then query both seller and buyer id, then add them all in an array of chat objects.
        // then try to send this array to an adapter to display a list

    }

    @Override
    protected void onStart() {
        super.onStart ();
        firebaseAuth = FirebaseAuth.getInstance ();
        firebaseUser =firebaseAuth.getCurrentUser ();
        currentUserId = firebaseUser.getUid ();
    }
}
