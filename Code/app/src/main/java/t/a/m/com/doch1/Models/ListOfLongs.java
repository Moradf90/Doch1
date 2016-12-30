package t.a.m.com.doch1.Models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Morad on 12/16/2016.
 */
public class ListOfLongs extends ArrayList<Long> implements Serializable{
    @Override
    public boolean equals(Object object) {

        if(object instanceof ListOfLongs){
            ListOfLongs compared = (ListOfLongs) object;
            if(compared.size() == size()){
                for (Long obj : compared) {
                    if(!contains(obj)){
                        return false;
                    }
                }
                return true;
            }
        }

        return false;
    }
}
