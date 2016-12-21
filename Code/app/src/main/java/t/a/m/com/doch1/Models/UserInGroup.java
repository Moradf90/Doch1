package t.a.m.com.doch1.Models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.Date;

/**
 * Created by tom on 12-Dec-16.
 */
@Table(name = UserInGroup.USERS_IN_GROUP_REFERENCE_KEY)
public class UserInGroup extends Model{
    public static final String MAIN_STATUS_PROPERTY = "mainStatus";
    public static final String SUB_STATUS_PROPERTY = "subStatus";
    public static final String USERS_IN_GROUP_REFERENCE_KEY = "usersInGroups";
    public static final String LAST_UPDATE_DATE_PROPERTY = "lastUpdateDate";
    private static final String GROUP_PROPERTY = "groupId";
    private static final String USER_PROPERTY = "userId";

    @Column(name = LAST_UPDATE_DATE_PROPERTY)
    private Date mlastUpdateDate;
    @Column(name = MAIN_STATUS_PROPERTY)
    private String mMainStatus;
    @Column(name = SUB_STATUS_PROPERTY)
    private String mSubStatus;
    @Column(name = GROUP_PROPERTY)
    private Long mGroupId;
    @Column(name = USER_PROPERTY)
    private Long mUserId;

    public UserInGroup() {
        super();
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

    public Long getGroupId(){
        return mGroupId;
    }

    public Long getUserId(){
        return mUserId;
    }

    public void setGroupId(Long groupId){
        mGroupId = groupId;
    }

    public void setUserId(Long userId){
        mUserId = userId;
    }

    // Method
    private String valueOrEmpty(String value) {
        return value != null ? value : "";
    }

}
