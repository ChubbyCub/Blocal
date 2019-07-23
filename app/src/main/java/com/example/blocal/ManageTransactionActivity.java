package com.example.blocal;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

public class ManageTransactionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_manage_transaction );
        final Bundle stringArrayList = getIntent().getExtras ();
        ArrayList<String> productIds = stringArrayList.getStringArrayList( "productIds" );
        Log.d("anything in here? ", productIds.toString ());
    }
}
