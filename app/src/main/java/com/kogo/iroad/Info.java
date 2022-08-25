package com.kogo.iroad;

public class Info {
    private String iconName;
    private String time;
    private String celcius;
    private String distance;


    public Info() {
    }

    public Info(String iconName, String time, String celcius, String distance) {
        this.iconName = iconName;
        this.time = time;
        this.celcius = celcius;
        this.distance = distance;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCelcius() {
        return celcius;
    }

    public void setCelcius(String celcius) {
        this.celcius = celcius;
    }
}
