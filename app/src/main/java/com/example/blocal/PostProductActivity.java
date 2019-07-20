package com.example.blocal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import model.Product;
import util.ProductApi;

public class PostProductActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
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

    private FirebaseFirestore db;
    private FirebaseAuth fireBaseAuth;
    private FirebaseUser user;
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    private List<String> categories;
    private String chosenCategory;
    private String currentUserId;

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

        fireBaseAuth = fireBaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        categories = new ArrayList<>();
        categories.add("Select a category");
        displayCategorySpinner();

        takePhotoButton.setOnClickListener(this);
        uploadPhotoButton.setOnClickListener(this);
        postProductButton.setOnClickListener(this);
        productCategorySpinner.setOnItemSelectedListener(this);

    }

    private void displayCategorySpinner() {
        CollectionReference collectionReference = db.collection("categories");
        collectionReference.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot document : task.getResult()) {
                                categories.add(document.getId());
                            }
                            Log.d("What is in my list?", categories.toString());
                        } else {
                            Log.d("ERROR: ", "error while getting document: ", task.getException());
                        }
                    }
                });

        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, categories);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        productCategorySpinner.setAdapter(adapter);
    }

    private void postProduct() {
        final String name = productNameText.getText().toString().trim();
        final String description = productDescriptionText.getText().toString().trim();
        final String category = chosenCategory;
        final String location = productLocationText.getText().toString().trim();
        final Double price = Double.parseDouble(productPriceText.getText().toString().trim());
        final CollectionReference collectionReference = db.collection("products");

        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(description)
        && !TextUtils.isEmpty(category) && !TextUtils.isEmpty(location)
                && price != null && productImageUri != null) {
            final StorageReference filepath = storageReference
                    .child("Test")
                    .child("product_image" + Timestamp.now().getSeconds());

            filepath.putFile(productImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Product product = new Product();
                                    product.setName(name);
                                    product.setDescription(description);
                                    product.setLocation(location);
                                    product.setPrice(price);
                                    product.setPhotoURL(uri.toString());
                                    product.setDateAdded(new Timestamp(new Date()));
                                    product.setCategory(chosenCategory);
                                    product.setUserId(currentUserId);
                                    collectionReference.add(product);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("ERROR WHILE POSTING NEW PRODUCT: ", e.getMessage());
                        }
                    });
        }
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
            case R.id.post_product_button:
                postProduct();
                startActivity(new Intent(PostProductActivity.this, MainActivity.class));
                finish();
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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        chosenCategory = adapterView.getItemAtPosition(i).toString();
        Toast.makeText(this, "Selected: " + chosenCategory, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        user = fireBaseAuth.getCurrentUser();
        currentUserId = user.getUid();
    }
}
