package t.a.m.com.doch1.Models;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.PropertyName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import t.a.m.com.doch1.common.Utils;

/**
 * Created by Morad on 12/3/2016.
 */
@IgnoreExtraProperties
@Table(name = User.USERS_REFERENCE_KEY)
public class User extends Model implements Serializable{

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

    @Column(name = NAME_PROPERTY, index = true)
    private String mName;
    @Column(name = EMAIL_PROPERTY, index = true)
    private String mEmail;
    @Column(name = PERSONAL_ID_PROPERTY, index = true)
    private String mPersonalId;
    @Column(name = GROUPS_PROPERTY)
    private ListOfLongs mGroupsId;
    @Column(name = PHONE_PROPERTY)
    private String mPhone;
    @Column(name = LAST_UPDATE_DATE_PROPERTY)
    private Date mlastUpdateDate;
    @Column(name = IMAGE_PROPERTY)
    private String mImage;

    private String mMainStatus;
    private String mSubStatus;
    private long mGroupId;

    public User(){
        super();
    }

    public void setId(Long mId) {
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

    public void setPhone(String mPhone) {
        this.mPhone = mPhone;
    }

    public void setMainStatus(String mMainStatus) {
        this.mMainStatus = mMainStatus;
    }

    public void setSubStatus(String mSubStatus) {
        this.mSubStatus = mSubStatus;
    }

    public void setGroupId(long mGroupId) {
        this.mGroupId = mGroupId;
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

    public String getPhone() {
        return mPhone;
    }

    public long getGroupId() {
        return mGroupId;
    }

    public List<Long> getGroups() {
        return mGroupsId;
    }

    public void setGroups(List<Long> mGroupsId) {
        this.mGroupsId = new ListOfLongs();
        this.mGroupsId.addAll(mGroupsId);
    }

    public void addGroupId(Long newGroupID) {
        if(this.mGroupsId == null) {
            this.mGroupsId = new ListOfLongs();
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
    public void updateUserStatuses() {
        this.setLastUpdateDate(new Date());

        FirebaseDatabase.getInstance().getReference(UserInGroup.USERS_IN_GROUP_REFERENCE_KEY)
                .child(String.valueOf(getGroupId())).child(getId().toString()).setValue(getUserInGroup());
                
    }

    private UserInGroup getUserInGroup() {
        return new UserInGroup(getMainStatus(), getSubStatus(), new Date());
    }

    @Override
    public boolean equals(Object object) {
        if(object != null && object instanceof User){
            User user = (User)object;
            return Utils.isLongsEquals(getId(),user.getId())
                    && Utils.isObjectsEquals(getName(), user.getName())
                    && Utils.isObjectsEquals(getEmail(), user.getEmail())
                    && Utils.isObjectsEquals(getPersonalId(), user.getPersonalId())
                    && Utils.isObjectsEquals(mGroupsId, user.mGroupsId)
                    && Utils.isObjectsEquals(getPhone(), user.getPhone())
                    && Utils.isObjectsEquals(getImage(), user.getImage());
        }
        return false;
    }

    public static User current(Context context){


        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        Long userId = pref.getLong("userId", -1);
        if(userId != -1){
            return User.load(User.class, userId);
        }

        User current = new Select().from(User.class)
                .where(User.EMAIL_PROPERTY + "= ?", FirebaseAuth.getInstance().getCurrentUser().getEmail())
                .executeSingle();

        if(current != null) {
            pref.edit().putLong("userId", current.getId()).commit();
        }

        return current;
    }
}
