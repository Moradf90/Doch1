package t.a.m.com.doch1.Models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.PropertyName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Morad on 12/5/2016.
 */
@IgnoreExtraProperties
@Table(name = Group.GROUPS_REFERENCE_KEY)
public class Group extends Model{

    public static final String GROUPS_REFERENCE_KEY = "groups";


    public static final String ID_PROPERTY = "id";
    public static final String NAME_PROPERTY = "name";
    public static final String PARENT_ID_PROPERTY = "parentId";
    public static final String USERS_PROPERTY = "users";
    public static final String IMAGE_PROPERTY = "image";
    private static final String STATUSES_PROPERTY = "statusesId";

    @Column(name = NAME_PROPERTY, index = true)
    private String mName;
    @Column(name = PARENT_ID_PROPERTY)
    private Long mParentId;
    @Column(name = USERS_PROPERTY)
    private ListOfLongs mUsers;
    @Column(name = IMAGE_PROPERTY)
    private String mImage;
    @Column(name = STATUSES_PROPERTY)
    private Long mStatusesId;

    public Group(){
        super();
    }

    public void setId(Long mId) {
        this.mId = mId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public Long getParentId() {
        return mParentId;
    }

    public void setParentId(Long mParentId) {
        this.mParentId = mParentId;
    }

    public String getImage() {
        return mImage;
    }

    public void setImage(String mImage) {
        this.mImage = mImage;
    }

    public List<Long> getUsers() {
        return mUsers;
    }

    public void setUsers(List<Long> mUsers) {
        this.mUsers = new ListOfLongs();
        this.mUsers.addAll(mUsers);
    }

    public void addUser(Long id) {
        if(mUsers == null){
            mUsers = new ListOfLongs();
        }
        mUsers.add(id);
    }

    public Long getStatusesId() {
        return mStatusesId;
    }

    public void setStatusesId(Long mStatusesId) {
        this.mStatusesId = mStatusesId;
    }
}
