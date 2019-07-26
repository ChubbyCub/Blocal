package ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blocal.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ReceivedOffersRecyclerAdapter extends RecyclerView.Adapter<ReceivedOffersRecyclerAdapter.ViewHolder> {
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
        DocumentReference df = db.collection ( "offers" ).document ( offerId );
        df.get ()
                .addOnCompleteListener ( new OnCompleteListener<DocumentSnapshot> () {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful ()) {
                            DocumentSnapshot document = task.getResult ();
                            String buyerId = document.get ( "buyerId" ).toString ();
                            holder.offerAmount.setText ( "$" + document.get ( "price" ).toString () );
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
        return receivedOffers.size ();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView offerAmount;
        public TextView buyerName;

        public ViewHolder(@NonNull View itemView, Context ctx) {
            super ( itemView );
            context = ctx;

            offerAmount = itemView.findViewById ( R.id.offer_amount_list );
            buyerName = itemView.findViewById ( R.id.offer_buyer_name );
        }
    }


}
