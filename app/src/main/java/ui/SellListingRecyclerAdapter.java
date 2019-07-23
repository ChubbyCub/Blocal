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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blocal.ProductDetailActivity;
import com.example.blocal.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import model.DistanceCalculator;
import model.Product;

public class SellListingRecyclerAdapter extends RecyclerView.Adapter<SellListingRecyclerAdapter.ViewHolder> {
    private Context context;
    private List<String> photoUrls;

    public SellListingRecyclerAdapter(Context context, List<String> photoUrls) {
        this.context = context;
        this.photoUrls = photoUrls;
    }

    @NonNull
    @Override
    public SellListingRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from ( context ).inflate ( R.layout.product_row, parent, false );

        return new ViewHolder ( view, context );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final String url = photoUrls.get ( position );

        Picasso.get ().load ( url ).placeholder ( R.drawable.ic_image_placeholder ).fit ().centerCrop ().into ( holder.image );

//        holder.itemView.setOnClickListener ( new View.OnClickListener () {
//            @Override
//            public void onClick(View view) {
//                Log.i ( "Product card: ", "is clicked" );
//                Intent intent = new Intent ( context, ProductDetailActivity.class ).putExtra ( "product", product );
//                context.startActivity ( intent );
//            }
//        } );

    }


    @Override
    public int getItemCount() {
        return photoUrls.size ();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;

        public ViewHolder(@NonNull View itemView, Context ctx) {
            super ( itemView );
            context = ctx;

            image = itemView.findViewById ( R.id.product_image_sell_transaction );
        }
    }


}
