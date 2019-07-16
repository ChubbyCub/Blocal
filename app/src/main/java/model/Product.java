package model;

import com.google.firebase.Timestamp;


public class Product {
    private String name;
    private double price;
    private String description;
    private String location;
    private String userId;
    private Timestamp dateAdded;
    private String photoUrl;

    public Product() {

    }

    public Product(String name, double price, String description, String location, String userId, Timestamp dateAdded, String photoUrl) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.location = location;
        this.userId = userId;
        this.dateAdded = dateAdded;
        this.photoUrl = photoUrl;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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

    public String getImageUrl() {
        return photoUrl;
    }

    public void setImageUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}