package com.example.blocal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.blocal.model.Chat;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

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
        final ArrayList<Chat> chats = new ArrayList<> ();
        // then query both seller and buyer id, then add them all in an array of chat objects.
        db.collection ( "chat" ).whereEqualTo ( "mSellerUid", currentUserId )
                .get()
                .addOnCompleteListener ( new OnCompleteListener<QuerySnapshot> () {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful ()) {
                            QuerySnapshot qs = task.getResult ();
                            if(qs != null) {
                                for(DocumentSnapshot document: qs) {
                                    Chat chat = document.toObject ( Chat.class );
                                    chats.add(chat);
                                }
                            }
                        }
                    }
                } );

        db.collection ( "chat" ).whereEqualTo ( "uid", currentUserId )
                .get ()
                .addOnCompleteListener ( new OnCompleteListener<QuerySnapshot> () {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful ()) {
                            QuerySnapshot qs = task.getResult ();
                            if(qs != null) {
                                for(DocumentSnapshot document : qs) {
                                    Chat chat = document.toObject ( Chat.class );
                                    chats.add ( chat );
                                }
                            }
                        }
                    }
                } );
    }

    @Override
    protected void onStart() {
        super.onStart ();
        firebaseAuth = FirebaseAuth.getInstance ();
        firebaseUser =firebaseAuth.getCurrentUser ();
        currentUserId = firebaseUser.getUid ();
    }
}
