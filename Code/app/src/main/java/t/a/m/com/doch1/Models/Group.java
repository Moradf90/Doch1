package t.a.m.com.doch1.Models;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.PropertyName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Morad on 12/5/2016.
 */
@IgnoreExtraProperties
public class Group {

    public static final String GROUPS_REFERENCE_KEY = "groups";


    public static final String ID_PROPERTY = "id";
    public static final String NAME_PROPERTY = "name";
    public static final String PARENT_ID_PROPERTY = "parentId";
    public static final String USERS_PROPERTY = "users";
    public static final String IMAGE_PROPERTY = "image";

    @PropertyName(ID_PROPERTY)
    private String mId;
    @PropertyName(NAME_PROPERTY)
    private String mName;
    @PropertyName(PARENT_ID_PROPERTY)
    private String mParentId;
    @PropertyName(USERS_PROPERTY)
    private List<String> mUsers;
    @PropertyName(IMAGE_PROPERTY)
    private String mImage;

    public Group(){}

    public String getId() {
        return mId;
    }

    public void setId(String mId) {
        this.mId = mId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getParentId() {
        return mParentId;
    }

    public void setParentId(String mParentId) {
        this.mParentId = mParentId;
    }

    public String getImage() {
        return mImage;
    }

    public void setImage(String mImage) {
        this.mImage = mImage;
    }

    public List<String> getUsers() {
        return mUsers;
    }

    public void setUsers(List<String> mUsers) {
        this.mUsers = mUsers;
    }

    public void addUser(String id) {
        if(mUsers == null){
            mUsers = new ArrayList<>();
        }
        mUsers.add(id);
    }
}
