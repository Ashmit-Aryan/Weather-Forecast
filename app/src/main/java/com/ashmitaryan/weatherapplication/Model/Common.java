package com.ashmitaryan.weatherapplication.Model;

import android.annotation.SuppressLint;
import android.location.Location;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Common {
    public static final String API_KEY = "9df30a7c5b804d6c09f23ed68b95aae1";
    public static Location current_location = null;

    public static String covertUnixToDate(int dt){
        Date date = new Date(dt*1000L);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("EEE,d MMM yyyy, HH:mm aaa");
        return sdf.format(date);
    }

    public static String covertUnixToHour(int sunrise){
        Date date = new Date(sunrise*1000L);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("HH:mm aaa");
        return sdf.format(date);
    }
}
