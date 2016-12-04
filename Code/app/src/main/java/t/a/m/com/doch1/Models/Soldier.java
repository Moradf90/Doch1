package t.a.m.com.doch1.Models;

import android.graphics.drawable.Drawable;

/**
 * Created by tom on 04-Dec-16.
 */
public class Soldier {
    private String firstName;
    private String lastName;
    private String mainStatus;
    private String subStatus;
    private Integer picture;
    private Long soldierID;

    // Ctors
    public Soldier(String firstName, String lastName, Long soldierID, Integer picture) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.soldierID = soldierID;
        this.picture = picture;
    }

    // Getters
    public String getFirstName() {
        return valueOrEmpty(firstName);
    }

    public String getLastName() {
        return valueOrEmpty(lastName);
    }

    public String getMainStatus() {
        return valueOrEmpty(mainStatus);
    }

    public String getSubStatus() {
        return valueOrEmpty(subStatus);
    }

    public Integer getPicture() {
        return picture;
    }

    public Long getSoldierID() {
        return soldierID;
    }

    // Setters
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setMainStatus(String mainStatus) {
        this.mainStatus = mainStatus;
    }

    public void setSubStatus(String subStatus) {
        // TODO: remove - for debug only
        this.mainStatus = "Coming";
        this.subStatus = subStatus;
    }

    public void setPicture(Integer picture) {
        this.picture = picture;
    }

    public void setSoldierID(Long soldierID) {
        this.soldierID = soldierID;
    }

    // Methods
    public String getFullName() {
        return getFirstName() + " " + getLastName();
    }

    public String getDisplayStatus(){
        return getMainStatus() + ", " + getSubStatus();
    }

    @Override
    public String toString() {
        return getFullName() + ": " + getDisplayStatus();
    }

    private String valueOrEmpty(String value) {
        return value != null ? value : "";
    }
}
