package t.a.m.com.doch1.Models;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import t.a.m.com.doch1.R;
import t.a.m.com.doch1.views.RoundedImageView;

/**
 * Created by tom on 01-Dec-16.
 */
public class StatusContainer {
    private View container;
    private List<RoundedImageView> soldiers;
    private String[] subStatuses;
    private String mainStatus;
    private Rect rect;

    public StatusContainer(View container, String main, String[] sub) {
        this.container = container;
        this.subStatuses = sub;
        this.mainStatus = main;
        this.soldiers = new ArrayList<>();
    }

    // Getters
    public String getMainStatus() {
        return mainStatus;
    }

    public View getContainer() {
        return container;
    }

    public String[] getSubStatuses() {
        return subStatuses.clone();
    }

    public Rect getRect() {
        return new Rect(rect);
    }

    // Setters

    public void setMainStatus(String mainStatus) {
        this.mainStatus = mainStatus;
    }

    public void setContainer(View container) {
        this.container = container;
    }

    public void setSubStatuses(String[] subStatuses) {
        this.subStatuses = subStatuses;
    }

    public void setRect(Rect rect) {
        this.rect = rect;
    }

    // Methods
    public void addSoldier(RoundedImageView soldier) {
        if (!soldiers.contains(soldier)) {
            soldier.setTag(R.string.main_status, mainStatus);
            soldiers.add(soldier);
        }
        arrangeSoldiersOnScreen();
    }

    public void removeSoldier(RoundedImageView soldier) {

        if (soldiers.contains(soldier)) {
            soldier.setTag(R.string.main_status, null);
            soldiers.remove(soldier);
        }

        arrangeSoldiersOnScreen();

    }

    private void arrangeSoldiersOnScreen() {

        if ((getRect() != null) && (soldiers != null) && soldiers.size() > 0) {

            int nImagesInFloorCapacity = 3;

            if  (soldiers.get(0) != null) {
                // Check how many soldiers images fix into layout and set this many in each level
                nImagesInFloorCapacity = ((getRect().right - getRect().left) / 160);
                if (nImagesInFloorCapacity == 0) {
                    nImagesInFloorCapacity = 1;
                }
            }

            int heightLevel = -1;
            int widthLevel = 0;
            for (int i = 0; i < soldiers.size(); i++) {
                // If we need to go down a level
                if (i % nImagesInFloorCapacity == 0) {
                    heightLevel++;
                    widthLevel = 0;
                }

                soldiers.get(i).setX(getRect().left + widthLevel * 160);
                soldiers.get(i).setY(getRect().top + heightLevel * 160);
                widthLevel++;
            }
        }
    }
}
