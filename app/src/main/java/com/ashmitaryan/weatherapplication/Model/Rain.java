package com.ashmitaryan.weatherapplication.Model;

import com.google.gson.annotations.SerializedName;

public class Rain {
    @SerializedName("3h")
    private double h;

    public double getH() {
        return h;
    }

    public void setH(double h) {
        this.h = h;
    }
}
