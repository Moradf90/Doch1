package t.a.m.com.doch1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

//import com.skholingua.android.dragndrop_relativelayout.R;

public class MainActivity extends Activity {

	private ImageView img;
	private ViewGroup rootLayout;
	private int _xDelta;
	private int _yDelta;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		String value = intent.getStringExtra("user"); //if it's a string you stored.

		setContentView(R.layout.activity_main);
		rootLayout = (ViewGroup) findViewById(R.id.view_root);

		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(150, 150);

		for(int i=0;i<6;i++)
		{
			ImageView image = new ImageView(this);
			AbsoluteLayout.LayoutParams param = new AbsoluteLayout.LayoutParams(120, 120,10 *i, 10* i);

			image.setLayoutParams(param);
			image.setMaxHeight(20);
			image.setMaxWidth(20);
			image.setImageResource(R.drawable.ic_launcher);

			// Adds the view to the layout
			rootLayout.addView(image);
			image.setLayoutParams(layoutParams);
			image.setOnTouchListener(new ChoiceTouchListener());
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

}



