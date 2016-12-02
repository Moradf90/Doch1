package t.a.m.com.doch1;

import android.app.Activity;
import android.content.ClipData;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.apmem.tools.layouts.FlowLayout;

import java.util.HashMap;
import java.util.Map;

import t.a.m.com.doch1.views.RoundedImageView;

public class MainActivity extends Activity {

    private static final int ENLARGE_ON_DARG = 2;
    private static final long DOUBLE_PRESS_INTERVAL = 500;
    private int _nImageSizeOnDrop = 140;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int[] drawableRes = new int[]{R.drawable.morad72, R.drawable.tom72, R.drawable.michal72, R.drawable.batel72, R.drawable.amit72, R.drawable.tal72};

        for (int i = 0; i < drawableRes.length; i++) {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(_nImageSizeOnDrop, _nImageSizeOnDrop);

            RoundedImageView soldierImage = new RoundedImageView(this);

            soldierImage.setLayoutParams(layoutParams);
            soldierImage.setImageResource(drawableRes[i]);

            soldierImage.setOnTouchListener(new MyTouchListener());
            FlowLayout btm = (FlowLayout) findViewById(R.id.topleft);
            btm.addView(soldierImage);
        }

//        findViewById(R.id.topleft).setOnDragListener(new MyDragListener());
//        findViewById(R.id.topright).setOnDragListener(new MyDragListener());
//        findViewById(R.id.bottomleft).setOnDragListener(new MyDragListener());
//        findViewById(R.id.bottomright).setOnDragListener(new MyDragListener());

        // Set drag listeners
        LinearLayout rootLinearLayout = (LinearLayout) findViewById(R.id.root);
        int countRoot = rootLinearLayout.getChildCount();
        for (int i = 0; i < countRoot; i++) {
            LinearLayout vParent = (LinearLayout) rootLinearLayout.getChildAt(i);
            if (vParent instanceof LinearLayout) {
                int countStatuses = rootLinearLayout.getChildCount();
                for (int j = 0; j < countStatuses; j++) {
                    View v = vParent.getChildAt(j);
                    if (v instanceof org.apmem.tools.layouts.FlowLayout) {
                        v.setOnDragListener(new MyDragListener());
                    }
                }
            }
        }
    }

    private final class MyTouchListener implements View.OnTouchListener {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
            {
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new MyDragShadowBuilder(
                        view);
                view.startDrag(data, shadowBuilder, view, 0);
                // TODO: if you click many times fast it remains invisible so think about timeout or something
//                view.setVisibility(View.INVISIBLE);

                // Detect double click:

                // Get current time in nano seconds.
                long pressTime = System.currentTimeMillis();
                long lastPressTime = 0;
                if (view.getTag(R.string.last_press_time) != null) {
                    lastPressTime = Long.parseLong(view.getTag(R.string.last_press_time).toString());
                }
                // If double click..
                long diff = pressTime - lastPressTime;
                if (diff <= DOUBLE_PRESS_INTERVAL) {
                    Toast.makeText(getApplicationContext(),String.valueOf(diff), Toast.LENGTH_SHORT).show();
                }

                // record the last time the menu button was pressed.
                view.setTag(R.string.last_press_time, pressTime);
                return true;
            } else {
                return false;
            }
        }
    }

    class MyDragListener implements View.OnDragListener {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            int action = event.getAction();
            View view = (View) event.getLocalState();

            ViewGroup owner = (ViewGroup) view.getParent();

            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    // do nothing
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    break;
                case DragEvent.ACTION_DROP:

                    owner.removeView(view);
                    FlowLayout container = (FlowLayout) v;
                    container.addView(view);
                    view.setVisibility(View.VISIBLE);

                    break;
                case DragEvent.ACTION_DRAG_ENDED:

                default:
                    break;
            }
            return true;
        }
    }

    private static class MyDragShadowBuilder extends View.DragShadowBuilder {

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
}

