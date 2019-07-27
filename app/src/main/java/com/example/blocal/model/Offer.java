package com.example.blocal.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.firebase.Timestamp;

public class Offer implements Parcelable {
    private double price;
    private String sellerId;
    private String buyerId;
    private String productId;
    private Timestamp dateCreated;
    private Timestamp dateUpdated;
    private String status; // pending - accepted - rejected
    private boolean productState; // sold - true, unsold - false

    public Offer() {
    }

    public Timestamp getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Timestamp dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Timestamp getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(Timestamp dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isProductState() {
        return productState;
    }

    public void setProductState(boolean productState) {
        this.productState = productState;
    }

    public Offer(double price, String buyerId, String sellerId, String productId,
                 Timestamp dateCreated, Timestamp dateUpdated, String status, boolean productState) {
        this.price = price;
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.productId = productId;
        this.dateCreated = dateCreated;
        this.dateUpdated = dateUpdated;
        this.status = status;
        this.productState = productState;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public static final Parcelable.Creator<Offer> CREATOR = new Parcelable.Creator<Offer> () {

        public Offer createFromParcel(Parcel in) {
            return new Offer ( in );
        }

        public Offer[] newArray(int size) {
            return new Offer[size];
        }
    };

    private Offer(Parcel in) {
        price = in.readDouble ();
        buyerId = in.readString ();
        sellerId = in.readString ();
        productId = in.readString ();
        dateCreated = in.readParcelable(Timestamp.class.getClassLoader ());
        dateUpdated = in.readParcelable(Timestamp.class.getClassLoader ());
        status = in.readString ();
        productState = in.readBoolean ();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeDouble ( price );
        parcel.writeString ( buyerId );
        parcel.writeString ( sellerId );
        parcel.writeString ( productId );
        parcel.writeParcelable ( dateCreated, i );
        parcel.writeParcelable ( dateUpdated, i );
        parcel.writeString ( status );
        parcel.writeBoolean ( productState );
    }
}
