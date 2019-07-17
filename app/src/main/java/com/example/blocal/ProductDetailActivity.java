package com.example.blocal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.media.Image;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import model.Product;

public class ProductDetailActivity extends AppCompatActivity {
    private TextView productName;
    private TextView productLocation;
    private TextView productTimestamp;
    private TextView productDescription;
    private TextView productPrice;
    private ImageView productImage;
    private TextView sellerName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Product product = getIntent().getParcelableExtra("product");

        productName = (TextView) findViewById(R.id.product_name_main);
        productLocation = (TextView) findViewById(R.id.product_location_main);
        productTimestamp = (TextView) findViewById(R.id.product_timestamp_main);
        productImage = (ImageView) findViewById(R.id.product_image_main);
        productDescription = findViewById(R.id.product_description_main);
        productPrice = findViewById(R.id.product_price_main);
        sellerName = findViewById(R.id.seller_name_main);

        productName.setText(product.getName());
        productLocation.setText(product.getLocation());
        productDescription.setText(product.getDescription());
        productPrice.setText("$" + Double.toString(product.getPrice()));

        // TODO: set user name

        // TODO: find a way to refactor this piece of code to translate time
        String timeAgo = (String) DateUtils.getRelativeTimeSpanString(product.getDateAdded().getSeconds() * 1000);
        productTimestamp.setText(timeAgo);


        // TODO: find a way to refactor this piece of code to edit picture
        String photoURL = product.getPhotoURL();
        Picasso.get().load(photoURL).placeholder(R.drawable.running_shoes).fit().centerCrop().into(productImage);

        Log.i("Name of the product card: ", product.getName());
    }
}
