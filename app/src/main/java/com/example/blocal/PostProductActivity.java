package com.example.blocal;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;

import com.squareup.picasso.Picasso;

import java.io.File;

public class PostProductActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int GALLERY_CODE = 1;

    private ImageButton takePhotoButton;
    private ImageButton uploadPhotoButton;
    private EditText productNameText;
    private Spinner productCategorySpinner;
    private EditText productDescriptionText;
    private EditText productLocationText;
    private EditText productPriceText;
    private Button postProductButton;
    private Uri productImageUri;
    private ImageView productImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_product);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        takePhotoButton = findViewById(R.id.take_photo_button);
        uploadPhotoButton = findViewById(R.id.upload_photo_button);
        productNameText = findViewById(R.id.product_name_post);
        productCategorySpinner = findViewById(R.id.category_spinner_post);
        productDescriptionText = findViewById(R.id.product_description_post);
        productLocationText = findViewById(R.id.product_location_post);
        productPriceText = findViewById(R.id.product_price_post);
        postProductButton = findViewById(R.id.post_product_button);
        productImageView = findViewById(R.id.product_image_post);

        takePhotoButton.setOnClickListener(this);
        uploadPhotoButton.setOnClickListener(this);



    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.take_photo_button:
                // saveJournal();
                break;
            case R.id.upload_photo_button:
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_CODE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_CODE && resultCode == RESULT_OK) {
            if(data != null) {
                productImageUri = data.getData();
                Picasso.get().load(productImageUri).fit().centerCrop().into(productImageView);
                productImageView.setBackgroundColor(0x00000000);
                takePhotoButton.setVisibility(View.INVISIBLE);
                uploadPhotoButton.setVisibility(View.INVISIBLE);
            }
        }
    }
}
