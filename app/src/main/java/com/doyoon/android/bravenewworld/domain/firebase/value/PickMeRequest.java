package com.doyoon.android.bravenewworld.domain.firebase.value;

import com.doyoon.android.bravenewworld.domain.firebase.FirebaseModel;
import com.doyoon.android.bravenewworld.util.ConvString;

/**
 * Created by DOYOON on 7/13/2017.
 */

public class PickMeRequest extends FirebaseModel {

    private String fromUserAccessKey;
    private String imageUrl;

    private String name;
    private int age;
    private int gender;
    private String distance;


    public PickMeRequest() {
    }

    @Override
    public String getKey() {
        return null;
    }

    @Override
    public void setKey(String key) {

    }

    public String getFromUserAccessKey() {
        return fromUserAccessKey;
    }

    public void setFromUserAccessKey(String fromUserAccessKey) {
        this.fromUserAccessKey = fromUserAccessKey;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public void fetchDataFromUserProfile(UserProfile userProfile) {
        this.fromUserAccessKey = ConvString.commaSignToString(userProfile.getEmail());
        this.age = userProfile.getAge();
        this.gender = userProfile.getGender();
        this.name = userProfile.getName();
        this.imageUrl = userProfile.getImageUri();
    }

    @Override
    public String toString() {
        return "PickMeRequest{" +
                "fromUserAccessKey='" + fromUserAccessKey + '\'' +
                ", age=" + age +
                ", gender=" + gender +
                ", name='" + name + '\'' +
                '}';
    }
}
