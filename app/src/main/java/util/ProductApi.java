package util;

import android.app.Application;

public class ProductApi extends Application {
    private String userEmail;
    private String userId;


    private static ProductApi instance;

    public static ProductApi getInstance() {
        if(instance == null) {
            instance = new ProductApi();
        }
        return instance;
    }

    public ProductApi() {}

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
