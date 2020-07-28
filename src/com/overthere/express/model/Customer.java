package com.overthere.express.model;


import java.text.SimpleDateFormat;
import java.util.Date;

public class Customer {

    private int id, reqestID;
    private String DatetTime, pkDate, pkupArea, dropoffArea, bookingFare;
    private String customerName, memberDate, serviceTimes, reviewRate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRequestID() {
        return reqestID;
    }

    public void setRequestID(int reqestID) {
        this.reqestID = reqestID;
    }

    public String getDatetTime() {
        return DatetTime;
    }

    public void setDatetTime(String DatetTime) {
        this.DatetTime = DatetTime;
    }

    public String getpkupArea() { return pkupArea; }

    public void setpkupArea(String pkupArea) { this.pkupArea = pkupArea;}

    public String getdropoffArea() { return dropoffArea; }

    public void setdropoffArea(String dropoffArea) { this.dropoffArea = dropoffArea;}

    public String getbookingFare() { return bookingFare; }

    public void setbookingFare(String bookingFare) { this.bookingFare = bookingFare;}

    public String getCustomerName() { return customerName; }

    public void setCustomerName(String customerName) { this.customerName = customerName;}

    public String getMemberDate() { return memberDate; }

    public void setMemberDate(String memberDate) {
        SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf4 = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = sdf3.parse(memberDate);
            this.memberDate = sdf4.format(date);
        }catch (Exception e) {
            this.memberDate = memberDate;
        }
    }

    public String getServiceTimes() { return serviceTimes; }

    public void setServiceTimes(String serviceTimes) { this.serviceTimes = serviceTimes;}

    public String getReviewRate() { return reviewRate; }

    public void setReviewRate(String reviewRate) { this.reviewRate = reviewRate;}

    public String getPkDate() { return pkDate; }

    public void setPkDate(String pkDate) { this.pkDate = pkDate;}

}
