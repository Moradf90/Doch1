package t.a.m.com.doch1.Models;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.PropertyName;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Morad on 12/3/2016.
 */
@IgnoreExtraProperties
public class User {

    public static final String USERS_REFERENCE_KEY = "users";
    public static final String ID_PROPERTY = "id";
    public static final String NAME_PROPERTY = "name";
    public static final String EMAIL_PROPERTY = "email";
    public static final String PERSONAL_ID_PROPERTY = "personalId";
    public static final String GROUP_ID_PROPERTY = "groupId";
    public static final String GROUPS_PROPERTY = "groups";
    public static final String PHONE_PROPERTY = "phone";
    public static final String LAST_UPDATE_DATE_PROPERTY = "lastUpdateDate";
    public static final String IMAGE_PROPERTY = "image";
    public static final String MAIN_STATUS = "mainStatus";
    public static final String SUB_STATUS = "subStatus";


    @PropertyName(ID_PROPERTY)
    private String mId;
    @PropertyName(NAME_PROPERTY)
    private String mName;
    @PropertyName(EMAIL_PROPERTY)
    private String mEmail;
    @PropertyName(PERSONAL_ID_PROPERTY)
    private String mPersonalId;
    @PropertyName(GROUP_ID_PROPERTY)
    private String mGroupId;
    @PropertyName(GROUPS_PROPERTY)
    private List<String> mGroupsId;
    @PropertyName(PHONE_PROPERTY)
    private String mPhone;
    @PropertyName(LAST_UPDATE_DATE_PROPERTY)
    private Date mlastUpdateDate;
    @PropertyName(IMAGE_PROPERTY)
    private String mImage;
    @PropertyName(MAIN_STATUS)
    private String mMainStatus;
    @PropertyName(SUB_STATUS)
    private String mSubStatus;

    public void setId(String mId) {
        this.mId = mId;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public void setEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public void setPersonalId(String mPersonalId) {
        this.mPersonalId = mPersonalId;
    }

    public void setGroupId(String mGroupId) {
        this.mGroupId = mGroupId;
    }

    public void setPhone(String mPhone) {
        this.mPhone = mPhone;
    }

    public void setMainStatus(String mMainStatus) {
        this.mMainStatus = mMainStatus;
    }

    public void setSubStatus(String mSubStatus) {
        this.mSubStatus = mSubStatus;
    }

    public String getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public String getEmail() {
        return mEmail;
    }

    public String getPersonalId() {
        return mPersonalId;
    }

    public String getGroupId() {
        return mGroupId;
    }

    public String getPhone() {
        return mPhone;
    }

    public List<String> getGroups() {
        return mGroupsId;
    }

    public void setGroups(List<String> mGroupsId) {
        this.mGroupsId = mGroupsId;
    }

    public void addGroupId(String newGroupID) {
        if(this.mGroupsId == null) {
            this.mGroupsId = new ArrayList<>();
        }
        this.mGroupsId.add(newGroupID);
    }

    public void remvoeGroupId(String toRemoveGroupID) {
        this.mGroupsId.remove(toRemoveGroupID);
    }

    public Date getLastUpdateDate() {
        return mlastUpdateDate;
    }

    public void setLastUpdateDate(Date mlastUpdateDate) {
        this.mlastUpdateDate = mlastUpdateDate;
    }

    public void setImage(String mImage) {
        this.mImage = mImage;
    }

    public String getImage() {
        return mImage;
    }

    public String getMainStatus() {
        return valueOrEmpty(mMainStatus);
    }

    public String getSubStatus() {
        return valueOrEmpty(mSubStatus);
    }

    // Method
    private String valueOrEmpty(String value) {
        return value != null ? value : "";
    }

    // Update the user in the DB
    public void updateUserStatuses(String groupId) {
        this.setLastUpdateDate(new Date());

        FirebaseDatabase.getInstance().getReference(UserInGroup.USERS_IN_GROUP_REFERENCE_KEY)
                .child(groupId).child(getId()).setValue(getUserInGroup());
                
    }

    private UserInGroup getUserInGroup() {
        return new UserInGroup(getMainStatus(), getSubStatus(), new Date());
    }
}
