package t.a.m.com.doch1.views;

import android.app.DialogFragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.List;

import t.a.m.com.doch1.MainFragment;
import t.a.m.com.doch1.R;

/**
 * Created by tom on 31-Dec-16.
 */
public class ClusterDialogFragment extends DialogFragment implements View.OnLongClickListener {

    List<CircleImageView> images;
    ViewGroup container;

    public ClusterDialogFragment(ViewGroup container, List<CircleImageView> images){
        this.images = images;
        this.container = container;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.custom, container, false);
        getDialog().setTitle("Simple Dialog");
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.cluster_layout);

        for(int index = 0; index < images.size(); index++){
            CircleImageView copy = new CircleImageView(getActivity());
            copy.setImageResource(R.drawable.profile_pic);
            copy.setTag(index);
            copy.setOnLongClickListener(this);
            layout.addView(copy);
        }

//        image.setOnLongClickListener(this);
    }

    @Override
    public boolean onLongClick(View view) {



        dismiss();

//        long downTime = SystemClock.uptimeMillis();
//        long eventTime = SystemClock.uptimeMillis() + 1000 * 60;
//        MotionEvent motionEvent = MotionEvent.obtain(
//                downTime,
//                eventTime, 141 , 0, 0, 0);
//        container.dispatchTouchEvent(motionEvent);

                int index = (Integer) view.getTag();
                View source = images.get(index);
               source.setVisibility(View.VISIBLE);
//
//
//
//        MyHandler.startDrag(source);

        MainFragment.instance().test(container, index);

        return true;
    }

    public static class MyHandler implements Runnable {
        View view;
        Handler handler;
        private MyHandler(View view){
            this.view = view;
            handler = new Handler();
            handler.postDelayed(this, 5000);
        }

        public static void startDrag(View view){
            new MyHandler(view);
        }

        @Override
        public void run() {
            // Obtain MotionEvent object
            long downTime = SystemClock.uptimeMillis();
            long eventTime = SystemClock.uptimeMillis() + 1000 * 60;
            MotionEvent motionEvent = MotionEvent.obtain(
                    downTime,
                    eventTime,
                    MotionEvent.ACTION_DOWN, 0, 0, 0);
            view.dispatchTouchEvent(motionEvent);
        }
    }
}