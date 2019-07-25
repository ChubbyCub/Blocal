package com.example.blocal.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Offer implements Parcelable {
    private double price;
    private String sellerId;
    private String buyerId;

    public Offer() {
    }

    public Offer(double price, String buyerId, String sellerId) {
        this.price = price;
        this.buyerId = buyerId;
        this.sellerId = sellerId;
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

    public static final Parcelable.Creator<Offer> CREATOR = new Parcelable.Creator<Offer>() {

        public Offer createFromParcel(Parcel in) {
            return new Offer(in);
        }

        public Offer[] newArray(int size) {
            return new Offer[size];
        }
    };

    private Offer(Parcel in) {
        price = in.readDouble();
        buyerId = in.readString();
        sellerId = in.readString ();
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
    }
}
