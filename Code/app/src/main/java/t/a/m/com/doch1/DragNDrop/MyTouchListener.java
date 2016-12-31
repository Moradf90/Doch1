package t.a.m.com.doch1.DragNDrop;

import android.content.ClipData;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by tom on 31-Dec-16.
 */
public final class MyTouchListener implements View.OnTouchListener {
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
        {
            ClipData data = ClipData.newPlainText("", "");
            View.DragShadowBuilder shadowBuilder = new MyDragShadowBuilder(
                    view);
            view.startDrag(data, shadowBuilder, view, 0);
            // TODO: if you click many times fast it remains invisible so think about timeout or something
//                view.setVisibility(View.INVISIBLE);
            return true;
        } else {
            return false;
        }
    }
}