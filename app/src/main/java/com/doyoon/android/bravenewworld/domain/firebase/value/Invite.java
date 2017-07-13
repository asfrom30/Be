package com.doyoon.android.bravenewworld.domain.firebase.value;

import com.doyoon.android.bravenewworld.domain.firebase.FirebaseModel;

/**
 * Created by DOYOON on 7/13/2017.
 */

public class Invite extends FirebaseModel {

    private String from;
    private int age;
    private int gender;
    private String name;

    public Invite() {
    }

    @Override
    public String getKey() {
        return null;
    }

    @Override
    public void setKey(String key) {

    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
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

    public void fetchDataFromUserProfile(UserProfile userProfile) {
        this.from = userProfile.getEmail();
        this.age = userProfile.getAge();
        this.gender = userProfile.getGender();
        this.name = userProfile.getName();
    }

    @Override
    public String toString() {
        return "Invite{" +
                "from='" + from + '\'' +
                ", age=" + age +
                ", gender=" + gender +
                ", name='" + name + '\'' +
                '}';
    }
}
