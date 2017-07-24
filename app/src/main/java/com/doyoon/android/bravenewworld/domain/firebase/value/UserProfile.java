package com.doyoon.android.bravenewworld.domain.firebase.value;

import com.doyoon.android.bravenewworld.domain.firebase.FirebaseModel;

/**
 * Created by DOYOON on 7/12/2017.
 */

public class UserProfile extends FirebaseModel{

    private String comment;
    private String key;
    private String name;
    private int age;
    private int gender;
    private String work;
    private String email;
    private String imageUri;
    private int umbCount;
    private int rainCount;

    public UserProfile() {

    }

    public UserProfile(String name, int age, int gender, String email) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public int getUmbCount() {
        return umbCount;
    }

    public void setUmbCount(int umbCount) {
        this.umbCount = umbCount;
    }

    public int getRainCount() {
        return rainCount;
    }

    public void setRainCount(int rainCount) {
        this.rainCount = rainCount;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String getKey() {
        return null;
    }

    @Override
    public void setKey(String key) {

    }

    @Override
    public String toString() {
        return "UserProfile{" +
                "key='" + key + '\'' +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", gender=" + gender +
                ", work='" + work + '\'' +
                ", email='" + email + '\'' +
                ", imageUri='" + imageUri + '\'' +
                ", umbCount='" + umbCount + '\'' +
                ", rainCount='" + rainCount + '\'' +
                '}';
    }
}
