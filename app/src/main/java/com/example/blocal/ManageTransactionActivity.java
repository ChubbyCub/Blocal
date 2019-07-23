package com.example.blocal;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;

import java.util.ArrayList;

public class ManageTransactionActivity extends AppCompatActivity implements TabHost.OnTabChangeListener {
    TabHost tabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_manage_transaction );
        final Bundle stringArrayList = getIntent().getExtras ();
        ArrayList<String> productIds = stringArrayList.getStringArrayList( "productIds" );
        Log.d("anything in here? ", productIds.toString ());

        tabHost = (TabHost) findViewById ( R.id.tabHost );
        tabHost.setup();

        TabHost.TabSpec spec = tabHost.newTabSpec ( "Sell" );
        spec.setContent ( R.id.tab1 );
        spec.setIndicator("Sell");
        tabHost.addTab(spec);

        spec = tabHost.newTabSpec ( "Buy" );
        spec.setContent ( R.id.tab2 );
        spec.setIndicator("Buy");
        tabHost.addTab(spec);

        tabHost.setOnTabChangedListener ( this );
    }

    @Override
    public void onTabChanged(String s) {
        switch(s) {
            case "Sell":
                break;
            case "Buy":
                break;
        }
    }
}
