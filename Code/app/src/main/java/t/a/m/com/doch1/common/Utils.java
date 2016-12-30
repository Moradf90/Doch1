package t.a.m.com.doch1.common;

/**
 * Created by Morad on 12/30/2016.
 */
public class Utils {
    public static boolean isLongsEquals(Long l1, Long l2){
        if(l1 != null && l2 == null || l1 == null && l2 != null) return false;
        return l1 == null || l1.longValue() == l2.longValue();
    }

    public static boolean isObjectsEquals(Object obj1, Object obj2){
        if(obj1 != null && obj2 == null || obj1 == null && obj2 != null) return false;
        return obj1 == null || obj1.equals(obj2);
    }
}
