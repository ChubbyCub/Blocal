package com.example.blocal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

import ui.SectionPageAdapter;

public class ManageTransactionActivity extends AppCompatActivity {
    private SectionPageAdapter mSectionPageAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_manage_transaction );
        final Bundle stringArrayList = getIntent().getExtras ();
        ArrayList<String> productIds = stringArrayList.getStringArrayList( "productIds" );
        Log.d("anything in here? ", productIds.toString ());

        mSectionPageAdapter = new SectionPageAdapter ( getSupportFragmentManager () );
        mViewPager = (ViewPager) findViewById ( R.id.container );
        setupViewPager ( mViewPager );

        TabLayout tabLayout = (TabLayout) findViewById ( R.id.tabs );
        tabLayout.setupWithViewPager ( mViewPager );
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionPageAdapter adapter = new SectionPageAdapter ( getSupportFragmentManager () );
        adapter.addFragment ( new SellTransaction (), "Sell" );
        adapter.addFragment ( new BuyTransaction (), "Buy" );

        viewPager.setAdapter ( adapter );
    }
}
