package t.a.m.com.doch1.Models;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.PropertyName;

import java.util.List;

/**
 * Created by Morad on 12/3/2016.
 */
@IgnoreExtraProperties
public class MainStatus {

    public static final String STATUSES_REFERENCE_KEY = "statuses";

    public static final String MAIN_STATUS_NAME = "name";
    public static final String SUB_STATUSES = "subStatuses";


    private String name;
    private List<String> subStatuses;

    // Getters
    public String getName() {
        return name;
    }

    public List<String> getSubStatuses() {
        return subStatuses;
    }
}
