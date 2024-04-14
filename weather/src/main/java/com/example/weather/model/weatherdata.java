package com.example.weather.model;

import java.util.Date;

public class weatherdata {
	private double humi;
    private double temp;
    private Date gettime;

    public weatherdata() {
        // 默认构造函数
    }

    public weatherdata(double humi, double temp, Date gettime) {
        this.humi = humi;
        this.temp = temp;
        this.gettime = gettime;
    }

    public double getHumi() {
        return humi;
    }

    public void setHumi(double humi) {
        this.humi = humi;
    }

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public Date getGettime() {
        return gettime;
    }

    public void setGettime(Date gettime) {
        this.gettime = gettime;
    }

}
