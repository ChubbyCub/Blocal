package ui;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blocal.R;
import com.squareup.picasso.Picasso;

import java.util.List;

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
        View view = LayoutInflater.from(context).inflate(R.layout.product_row, parent, false);
        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.name.setText(product.getName());
        holder.location.setText(product.getLocation());

        String photoURL = product.getPhotoURL();

        // remove the placeholder after testing
        Picasso.get().load(photoURL).placeholder(R.drawable.running_shoes).fit().into(holder.image);

        String timeAgo = (String) DateUtils.getRelativeTimeSpanString(product.getDateAdded().getSeconds() * 1000);
        holder.dateAdded.setText(timeAgo);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Product card: ", "is clicked");
                context.startActivity(new Intent(context, ProductDetailActivity.class));
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name, location, dateAdded;
        public ImageView image;

        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);
            context = ctx;

            name = itemView.findViewById(R.id.product_name_list);
            location = itemView.findViewById(R.id.product_location_list);
            dateAdded = itemView.findViewById(R.id.product_timestamp_list);
            image = itemView.findViewById(R.id.product_image_list);
        }
    }


}
