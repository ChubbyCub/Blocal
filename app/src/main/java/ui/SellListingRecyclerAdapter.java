package ui;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blocal.R;
import com.example.blocal.ManageOffersActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import com.example.blocal.model.Product;

public class SellListingRecyclerAdapter extends RecyclerView.Adapter<SellListingRecyclerAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Product> listings;

    public SellListingRecyclerAdapter(Context context, ArrayList<Product> listings) {
        this.context = context;
        this.listings = listings;
    }

    @NonNull
    @Override
    public SellListingRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from ( context ).inflate ( R.layout.listing_row, parent, false );

        return new ViewHolder ( view, context );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Product product = listings.get ( position );
        holder.productName.setText((product.getName ()));

        Picasso.get ()
                .load ( product.getPhotoURL () )
                .placeholder ( R.drawable.ic_image_placeholder )
                .fit ().centerCrop ().into ( holder.productImage );

        holder.seeAllOffersButton.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                Log.i ( "See all offers button", "is clicked" );
                Intent intent = new Intent(context, ManageOffersActivity.class);
                intent.putExtra("product", product);
                context.startActivity(intent);
            }
        } );

    }


    @Override
    public int getItemCount() {
        return listings.size ();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView productName;
        public ImageView productImage;
        public Button seeAllOffersButton;

        public ViewHolder(@NonNull View itemView, Context ctx) {
            super ( itemView );
            context = ctx;

            productName = itemView.findViewById ( R.id.listing_name );
            productImage = itemView.findViewById ( R.id.listing_thumbnail );
            seeAllOffersButton = itemView.findViewById ( R.id.see_all_offers_btn );
        }
    }
}
