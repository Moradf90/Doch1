package t.a.m.com.doch1;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Layout;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import t.a.m.com.doch1.Models.StatusContainer;
import t.a.m.com.doch1.views.RoundedImageView;

public class MainActivity extends Activity {

	private ViewGroup rootLayout;
	private int _xDelta;
	private int _yDelta;
	private int _nImageSizeOnDrag = 280;
	private int _nImageSizeOnDrop = 160;
	final int DOUBLE_PRESS_INTERVAL = 500;

	boolean bIsRectInit = false;

	List<StatusContainer> lstContainers;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		String value = intent.getStringExtra("user"); //if it's a string you stored.

		setContentView(R.layout.activity_main);
		rootLayout = (ViewGroup) findViewById(R.id.view_root);

		lstContainers = new ArrayList<>();
		initContainers();

		int[] drawableRes = new int[]{R.drawable.morad72, R.drawable.tom72, R.drawable.michal72, R.drawable.batel72, R.drawable.amit72, R.drawable.tal72};

		for (int i = 0; i < drawableRes.length; i++) {
			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(_nImageSizeOnDrop, _nImageSizeOnDrop);

			RoundedImageView soldierImage = new RoundedImageView(this);

			soldierImage.setLayoutParams(layoutParams);
			soldierImage.setX(i * 150);
			soldierImage.setY(100);
			soldierImage.setTag(R.string.soldier_name, "Tom Dinur");
			soldierImage.setImageResource(drawableRes[i]);

			// Adds the view to the layout
			rootLayout.addView(soldierImage);
			lstContainers.get(0).addSoldier(soldierImage);
			soldierImage.setOnTouchListener(new ChoiceTouchListener());
		}
	}

	private void initContainers() {
		lstContainers.add(new StatusContainer((LinearLayout) findViewById(R.id.a_status_layout), "A", new String[]{}));
		lstContainers.add(new StatusContainer((LinearLayout) findViewById(R.id.b_status_layout), "B", new String[]{"מיוחדת 1", "מחלה", "מחוץ ליחידה"}));
		lstContainers.add(new StatusContainer((LinearLayout) findViewById(R.id.c_status_layout), "C", new String[]{"הריון", "יום אבל", "מחלה בהצהרה"}));
		lstContainers.add(new StatusContainer((LinearLayout) findViewById(R.id.d_status_layout), "D", new String[]{"מיוחדת 3", "שחרור", "מטיול" , "יום סידורים", "חוץ לארץ"}));
	}

	private void openSpinner(final View view) {
		if (view.getTag(R.string.main_status) != null) {
			String currentStatus = view.getTag(R.string.main_status).toString();
			StatusContainer mainStatusContainer = getContainerByStatus(currentStatus);

			if (mainStatusContainer != null) {
				AlertDialog.Builder b = new AlertDialog.Builder(this);
				b.setTitle(currentStatus + " status");

				b.setItems(mainStatusContainer.getSubStatuses(), new DialogInterface.OnClickListener() {

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

		// TODO: understand why the rect sizes are changed
		if (!bIsRectInit) {
			bIsRectInit = true;
			for (int i = 0; i < lstContainers.size(); i++) {
				View currLayout = lstContainers.get(i).getContainer();
				Rect rect;
				// TODO: understand why the bottom layout starts from 0
				if (i < 2) {
					rect = new Rect(currLayout.getLeft(),
							currLayout.getTop(),
							currLayout.getRight(),
							currLayout.getBottom());
				} else {
					rect = new Rect(currLayout.getLeft(),
							currLayout.getTop() + lstContainers.get(0).getContainer().getBottom(),
							currLayout.getRight(),
							currLayout.getBottom() + lstContainers.get(0).getContainer().getBottom());
				}
				lstContainers.get(i).setRect(rect);
			}
		}
	}

	public final class ChoiceTouchListener implements OnTouchListener {

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

				// TODO: detect long click
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

		// Remove from the old container
		StatusContainer oldContainer = getContainerByStatus((String) view.getTag(R.string.main_status));
		if (oldContainer != null) {
			oldContainer.removeSoldier((RoundedImageView) view);
		}
		else {
			int x = 4;
		}

		for(int i=0; i<lstContainers.size(); i++) {
			if (lstContainers.get(i).getRect().contains(myViewRect)) {
				lstContainers.get(i).addSoldier((RoundedImageView) view);

				Toast.makeText(this, "status is -> " + (char)('A' + i), Toast.LENGTH_SHORT).show();
				return;
			}
			else if (lstContainers.get(i).getRect().intersect(myViewRect)) {
				lstContainers.get(i).addSoldier((RoundedImageView) view);

				Toast.makeText(this, "status is -> " + (char)('A' + i), Toast.LENGTH_SHORT).show();
				return;
			}
		}
	}

	private StatusContainer getContainerByStatus(final String status) {
		for(StatusContainer con : lstContainers) {
			if(con.getMainStatus().equals(status)) {
				return con;
			}
		}
		return null;
	}
}



