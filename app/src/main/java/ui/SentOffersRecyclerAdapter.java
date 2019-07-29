package ui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blocal.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.List;

import javax.annotation.Nullable;

public class SentOffersRecyclerAdapter extends RecyclerView.Adapter<SentOffersRecyclerAdapter.ViewHolder> {
    private Context context;
    private List<String> sentOffers;

    public SentOffersRecyclerAdapter(Context context, @NonNull List<String> sentOffers) {
        this.context = context;
        this.sentOffers = sentOffers;
    }

    @NonNull
    @Override
    public SentOffersRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from ( context ).inflate ( R.layout.sent_offer_row, parent, false );

        return new ViewHolder ( view, context );
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final String offerId = sentOffers.get ( position );

        // query the database to find the matching price
        final FirebaseFirestore db = FirebaseFirestore.getInstance ();
        Query query = db.collection ( "offers" ).orderBy ( "dateUpdated", Query.Direction.DESCENDING );
        query.addSnapshotListener ( new EventListener<QuerySnapshot> () {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(e != null) {
                    Log.e("ERROR:", e.getMessage ());
                }

                if(queryDocumentSnapshots != null) {
                    for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        if(!documentSnapshot.getId().equals(offerId)) {
                            continue;
                        }

                        NumberFormat format = NumberFormat.getCurrencyInstance ();
                        holder.myOffer.setText ("My offer: " + format.format ( documentSnapshot.get ( "price" ) ) );

                        String status = documentSnapshot.get ( "status" ).toString ();
                        if (status.equals ( "pending" )) {
                            holder.status.setImageResource ( R.drawable.ic_pending_status );
                        }

                        if (status.equals ( "accepted" )) {
                            holder.status.setImageResource ( R.drawable.ic_soft_accept_sent_offer );
                        }

                        if (status.equals ( "rejected" )) {
                            holder.status.setImageResource ( R.drawable.ic_deny_symbol );
                        }

                        String productId = documentSnapshot.get ( "productId" ).toString ();
                        DocumentReference product = db.collection ( "products" ).document ( productId );
                        product.get ()
                                .addOnCompleteListener ( new OnCompleteListener<DocumentSnapshot> () {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful ()) {
                                            DocumentSnapshot document = task.getResult ();
                                            Picasso.get ().load ( document.get ( "photoURL" ).toString () ).fit ()
                                                    .centerCrop ().into ( holder.productImage );
                                            holder.productName.setText ( document.get ( "name" ).toString () );
                                        }
                                    }
                                } );
                    }
                }
            }
        } );
    }


    @Override
    public int getItemCount() {
        return sentOffers.size ();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView productImage;
        public TextView productName;
        public TextView myOffer;
        public ImageView status;

        public ViewHolder(@NonNull View itemView, Context ctx) {
            super ( itemView );
            context = ctx;

            productImage = itemView.findViewById ( R.id.sent_offer_product_image );
            productName = itemView.findViewById ( R.id.sent_offer_product_name );
            myOffer = itemView.findViewById ( R.id.sent_offer_my_offer );
            status = itemView.findViewById ( R.id.sent_offer_status );
        }
    }


}
