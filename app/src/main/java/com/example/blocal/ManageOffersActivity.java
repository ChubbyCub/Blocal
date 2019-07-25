package com.example.blocal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blocal.R;
import com.example.blocal.model.Product;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squareup.picasso.Picasso;

public class ManageOffersActivity extends AppCompatActivity {
    private static final String TAG = "ManageOffersActivity";
    private Product product;
    private ImageView productImage;
    private TextView productName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_manage_offers );
        setRequestedOrientation ( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );

        displayBottomNav ();
        product = getIntent ().getParcelableExtra ( "product" );
        Log.d(TAG, product.getName());

        productImage = findViewById ( R.id.tool_bar_product_image );
        productName = findViewById ( R.id.tool_bar_product_name );

        Picasso.get ().load(product.getPhotoURL ()).fit().centerCrop ().into ( productImage );
        productName.setText(product.getName ());

    }

    private void displayBottomNav() {
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById ( R.id.bottom_navigation );
        bottomNavigationView.setVisibility ( View.VISIBLE );
        bottomNavigationView.setOnNavigationItemSelectedListener ( new BottomNavigationView.OnNavigationItemSelectedListener () {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId ()) {
                    case R.id.action_add:
                        startActivity ( new Intent ( getApplicationContext (), PostProductActivity.class ) );
                        return true;
                    case R.id.action_account:
                        startActivity(new Intent (getApplicationContext (), ViewUserAccountActivity.class));
                        return true;
                    case R.id.action_home:
                        startActivity(new Intent (getApplicationContext (), MainActivity.class));
                        return true;
                }
                return false;
            }
        } );
    }
}
