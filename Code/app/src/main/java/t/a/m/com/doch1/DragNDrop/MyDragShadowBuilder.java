package t.a.m.com.doch1.DragNDrop;

import android.graphics.Canvas;
import android.graphics.Point;
import android.view.View;

/**
 * Created by tom on 31-Dec-16.
 */
public class MyDragShadowBuilder extends View.DragShadowBuilder {

    private static final int ENLARGE_ON_DARG = 2;

    private Point mScaleFactor;

    // Defines the constructor for myDragShadowBuilder
    public MyDragShadowBuilder(View v) {

        // Stores the View parameter passed to myDragShadowBuilder.
        super(v);
    }

    // Defines a callback that sends the drag shadow dimensions and touch point back to the
    // system.
    @Override
    public void onProvideShadowMetrics (Point size, Point touch) {
        // Defines local variables
        int width;
        int height;

        // Sets the width of the shadow to half the width of the original View
        width = getView().getWidth() * ENLARGE_ON_DARG;

        // Sets the height of the shadow to half the height of the original View
        height = getView().getHeight() * ENLARGE_ON_DARG;

        // Sets the size parameter's width and height values. These get back to the system
        // through the size parameter.
        size.set(width, height);

        // Sets size parameter to member that will be used for scaling shadow image.
        mScaleFactor = size;

        // Sets the touch point's position to be in the middle of the drag shadow
        touch.set(width / ENLARGE_ON_DARG, height / ENLARGE_ON_DARG);
    }

    @Override
    public void onDrawShadow(Canvas canvas) {

        // Draws the ColorDrawable in the Canvas passed in from the system.
        canvas.scale(mScaleFactor.x/(float)getView().getWidth(), mScaleFactor.y/(float)getView().getHeight());
        getView().draw(canvas);
    }
}