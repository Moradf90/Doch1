package t.a.m.com.doch1.Models;

import java.util.ArrayList;

/**
 * Created by Morad on 12/23/2016.
 */
public class ListOfStrings extends ArrayList<String> {
    @Override
    public boolean equals(Object object) {

        if(object instanceof ListOfStrings){
            ListOfStrings compared = (ListOfStrings) object;
            if(compared.size() == size()){
                for (String obj : compared) {
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
