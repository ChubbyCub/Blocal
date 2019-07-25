package com.example.blocal.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;


import java.util.ArrayList;
import java.util.List;


public class Product implements Parcelable {
    private String name;
    private double price;
    private String description;
    private String userId;
    private Timestamp dateAdded;
    private String photoURL;
    private GeoPoint coordinates;
    private String category;
    private String productId;
    private ArrayList<String> pendingOffers = null;


    public Product() {

    }

    public Product(String name, String productId, String photoURL) {
        this.name = name;
        this.productId = productId;
        this.photoURL = photoURL;
    }

    public Product(String name, double price, String description, String userId,
                   Timestamp dateAdded, String photoURL, String category,
                   ArrayList<String> pendingOffers) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.userId = userId;
        this.dateAdded = dateAdded;
        this.photoURL = photoURL;
        this.category = category;
        this.pendingOffers = pendingOffers;
    }

    public List<String> getPendingOffers() {
        return pendingOffers;
    }

    public void setPendingOffers(ArrayList<String> pendingOffers) {
        this.pendingOffers = pendingOffers;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Timestamp getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Timestamp dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public GeoPoint getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(GeoPoint coordinates) {
        this.coordinates = coordinates;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString ( name );
        parcel.writeDouble ( price );
        parcel.writeString ( description );
        parcel.writeString ( userId );
        parcel.writeParcelable ( dateAdded, i );
        parcel.writeString ( photoURL );
        parcel.writeString ( productId );
        parcel.writeStringList ( pendingOffers );
    }

    public static final Parcelable.Creator<Product> CREATOR = new Parcelable.Creator<Product> () {

        public Product createFromParcel(Parcel in) {
            return new Product ( in );
        }

        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    private Product(Parcel in) {
        name = in.readString ();
        price = in.readDouble ();
        description = in.readString ();
        userId = in.readString ();
        dateAdded = in.readParcelable ( Timestamp.class.getClassLoader () );
        photoURL = in.readString ();
        productId = in.readString ();
        pendingOffers = in.createStringArrayList ();
    }
}