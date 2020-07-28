
package com.overthere.express.model;


public class Booking {

    private int id, reqestID, applied;
    private String DatetTime, pkupArea, dropoffArea, bookingFare;

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

    public int getBookingApplied() {
        return applied;
    }

    public void setBookingApplied(int applied) {
        this.applied = applied;
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

}
