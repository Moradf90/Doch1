package t.a.m.com.doch1.Models;

import com.google.firebase.database.PropertyName;

import java.util.Date;

/**
 * Created by tom on 12-Dec-16.
 */
public class UserInGroup {
    public static final String MAIN_STATUS = "mainStatus";
    public static final String SUB_STATUS = "subStatus";
    public static final String USERS_IN_GROUP_REFERENCE_KEY = "usersInGroups";
    public static final String LAST_UPDATE_DATE_PROPERTY = "lastUpdateDate";

    @PropertyName(LAST_UPDATE_DATE_PROPERTY)
    private Date mlastUpdateDate;
    @PropertyName(MAIN_STATUS)
    private String mMainStatus;
    @PropertyName(SUB_STATUS)
    private String mSubStatus;

    public UserInGroup() {
    }

    public UserInGroup(String mMainStatus, String mSubStatus, Date mlastUpdateDate) {
        this.mMainStatus = mMainStatus;
        this.mSubStatus = mSubStatus;
        this.mlastUpdateDate = mlastUpdateDate;
    }

    public String getMainStatus() {
        return valueOrEmpty(mMainStatus);
    }

    public String getSubStatus() {
        return valueOrEmpty(mSubStatus);
    }

    public Date getLastUpdateDate() {
        return mlastUpdateDate;
    }

    public void setMainStatus(String mMainStatus) {
        this.mMainStatus = mMainStatus;
    }

    public void setSubStatus(String mSubStatus) {
        this.mSubStatus = mSubStatus;
    }

    public void setLastUpdateDate(Date mlastUpdateDate) {
        this.mlastUpdateDate = mlastUpdateDate;
    }

    // Method
    private String valueOrEmpty(String value) {
        return value != null ? value : "";
    }

}
