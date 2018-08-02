package com.example.admin.liftapp.Model;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.HashMap;



@Entity
public class User {

    @PrimaryKey
    @NonNull

    public String userName;
    public String email;
    public String birthday;
    public String claim;
    public String height;
    public String weight;
    public String imageUrl;
    public float lastUpdated;


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    public String getBirthday() {
        return birthday;
    }

    public void setBirhday(String birhday) {
        this.birthday = birhday;
    }

    public String getClaim() {
        return claim;
    }

    public void setClaim(String claim) {
        this.claim = claim;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void lastUpdated(float lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public float getlastUpdated() {
        return lastUpdated;
    }

    HashMap<String,Object> toJson(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("userName", userName);
        result.put("imageUrl", imageUrl);
        result.put("email", email);
        result.put("claim", claim);
        result.put("height", height);
        result.put("birthday", birthday);
        result.put("weight", weight);
        result.put("lastUpdated", lastUpdated);
        return result;
    }
}
