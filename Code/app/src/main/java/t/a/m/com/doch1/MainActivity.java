package t.a.m.com.doch1;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import t.a.m.com.doch1.views.RoundedImageView;

//import com.skholingua.android.dragndrop_relativelayout.R;

public class MainActivity extends Activity {

	private ImageView img;
	private ViewGroup rootLayout;
	private int _xDelta;
	private int _yDelta;

	Rect[] statusesRects;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		String value = intent.getStringExtra("user"); //if it's a string you stored.

		setContentView(R.layout.activity_main);
		rootLayout = (ViewGroup) findViewById(R.id.view_root);




		int[] drawableRes = new int[]{R.drawable.morad72, R.drawable.tom72, R.drawable.michal72};

		for(int i=0;i<3;i++)
		{
			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(120, 120);

			RoundedImageView soldierImage = new RoundedImageView(this);

			soldierImage.setLayoutParams(layoutParams);
			soldierImage.setMaxHeight(10);
			soldierImage.setMaxWidth(10);
			soldierImage.setX(i * 150);
			soldierImage.setY(100);
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
	}

	@Override
	public void onWindowFocusChanged (boolean hasFocus) {
		LinearLayout[] statusesLayouts = new LinearLayout[]
				{
						(LinearLayout) findViewById(R.id.a_status_layout),
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

				imgLayoutParams.width = 225;
				imgLayoutParams.height = 225;
				touchedImage.setLayoutParams(imgLayoutParams);

				break;
			case MotionEvent.ACTION_UP:
				imgLayoutParams.width = 120;
				imgLayoutParams.height = 120;
				touchedImage.setLayoutParams(imgLayoutParams);
				getStatusOfView(touchedImage);
				break;
			case MotionEvent.ACTION_POINTER_DOWN:
				break;
			case MotionEvent.ACTION_POINTER_UP:
				imgLayoutParams.width = 120;
				imgLayoutParams.height = 120;
				touchedImage.setLayoutParams(imgLayoutParams);
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
			if (Rect.intersects(myViewRect, statusesRects[i])) {
				Toast.makeText(this, "status is number -> " + i, Toast.LENGTH_SHORT).show();
				break;
			}
		}
	}
}



