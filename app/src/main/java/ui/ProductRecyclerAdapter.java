package ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blocal.ProductDetailActivity;
import com.example.blocal.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.firestore.GeoPoint;
import com.squareup.picasso.Picasso;

import java.nio.DoubleBuffer;
import java.util.List;

import model.DistanceCalculator;
import model.Product;

public class ProductRecyclerAdapter extends RecyclerView.Adapter<ProductRecyclerAdapter.ViewHolder> {
    private Context context;
    private List<Product> productList;

    public ProductRecyclerAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from ( context ).inflate ( R.layout.product_row, parent, false );

        return new ViewHolder ( view, context );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Product product = productList.get ( position );
        holder.name.setText ( product.getName () );
        SharedPreferences sharedPref = context.getSharedPreferences ( context.getString ( R.string.preference_file_key ), Context.MODE_PRIVATE );

        Double curr_lat = Double.longBitsToDouble ( sharedPref.getLong ( "curr_lat", Double.doubleToLongBits ( 0.00 ) ) );
        Double curr_lon = Double.longBitsToDouble ( sharedPref.getLong ( "curr_lon", Double.doubleToLongBits ( 0.00 ) ) );

        if (curr_lat == 0.00 && curr_lon == 0.00) {
            holder.location.setVisibility ( View.INVISIBLE );
        } else {
            Double miles = DistanceCalculator.calculateDistanceMiles (
                    product.getCoordinates ().getLatitude (),
                    product.getCoordinates ().getLongitude (),
                    curr_lat,
                    curr_lon );

            holder.location.setText ( Math.floor ( miles * 10 ) / 10 + " miles away" );
        }

        String photoURL = product.getPhotoURL ();

        Picasso.get ().load ( photoURL ).placeholder ( R.drawable.ic_image_placeholder ).fit ().centerCrop ().into ( holder.image );

        String timeAgo = (String) DateUtils.getRelativeTimeSpanString ( product.getDateAdded ().getSeconds () * 1000 );
        holder.dateAdded.setText ( timeAgo );

        holder.itemView.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                Log.i ( "Product card: ", "is clicked" );
                Intent intent = new Intent ( context, ProductDetailActivity.class ).putExtra ( "product", product );
                context.startActivity ( intent );
            }
        } );

    }


    @Override
    public int getItemCount() {
        return productList.size ();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name, location, dateAdded;
        public ImageView image;

        public ViewHolder(@NonNull View itemView, Context ctx) {
            super ( itemView );
            context = ctx;

            name = itemView.findViewById ( R.id.product_name_list );
            location = itemView.findViewById ( R.id.product_location_list );
            dateAdded = itemView.findViewById ( R.id.product_timestamp_list );
            image = itemView.findViewById ( R.id.product_image_list );
        }
    }


}
