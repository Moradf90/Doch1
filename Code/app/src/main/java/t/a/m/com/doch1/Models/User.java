package t.a.m.com.doch1.Models;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.PropertyName;

import java.util.Date;

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
    public static final String PHONE_PROPERTY = "phone";
    public static final String LAST_UPDATE_DATE_PROPERTY = "lastUpdateDate";
    public static final String IMAGE_PROPERTY = "image";

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
    @PropertyName(PHONE_PROPERTY)
    private String mPhone;
    @PropertyName(LAST_UPDATE_DATE_PROPERTY)
    private Date mlastUpdateDate;
    @PropertyName(IMAGE_PROPERTY)
    private String mImage;

    public User(){}

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
}
