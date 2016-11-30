package t.a.m.com.doch1;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import t.a.m.com.doch1.Adapters.GridViewAdapter;
import t.a.m.com.doch1.Adapters.GridViewAdapter.*;
import t.a.m.com.doch1.views.RoundedImageView;

public class MainActivity extends Activity {

	private ViewGroup rootLayout;
	private int _xDelta;
	private int _yDelta;
	private int _nImageSizeOnDrag = 280;
	private int _nImageSizeOnDrop = 160;
	final int DOUBLE_PRESS_INTERVAL = 500;
	private static final Map<String, String[]> statusesMap;
	static
	{
		statusesMap = new HashMap<String, String[]>();
		statusesMap.put("A", new String[]{});
		statusesMap.put("B", new String[]{"מיוחדת 1", "מחלה", "מחוץ ליחידה"});
		statusesMap.put("C", new String[]{"הריון", "יום אבל", "מחלה בהצהרה"});
		statusesMap.put("D", new String[]{"מיוחדת 3", "שחרור", "מטיול" , "יום סידורים", "חוץ לארץ"});
	}

	Rect[] statusesRects;

	private GridView gridView;
	private GridViewAdapter gridAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		String value = intent.getStringExtra("user"); //if it's a string you stored.

		setContentView(R.layout.activity_main);
		rootLayout = (ViewGroup) findViewById(R.id.view_root);

		int[] drawableRes = new int[]{R.drawable.morad72, R.drawable.tom72, R.drawable.michal72, R.drawable.batel72, R.drawable.amit72, R.drawable.tal72};

