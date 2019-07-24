package com.example.blocal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

import com.example.blocal.model.Product;
import ui.SectionPageAdapter;

public class ManageTransactionActivity extends AppCompatActivity {
    private static final String TAG = "ManageTransactionActivity";

    private SellTransaction sellTransactionFragment;
    private BuyTransaction buyTransactionFragment;
    private ViewPager mViewPager;
    private ArrayList<Product> listings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_manage_transaction );
        setRequestedOrientation ( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );

        listings = getIntent ().getParcelableArrayListExtra ( "listings" );


        sellTransactionFragment = new SellTransaction ();
        buyTransactionFragment = new BuyTransaction ();

        mViewPager = (ViewPager) findViewById ( R.id.container );
        setupViewPager ( mViewPager );

        TabLayout tabLayout = (TabLayout) findViewById ( R.id.tabs );
        tabLayout.setupWithViewPager ( mViewPager );

        sendDataToFragment ();

        displayBottomNav ();
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionPageAdapter adapter = new SectionPageAdapter ( getSupportFragmentManager () );
        adapter.addFragment ( sellTransactionFragment, "Sell" );
        adapter.addFragment ( buyTransactionFragment, "Buy" );
        viewPager.setAdapter ( adapter );
    }

    private void sendDataToFragment() {
        Bundle mBundle = new Bundle ();
        mBundle.putParcelableArrayList ( "listings", listings );
        sellTransactionFragment.setArguments ( mBundle );
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
                        startActivity ( new Intent ( getApplicationContext (), ViewUserAccountActivity.class ) );
                        return true;
                    case R.id.action_home:
                        startActivity ( new Intent ( getApplicationContext (), MainActivity.class ) );
                        return true;
                }
                return false;
            }
        } );
    }
}
