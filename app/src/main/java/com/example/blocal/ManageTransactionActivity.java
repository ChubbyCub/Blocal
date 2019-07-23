package com.example.blocal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

import ui.SectionPageAdapter;

public class ManageTransactionActivity extends AppCompatActivity {
    private SectionPageAdapter mSectionPageAdapter;
    private SellTransaction sellTransactionFragment;
    private BuyTransaction buyTransactionFragment;
    private ViewPager mViewPager;
    private ArrayList<String> productIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_manage_transaction );
        final Bundle stringArrayList = getIntent().getExtras ();
        productIds = stringArrayList.getStringArrayList( "productIds" );
        Log.d("anything in here? ", productIds.toString ());

        sellTransactionFragment = new SellTransaction ();
        buyTransactionFragment = new BuyTransaction ();

        mSectionPageAdapter = new SectionPageAdapter ( getSupportFragmentManager () );
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
        Bundle mbundle = new Bundle();
        mbundle.putStringArrayList ( "productIds", productIds );
        sellTransactionFragment.setArguments ( mbundle );
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
                        Toast.makeText ( getApplicationContext (), "Action Account clicked", Toast.LENGTH_SHORT ).show ();
                        return true;
                    case R.id.action_home:
                        startActivity(new Intent (getApplicationContext (), MainActivity.class));
                        Toast.makeText ( getApplicationContext (), "Action Home Clicked", Toast.LENGTH_SHORT ).show ();
                        return true;
                }
                return false;
            }
        } );
    }
}
