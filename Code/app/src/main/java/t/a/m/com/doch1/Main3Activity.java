package t.a.m.com.doch1;

import android.app.Activity;
import android.content.ClipData;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import t.a.m.com.doch1.views.RoundedImageView;

public class Main3Activity extends Activity {

    private int _nImageSizeOnDrag = 280;
    private int _nImageSizeOnDrop = 140;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        int[] drawableRes = new int[]{R.drawable.morad72, R.drawable.tom72, R.drawable.michal72, R.drawable.batel72, R.drawable.amit72, R.drawable.tal72};

        for (int i = 0; i < drawableRes.length; i++) {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(_nImageSizeOnDrop, _nImageSizeOnDrop);

            RoundedImageView soldierImage = new RoundedImageView(this);

            soldierImage.setLayoutParams(layoutParams);
            soldierImage.setImageResource(drawableRes[i]);

            soldierImage.setOnTouchListener(new MyTouchListener());
            LinearLayout btm = (LinearLayout) findViewById(R.id.topright);
            btm.addView(soldierImage);
        }

        findViewById(R.id.topleft).setOnDragListener(new MyDragListener());
        findViewById(R.id.topright).setOnDragListener(new MyDragListener());
        findViewById(R.id.bottomleft).setOnDragListener(new MyDragListener());
        findViewById(R.id.bottomright).setOnDragListener(new MyDragListener());
    }

    private final class MyTouchListener implements View.OnTouchListener {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
            {
                android.view.ViewGroup.LayoutParams imgLayoutParams = view.getLayoutParams();

//                imgLayoutParams.width = _nImageSizeOnDrag;
//                imgLayoutParams.height = _nImageSizeOnDrag;
//                view.setLayoutParams(imgLayoutParams);

                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(
                        view);
                view.startDrag(data, shadowBuilder, view, 0);
                view.setVisibility(View.INVISIBLE);
                return true;
            } else {
                return false;
            }
        }
    }

    class MyDragListener implements View.OnDragListener {
        Drawable enterShape = getResources().getDrawable(
                R.drawable.shape_droptarget);
        Drawable normalShape = getResources().getDrawable(R.drawable.shape);

//        if (enterShape instanceof ShapeDrawable) {
//            // cast to 'ShapeDrawable'
//            ShapeDrawable shapeDrawable = (ShapeDrawable) enterShape;
//            shapeDrawable.getPaint().setColor(getResources().getColor(R.color.colorAccent));
//        } else if (enterShape instanceof GradientDrawable) {
//            // cast to 'GradientDrawable'
//            GradientDrawable gradientDrawable = (GradientDrawable) enterShape;
//            gradientDrawable.setColor(getResources().getColor(R.color.common_google_signin_btn_text_dark_default));
//        } else if (enterShape instanceof ColorDrawable) {
//            // alpha value may need to be set again after this call
//            ColorDrawable colorDrawable = (ColorDrawable) enterShape;
//            colorDrawable.setColor(getResources().getColor(R.color.inputLabelColor));
//        }

        @Override
        public boolean onDrag(View v, DragEvent event) {
            int action = event.getAction();
            View view = (View) event.getLocalState();

            android.view.ViewGroup.LayoutParams imgLayoutParams = view.getLayoutParams();

            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
//                    imgLayoutParams.width = _nImageSizeOnDrag;
//                    imgLayoutParams.height = _nImageSizeOnDrag;
//                    view.setLayoutParams(imgLayoutParams);
                    // do nothing
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    v.setBackgroundDrawable(enterShape);
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    v.setBackgroundDrawable(normalShape);
                    break;
                case DragEvent.ACTION_DROP:
                    // Dropped, reassign View to ViewGroup
                    imgLayoutParams.width = _nImageSizeOnDrop;
                    imgLayoutParams.height = _nImageSizeOnDrop;
                    view.setLayoutParams(imgLayoutParams);

                    ViewGroup owner = (ViewGroup) view.getParent();
                    owner.removeView(view);
                    LinearLayout container = (LinearLayout) v;
                    container.addView(view);
                    view.setVisibility(View.VISIBLE);
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    v.setBackgroundDrawable(normalShape);
                default:
                    break;
            }
            return true;
        }
    }
}

