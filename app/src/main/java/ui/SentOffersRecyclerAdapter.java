package ui;

import android.content.Context;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;

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
        DocumentReference df = db.collection ( "offers" ).document ( offerId );
        df.get ()
                .addOnCompleteListener ( new OnCompleteListener<DocumentSnapshot> () {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful ()) {
                            DocumentSnapshot document = task.getResult ();

                            holder.myOffer.setText("My offer: " + document.get("price").toString ());

                            String status = document.get("status").toString ();
                            if(status.equals("pending")) {
                                holder.status.setImageResource ( R.drawable.ic_pending_status );
                            }

                            if(status.equals("accepted")) {
                                holder.status.setImageResource ( R.drawable.ic_soft_accept_sent_offer );
                            }

                            if(status.equals("rejected")) {
                                holder.status.setImageResource ( R.drawable.ic_deny_symbol );
                            }

                            String productId = document.get ( "productId" ).toString ();
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
                } );

//        holder.itemView.setOnClickListener ( new View.OnClickListener () {
//            @Override
//            public void onClick(View view) {
//                Log.i ( "Product card: ", "is clicked" );
//                Intent intent = new Intent ( context, ProductDetailActivity.class ).putExtra ( "product", product );
//                intent.setExtrasClassLoader ( Offer.class.getClassLoader () );
//                context.startActivity ( intent );
//            }
//        } );

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
