package t.a.m.com.doch1.views;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import t.a.m.com.doch1.DragNDrop.MyTouchListener;
import t.a.m.com.doch1.R;

/**
 * Created by tom on 31-Dec-16.
 */
public class ClusterDialogFragment extends DialogFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.custom, container, false);
        getDialog().setTitle("Simple Dialog");

        ImageView image = (ImageView) rootView.findViewById(R.id.image);
        image.setImageResource(R.drawable.ic_launcher);
        image.setOnTouchListener(new MyTouchListener());

        return rootView;
    }
}