package t.a.m.com.doch1.Models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.List;

/**
 * Created by Morad on 12/23/2016.
 */
@IgnoreExtraProperties
@Table(name = StatusesInGroup.STATUSES_IN_GROUP_REFERENCE_KEY)
public class StatusesInGroup extends Model {

    public static final String STATUSES_IN_GROUP_REFERENCE_KEY = "statusesInGroup";
    public static final String STATUSES_ID_PROPERTY = "statusesId";
    private static final String NAME_PROPERTY = "name";
    private static final String SUB_STATUSES_PROPERTY = "subStatuses";

    @Column(name = STATUSES_ID_PROPERTY)
    private Long mStatusesId;
    @Column(name = NAME_PROPERTY)
    private String mName;
    @Column(name = SUB_STATUSES_PROPERTY)
    private ListOfStrings mSubStatuses;

    public StatusesInGroup(){
        super();
    }

    public void setId(Long mId) {
        this.mId = mId;
    }

    public Long getStatusesId() {
        return mStatusesId;
    }

    public void setStatusesId(Long statusesId) {
        this.mStatusesId = statusesId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public List<String> getSubStatuses() {
        return mSubStatuses;
    }

    public void setSubStatuses(List<String> subStatuses) {
        if(mSubStatuses == null){
            mSubStatuses = new ListOfStrings();
        }
        this.mSubStatuses.addAll(subStatuses);
    }
}
