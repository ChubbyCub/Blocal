package ui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blocal.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Time;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;

import at.markushi.ui.CircleButton;

public class ReceivedOffersRecyclerAdapter extends RecyclerView.Adapter<ReceivedOffersRecyclerAdapter.ViewHolder> {
    private static final String TAG = "ReceivedOffersRecyclerAdapter";
    private Context context;
    private List<String> receivedOffers;

    public ReceivedOffersRecyclerAdapter(Context context, @NonNull List<String> receivedOffers) {
        this.context = context;
        this.receivedOffers = receivedOffers;
    }

    @NonNull
    @Override
    public ReceivedOffersRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from ( context ).inflate ( R.layout.received_offer_row, parent, false );

        return new ViewHolder ( view, context );
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final String offerId = receivedOffers.get ( position );

        // query the database to find the matching price
        final FirebaseFirestore db = FirebaseFirestore.getInstance ();
        final CollectionReference offers = db.collection ( "offers" );

        final DocumentReference df = offers.document ( offerId );
        df.get ()
                .addOnCompleteListener ( new OnCompleteListener<DocumentSnapshot> () {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful ()) {
                            DocumentSnapshot document = task.getResult ();
                            String buyerId = document.get ( "buyerId" ).toString ();

                            NumberFormat format = NumberFormat.getCurrencyInstance ();
                            holder.offerAmount.setText ( format.format ( document.get ( "price" ) ) );

                            String status = document.get ( "status" ).toString ();

                            if (status.equals ( "accepted" )) {
                                holder.acceptButton.setVisibility ( View.VISIBLE );
                                holder.rejectButton.setVisibility ( View.INVISIBLE );
                            }

                            if (status.equals ( "rejected" )) {
                                holder.rejectButton.setVisibility ( View.VISIBLE );
                                holder.acceptButton.setVisibility ( View.INVISIBLE );
                            }

                            // query the database to find the matching buyer name
                            DocumentReference buyer = db.collection ( "users" ).document ( buyerId );
                            buyer.get ()
                                    .addOnCompleteListener ( new OnCompleteListener<DocumentSnapshot> () {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful ()) {
                                                DocumentSnapshot document = task.getResult ();
                                                String userDisplayName = document.get ( "userDisplayName" ).toString ();
                                                holder.buyerName.setText ( userDisplayName );
                                            }
                                        }
                                    } );
                        }
                    }
                } );

        handleAcceptOffer ( holder, df, offerId, offers );
        handleRejectOffer ( holder, df );
    }


    private void handleAcceptOffer(@NonNull final ViewHolder holder,
                                   final DocumentReference df,
                                   final String offerId,
                                   final CollectionReference offers) {
        holder.acceptButton.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                // update status of the existing offer
                df.update ( "status", "accepted" );
                df.update ( "dateUpdated", new Timestamp ( new Date () ) );
                df.update ( "productState", true );

                // query the offers collection
                for (String id : receivedOffers) {
                    if (offerId.equals ( id )) {
                        continue;
                    }
                    offers.document ( id ).update ( "status", "rejected" );
                }
                queryProduct ( df );
            }
        } );
    }

    private void queryProduct(final DocumentReference df) {
        df.get ()
                .addOnCompleteListener ( new OnCompleteListener<DocumentSnapshot> () {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful ()) {
                            DocumentSnapshot document = task.getResult ();
                            String productId = document.get ( "productId" ).toString ();
                            FirebaseFirestore db = FirebaseFirestore.getInstance ();
                            DocumentReference soldProduct = db.collection ( "products" ).document ( productId );
                            soldProduct.update ( "sold", true );
                        }
                    }
                } );
    }

    private void handleRejectOffer(@NonNull final ViewHolder holder, final DocumentReference df) {
        holder.rejectButton.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                df.update ( "status", "rejected" );
                df.update ( "dateUpdated", new Timestamp ( new Date () ) );
            }
        } );
    }


    @Override
    public int getItemCount() {
        return receivedOffers.size ();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView offerAmount;
        public TextView buyerName;
        public CircleButton acceptButton;
        public CircleButton rejectButton;

        public ViewHolder(@NonNull View itemView, Context ctx) {
            super ( itemView );
            context = ctx;

            offerAmount = itemView.findViewById ( R.id.offer_amount_list );
            buyerName = itemView.findViewById ( R.id.offer_buyer_name );
            acceptButton = itemView.findViewById ( R.id.offer_accept_btn );
            rejectButton = itemView.findViewById ( R.id.offer_deny_btn );
        }
    }


}