		for(int i=0;i<drawableRes.length;i++)
		{
			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(_nImageSizeOnDrop, _nImageSizeOnDrop);

			RoundedImageView soldierImage = new RoundedImageView(this);

			soldierImage.setLayoutParams(layoutParams);
			soldierImage.setMaxHeight(10);
			soldierImage.setMaxWidth(10);
			soldierImage.setX(i * 150);
			soldierImage.setY(100);
			soldierImage.setTag(R.string.soldier_name, "Tom Dinur");
			soldierImage.setTag(R.string.main_status, "A");
			soldierImage.setImageResource(drawableRes[i]);

			// Adds the view to the layout
					rootLayout.addView(soldierImage);
			soldierImage.setOnTouchListener(new ChoiceTouchListener());

			soldierImage.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View view) {
					return false;
				}
			});
		}

		gridView = (GridView) findViewById(R.id.gridView);
		gridAdapter = new GridViewAdapter(this, R.layout.grid_item_layout, getData());
		gridView.setAdapter(gridAdapter);
	}

	// Prepare some dummy data for gridview
	private ArrayList<ImageItem> getData() {
		final ArrayList<ImageItem> imageItems = new ArrayList<>();
		TypedArray imgs = getResources().obtainTypedArray(R.array.image_ids);
		for (int i = 0; i < imgs.length(); i++) {
			Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imgs.getResourceId(i, -1));
			imageItems.add(new ImageItem(bitmap, "Image#" + i));
		}
		return imageItems;
	}

	private void openSpinner(final View view) {
		if (view.getTag(R.string.main_status) != null) {
			String currentStatus = view.getTag(R.string.main_status).toString();

			if (statusesMap.containsKey(currentStatus)) {
				AlertDialog.Builder b = new AlertDialog.Builder(this);
				b.setTitle(currentStatus + " status");
//		String[] types = {"By Zip", "By Category"};

				b.setItems(statusesMap.get(currentStatus), new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						dialog.dismiss();
						view.setTag(R.string.sub_status, which);
						switch (which) {
							case 0:
//						onZipRequested();
								break;
							case 1:
//						onCategoryRequested();
								break;
						}
					}

				});

				b.show();
			}
		}
	}

	@Override
	public void onWindowFocusChanged (boolean hasFocus) {
		LinearLayout[] statusesLayouts = new LinearLayout[]
				{
//						(LinearLayout) findViewById(R.id.a_status_layout),
						(LinearLayout) findViewById(R.id.b_status_layout),
						(LinearLayout) findViewById(R.id.c_status_layout),
						(LinearLayout) findViewById(R.id.d_status_layout),
				};

		statusesRects = new Rect[statusesLayouts.length];

		for(int i=0;i<statusesRects.length;i++) {
			int[] x= new int[2];
			statusesLayouts[i].getLocationOnScreen(x);

			int[] y = new int[2];
			statusesLayouts[i].getLocationInWindow(y);


			Rect rect = new Rect(statusesLayouts[i].getLeft(),
					statusesLayouts[i].getTop(),
					statusesLayouts[i].getRight(),
					statusesLayouts[i].getBottom());
			statusesRects[i] = rect;
		}
	}

	private final class ChoiceTouchListener implements OnTouchListener {

		public boolean onTouch(View view, MotionEvent event) {
			ImageView touchedImage = (ImageView)view;
			final int X = (int) event.getRawX();
			final int Y = (int) event.getRawY();

			android.view.ViewGroup.LayoutParams imgLayoutParams = touchedImage.getLayoutParams();

			switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
				RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
				_xDelta = X - lParams.leftMargin;
				_yDelta = Y - lParams.topMargin;

				imgLayoutParams.width = _nImageSizeOnDrag;
				imgLayoutParams.height = _nImageSizeOnDrag;
				touchedImage.setLayoutParams(imgLayoutParams);

				view.setTag(R.string.press_time, System.currentTimeMillis());

				// If double click...
				if ((view.getTag(R.string.last_press_time) != null) &&
						(Long.valueOf(view.getTag(R.string.press_time).toString()) -
								Long.valueOf(view.getTag(R.string.last_press_time).toString()) <=
								DOUBLE_PRESS_INTERVAL)) {
					openSpinner(view);
				}

				// record the last time the menu button was pressed.
				view.setTag(R.string.last_press_time, view.getTag(R.string.press_time));

				break;
			case MotionEvent.ACTION_UP:
				imgLayoutParams.width = _nImageSizeOnDrop;
				imgLayoutParams.height = _nImageSizeOnDrop;
				touchedImage.setLayoutParams(imgLayoutParams);
				getStatusOfView(touchedImage);
				break;
			case MotionEvent.ACTION_POINTER_DOWN:
				break;
			case MotionEvent.ACTION_POINTER_UP:
				break;
			case MotionEvent.ACTION_MOVE:
				RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view
						.getLayoutParams();
				layoutParams.leftMargin = X - _xDelta;
				layoutParams.topMargin = Y - _yDelta;
				layoutParams.rightMargin = -250;
				layoutParams.bottomMargin = -250;
				view.setLayoutParams(layoutParams);
				break;
			}
			rootLayout.invalidate();
			return true;
		}
	}

	// TODO :doesnt work, probably rectangles of layout arent good
	private final void getStatusOfView(View view) {

		Rect myViewRect = new Rect();
		view.getHitRect(myViewRect);

		for(int i=0; i<statusesRects.length; i++) {
			if (statusesRects[i].contains(myViewRect)) {
				// If this is the A status - no subStatus needed
				if (i == 0) {
					((ImageView)view).setColorFilter(Color.argb(100, 0, 0, 0));
				}
				// Need subStatus so we 'highlight' the soldier
				else if (i == 1){
					((ImageView)view).setColorFilter(Color.argb(10, 0, 0, 0));
				}
				else if (i==2) {
					((ImageView)view).setColorFilter(Color.argb(50, 0, 225, 255));
				}
				view.setTag(R.string.main_status, (char)('A' + i));
//				Toast.makeText(this, "status is -> " + (char)('A' + i), Toast.LENGTH_SHORT).show();
				break;
			}
		}
	}
}



