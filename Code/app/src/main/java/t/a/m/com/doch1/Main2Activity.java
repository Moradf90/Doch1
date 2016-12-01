package t.a.m.com.doch1;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import t.a.m.com.doch1.views.RoundedImageView;

public class Main2Activity extends AppCompatActivity {

    private ImageView myImage;
    private static final String IMAGEVIEW_TAG = "The Android Logo";
    private int _nImageSizeOnDrop = 160;

    /** Called when the activity is first created. */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        myImage = (ImageView)findViewById(R.id.image);
        // Sets the tag
        myImage.setTag(IMAGEVIEW_TAG);

        // set the listener to the dragging data
        myImage.setOnLongClickListener(new MyClickListener());

        ImageView myImage2 = (ImageView)findViewById(R.id.image2);
        // Sets the tag
        myImage2.setTag(IMAGEVIEW_TAG + "#");

        // set the listener to the dragging data
        myImage2.setOnLongClickListener(new MyClickListener());

        findViewById(R.id.a).setOnDragListener(new MyDragListener());
        findViewById(R.id.b).setOnDragListener(new MyDragListener());
        findViewById(R.id.bottomlinear).setOnDragListener(new MyDragListener());
//        findViewById(R.id.c_status_layout).setOnDragListener(new MyDragListener());
//        findViewById(R.id.d_status_layout).setOnDragListener(new MyDragListener());

        int[] drawableRes = new int[]{R.drawable.morad72, R.drawable.tom72, R.drawable.michal72, R.drawable.batel72, R.drawable.amit72, R.drawable.tal72};

        for (int i = 0; i < drawableRes.length; i++) {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(_nImageSizeOnDrop, _nImageSizeOnDrop);

            RoundedImageView soldierImage = new RoundedImageView(this);

            soldierImage.setLayoutParams(layoutParams);
            soldierImage.setX(i * 50);
            soldierImage.setY(100);
            soldierImage.setTag(IMAGEVIEW_TAG + i);

            soldierImage.setTag(R.string.soldier_name, "Tom Dinur");
            soldierImage.setImageResource(drawableRes[i]);

            // Adds the view to the layout
            LinearLayout aStatus = (LinearLayout) findViewById(R.id.bottomlinear);
//			rootLayout.addView(soldierImage);
            soldierImage.setOnLongClickListener(new MyClickListener());

            aStatus.addView(soldierImage);

//			lstContainers.get(0).addSoldier(soldierImage);
        }

    }

    private final class MyClickListener implements View.OnLongClickListener {

        // called when the item is long-clicked
        @Override
        public boolean onLongClick(View view) {
            // TODO Auto-generated method stub

            // create it from the object's tag
            ClipData.Item item = new ClipData.Item((CharSequence)view.getTag());

            String[] mimeTypes = { ClipDescription.MIMETYPE_TEXT_PLAIN };
            ClipData data = new ClipData(view.getTag().toString(), mimeTypes, item);
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);

            view.startDrag( data, //data to be dragged
                    shadowBuilder, //drag shadow
                    view, //local data about the drag and drop operation
                    0   //no needed flags
            );


            view.setVisibility(View.INVISIBLE);
            return true;
        }
    }

    class MyDragListener implements View.OnDragListener {

        @Override
        public boolean onDrag(View v, DragEvent event) {

            // Handles each of the expected events
            switch (event.getAction()) {

                //signal for the start of a drag and drop operation.
                case DragEvent.ACTION_DRAG_STARTED:
                    // do nothing
                    break;

                //the drag point has entered the bounding box of the View
                case DragEvent.ACTION_DRAG_ENTERED:
                    v.setBackgroundColor(Color.rgb(56,56,56));	//change the shape of the view
                    break;

                //the user has moved the drag shadow outside the bounding box of the View
                case DragEvent.ACTION_DRAG_EXITED:
                    v.setBackgroundColor(Color.rgb(176,176,176));	//change the shape of the view back to normal
                    break;

                //drag shadow has been released,the drag point is within the bounding box of the View
                case DragEvent.ACTION_DROP:
                    // if the view is the bottomlinear, we accept the drag item
                    if(v != findViewById(R.id.bottomlinear)) {
                        View view = (View) event.getLocalState();
                        ViewGroup viewgroup = (ViewGroup) view.getParent();
                        viewgroup.removeView(view);

                        //change the text
                        TextView text = (TextView) v.findViewById(R.id.text);
                        text.setText("The item is dropped");

                        LinearLayout containView = (LinearLayout) v;
                        containView.addView(view);
                        view.setVisibility(View.VISIBLE);
                    } else {
                        View view = (View) event.getLocalState();
                        view.setVisibility(View.VISIBLE);
                        Context context = getApplicationContext();
                        Toast.makeText(context, "You can't drop the image here",
                                Toast.LENGTH_LONG).show();
                        break;
                    }
                    break;

                //the drag and drop operation has concluded.
                case DragEvent.ACTION_DRAG_ENDED:
                    v.setBackgroundColor(Color.rgb(176,176,176));	//go back to normal shape

                default:
                    break;
            }
            return true;
        }
    }
}
