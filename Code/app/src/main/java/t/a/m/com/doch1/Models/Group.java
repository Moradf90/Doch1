package t.a.m.com.doch1.Models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.PropertyName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import t.a.m.com.doch1.common.SQLHelper;
import t.a.m.com.doch1.common.Utils;

/**
 * Created by Morad on 12/5/2016.
 */
@IgnoreExtraProperties
@Table(name = Group.GROUPS_REFERENCE_KEY)
public class Group extends Model implements Serializable{

    public static final String GROUPS_REFERENCE_KEY = "groups";

    public static final String ID_PROPERTY = "id";
    public static final String NAME_PROPERTY = "name";
    public static final String PARENT_ID_PROPERTY = "parentId";
    public static final String USERS_PROPERTY = "users";
    public static final String MANAGER_PROPERTY = "manager";
    public static final String IMAGE_PROPERTY = "image";
    private static final String STATUSES_PROPERTY = "statusesId";

    @Column(name = NAME_PROPERTY, index = true)
    private String mName;
    @Column(name = PARENT_ID_PROPERTY)
    private Long mParentId;
    @Column(name = USERS_PROPERTY)
    private ListOfLongs mUsers;
    @Column(name = MANAGER_PROPERTY)
    private Long mManager;
    @Column(name = IMAGE_PROPERTY)
    private String mImage;
    @Column(name = STATUSES_PROPERTY)
    private Long mStatusesId;


    private boolean mIsManager = false;

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

    public Long getManager() {
        return mManager;
    }

    public void setManager(Long mManager) {
        this.mManager = mManager;
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

    // this method doesnt called isManager cause of firebase (exception - 'andoid firebase Found conflicting getters for name')
    public boolean getIsManager() {
        return mIsManager;
    }

    public void setIsManager(boolean isManager) {
        this.mIsManager = isManager;
    }

    @Override
    public boolean equals(Object object) {
        if(object != null && object instanceof Group) {
            Group group = (Group) object;

            return (Utils.isLongsEquals(getId(),group.getId())
                    && Utils.isObjectsEquals(getImage(), group.getImage())
                    && Utils.isObjectsEquals(getName(), group.getName())
                    && Utils.isLongsEquals(getParentId(), group.getParentId())
                    && Utils.isLongsEquals(getManager(), group.getManager())
                    && Utils.isLongsEquals(getStatusesId(), group.getStatusesId())
                    && Utils.isObjectsEquals(mUsers, group.mUsers));
        }
        return false;
    }

    public static void deleteGroups(Collection<Long> ids){
        if(ids != null && ids.size() > 0) {
            new Delete().from(Group.class)
                    .where(Group.ID_PROPERTY + SQLHelper.getInQuery(ids)).execute();
        }
    }
}
