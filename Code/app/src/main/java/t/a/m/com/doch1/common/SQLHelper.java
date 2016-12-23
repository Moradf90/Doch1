package t.a.m.com.doch1.common;

import java.util.List;

/**
 * Created by tom on 23-Dec-16.
 */
public class SQLHelper {


    public static String getInQuery(Object... groupsId) {
        StringBuilder query = new StringBuilder(" in (");
        for (Object groupID: groupsId) {
            query.append(groupID + ",");
        }

        return query.substring(0, query.length() - 1) + ") ";
    }

//    public static String getInQuery(List<Object> groupsId) {
//        getInQuery(groupsId.toArray());
//        StringBuilder query = new StringBuilder(" in (");
//        for (Long groupID: groupsId) {
//            query.append(groupID + ",");
//        }
//
//        return query.substring(0, query.length() - 1) + ") ";
//    }
}
