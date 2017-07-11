package com.doyoon.android.bravenewworld.domain;

import com.doyoon.android.bravenewworld.domain.firebase.value.Giver;
import com.google.android.gms.maps.model.LatLng;

import java.util.Random;

/**
 * Created by DOYOON on 7/12/2017.
 */

public class DummyDao {

    public static Giver createDummyGiver(){
        Giver giver = new Giver();
        LatLng latLng = getRandomLatLng();
        giver.setLatitude(latLng.latitude);
        giver.setLongitude(latLng.longitude);
        return giver;
    }

    private static LatLng getRandomLatLng() {
        // ref 1 : https://www.google.co.kr/maps/place/37%C2%B029'29.7%22N+126%C2%B059'34.7%22E/@37.4917622,126.9779259,13.74z/data=!4m5!3m4!1s0x0:0x0!8m2!3d37.491576!4d126.992967?hl=en
        // ref 2 : https://www.google.co.kr/maps/place/37%C2%B031'26.9%22N+127%C2%B001'36.6%22E/@37.524129,127.0246373,17z/data=!3m1!4b1!4m5!3m4!1s0x0:0x0!8m2!3d37.524129!4d127.026826?hl=en

        double top = 127.026826;
        double bottom = 126.992967;

        double left = 37.491576;
        double right = 37.524129;

        double longitude = getRandomDouble(bottom, top);
        double latitude = getRandomDouble(left, right);
        return new LatLng(latitude, longitude);
    }

    private static Random r = new Random();
    private static double getRandomDouble(double min, double max) {
        return min + (max - min) * r.nextDouble();
    }
}
