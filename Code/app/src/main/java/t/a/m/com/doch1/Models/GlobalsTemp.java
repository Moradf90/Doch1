package t.a.m.com.doch1.Models;

import java.util.ArrayList;
import java.util.List;

import t.a.m.com.doch1.R;

/**
 * Created by tom on 04-Dec-16.
 */
public class GlobalsTemp {
    public static List<Soldier> MySoldiers = new ArrayList<Soldier>() {{
        add(new Soldier("Amit", "Hanoch", (long) 1234567, R.drawable.amit72));
        add(new Soldier("Tom", "Dinur", (long) 7647694, R.drawable.tom72));
        add(new Soldier("Morad", "Faris", (long) 51879251, R.drawable.morad72));
    }};
}
