package t.a.m.com.doch1.Models;

import java.util.ArrayList;
import java.util.List;

import t.a.m.com.doch1.R;

/**
 * Created by tom on 04-Dec-16.
 */
public class GlobalsTemp {
    public static List<Soldier> MySoldiers = new ArrayList<Soldier>() {{
        add(new Soldier("Amit", "Hanuch", R.drawable.amit72));
        add(new Soldier("Tom", "Dinur", R.drawable.tom72));
        add(new Soldier("Morad", "Faris", R.drawable.morad72));
    }};
}
