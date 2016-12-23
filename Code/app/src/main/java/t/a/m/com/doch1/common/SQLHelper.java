package t.a.m.com.doch1.common;

/**
 * Created by tom on 23-Dec-16.
 */
public class SQLHelper {


    public static String getInQuery(Long[] groupsId) {
        StringBuilder query = new StringBuilder(" in (");
        for (Long groupID: groupsId) {
            query.append(groupID + ",");
        }

        return query.substring(0, query.length() - 1) + ") ";
    }
}
