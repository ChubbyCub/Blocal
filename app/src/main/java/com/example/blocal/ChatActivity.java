package com.example.blocal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blocal.model.ChatHolder;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blocal.model.Chat;

import com.example.blocal.model.ImeHelper;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "FirestoreChatActivity";

    private static final CollectionReference sChatCollection =
            FirebaseFirestore.getInstance().collection("chats");
    /** Get the last 50 chat messages ordered by timestamp . */
    private static final Query sChatQuery =
            sChatCollection.orderBy("timestamp", Query.Direction.DESCENDING).limit(50);

    static {
        FirebaseFirestore.setLoggingEnabled(true);
    }

    // @BindView(R.id.messagesList)
    RecyclerView mRecyclerView;

    // @BindView(R.id.sendButton)
    Button mSendButton;

    // @BindView(R.id.messageEdit)
    EditText mMessageEdit;

    // @BindView(R.id.emptyTextView)
    TextView mEmptyListMessage;


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "On create get called");
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_chat );
        setRequestedOrientation ( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );

        mRecyclerView = findViewById ( R.id.messagesList );
        mSendButton = findViewById ( R.id.sendButton );
        mMessageEdit = findViewById ( R.id.messageEdit );
        mEmptyListMessage = findViewById ( R.id.emptyTextView );

        ButterKnife.bind(this);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(manager);

        mRecyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    mRecyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mRecyclerView.smoothScrollToPosition(0);
                        }
                    }, 100);
                }
            }
        });

        ImeHelper.setImeOnDoneListener(mMessageEdit, new ImeHelper.DonePressedListener() {
            @Override
            public void onDonePressed() {
                Log.d(TAG, "After I click send, where does it go?");
                onSendClick();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isSignedIn()) { attachRecyclerViewAdapter(); }
    }


    private boolean isSignedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    private void attachRecyclerViewAdapter() {
        final RecyclerView.Adapter adapter = newAdapter();

        // Scroll to bottom on new messages
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                mRecyclerView.smoothScrollToPosition(0);
            }
        });

        mRecyclerView.setAdapter(adapter);
    }

    @OnClick(R.id.sendButton)
    public void onSendClick() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String name = "User " + uid.substring(0, 6);

        onAddMessage(new Chat (name, mMessageEdit.getText().toString(), uid));

        mMessageEdit.setText("");
    }

    @NonNull
    private RecyclerView.Adapter newAdapter() {
        FirestoreRecyclerOptions<Chat> options =
                new FirestoreRecyclerOptions.Builder<Chat>()
                        .setQuery(sChatQuery, Chat.class)
                        .setLifecycleOwner(this)
                        .build();

        return new FirestoreRecyclerAdapter<Chat, ChatHolder> (options) {
            @NonNull
            @Override
            public ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new ChatHolder( LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.message, parent, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull ChatHolder holder, int position, @NonNull Chat model) {
                holder.bind(model);
            }

            @Override
            public void onDataChanged() {
                // If there are no chat messages, show a view that invites the user to add a message.
                mEmptyListMessage.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
            }
        };
    }

    private void onAddMessage(@NonNull Chat chat) {
        sChatCollection.add(chat).addOnFailureListener(this, new OnFailureListener () {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Failed to write message", e);
            }
        });
    }
}
