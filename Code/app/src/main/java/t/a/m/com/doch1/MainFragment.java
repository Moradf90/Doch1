package t.a.m.com.doch1;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.apmem.tools.layouts.FlowLayout;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import t.a.m.com.doch1.Models.GlobalsTemp;
import t.a.m.com.doch1.Models.MainStatus;
import t.a.m.com.doch1.Models.User;

import java.util.Random;

import t.a.m.com.doch1.views.MySpinner;
import t.a.m.com.doch1.views.RoundedImageView;

public class MainFragment extends Fragment {

    private static final int ENLARGE_ON_DARG = 2;
    private static final long DOUBLE_PRESS_INTERVAL = 500;
    private int _nImageSizeOnDrop = 140;
    List<User> lstSoldiers;
    Map<String, List<String>> mapMainStatusToSub;
    List<String> lstMain;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setTitle(R.string.main_fragment_title);

        lstSoldiers = new ArrayList<>();
        mapMainStatusToSub = new HashMap<>();
        lstMain = new ArrayList<>();

        final ProgressDialog progress = new ProgressDialog(getActivity());
        progress.setTitle(getString(R.string.loading_title));
        progress.setMessage(getString(R.string.loading_message));
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        final View vFragmentLayout = inflater.inflate(R.layout.activity_main, container, false);

        getActivity().setTitle(R.string.main_fragment_title);

        final LinearLayout rootLinearLayout = (LinearLayout) vFragmentLayout.findViewById(R.id.root);

        FirebaseDatabase.getInstance().getReference(MainStatus.STATUSES_REFERENCE_KEY).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot statusSnapshot : dataSnapshot.getChildren()) {
                    MainStatus currStatus = statusSnapshot.getValue(MainStatus.class);
                    mapMainStatusToSub.put(currStatus.getName(), currStatus.getSubStatuses() != null ? currStatus.getSubStatuses() : new ArrayList<String>());
                    lstMain.add(currStatus.getName());
                }

                buildLayout();
                pullSoldiers();
            }


            private void buildLayout() {

                int colsSize = 3;
                int rowsSize = lstMain.size() / colsSize;

                rootLinearLayout.setWeightSum(rowsSize);

                for (int rowIndex = 0; rowIndex < rowsSize; rowIndex++) {
                    // Create new row
                    LinearLayout newRow = new LinearLayout(getActivity());

                    LinearLayout.LayoutParams newRowParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1);

                    newRow.setWeightSum(colsSize);
                    newRow.setOrientation(LinearLayout.HORIZONTAL);
                    newRow.setLayoutParams(newRowParams);

                    for (int colIndex = 0; colIndex < colsSize; colIndex++) {
                        FlowLayout newCol = new FlowLayout(getActivity());

                        if (rowIndex == 0 && colIndex ==0) {
                            newCol.setId(R.id.defaultStatus);
                        }

                        LinearLayout.LayoutParams colParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.FILL_PARENT, 1);

                        newCol.setLayoutParams(colParams);
                        newCol.setTag(R.string.main_status, lstMain.get(rowIndex * colsSize + colIndex));

                        newCol.setOnDragListener(new MyDragListener());

                        TextView textView = new TextView(getActivity());
                        textView.setGravity(Gravity.CENTER);
                        textView.setText(lstMain.get(rowIndex * colsSize + colIndex));

                        newCol.addView(textView);

                        Random rnd = new Random();
                        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                        newCol.setBackgroundColor(color);



                        newRow.addView(newCol);
                    }

                    rootLinearLayout.addView(newRow);

                }
            }

            private void pullSoldiers() {
                FirebaseDatabase.getInstance().getReference(User.USERS_REFERENCE_KEY).orderByChild(User.GROUP_ID_PROPERTY)
                        // TODO: change
                        .equalTo("21827933-d057-4ada-a51e-816cd46a586d")
                        // must be single event or the images will be added over and over again
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                for (DataSnapshot usrSnapshot : dataSnapshot.getChildren()) {
                                    User currUser = usrSnapshot.getValue(User.class);
                                    lstSoldiers.add(currUser);
                                }

                                setSoldiersOnStatuses(vFragmentLayout);
                                progress.dismiss();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return vFragmentLayout;
    }

    private void setSoldiersOnStatuses(View vFragmentLayout) {

        for (int i = 0; i < lstSoldiers.size(); i++) {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(_nImageSizeOnDrop, _nImageSizeOnDrop);

            RoundedImageView soldierImage = new RoundedImageView(getActivity());

            soldierImage.setLayoutParams(layoutParams);
            Picasso.with(getActivity()).load(lstSoldiers.get(i).getImage()).into(soldierImage);

            FlowLayout btm = (FlowLayout) vFragmentLayout.findViewById(R.id.defaultStatus);

            if (lstSoldiers.get(i).getMainStatus().equals("")) {
                lstSoldiers.get(i).setMainStatus((String) btm.getTag(R.string.main_status));
                lstSoldiers.get(i).update();
                btm.addView(soldierImage);
            }

            // TODO: put the image on the right layout - if has mainStatus

            soldierImage.setTag(R.string.soldier, lstSoldiers.get(i));

            soldierImage.setOnTouchListener(new MyTouchListener());
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
                    showPopup(view);
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
            View imgSoldier = (View) event.getLocalState();

            if (imgSoldier != null) {
                ViewGroup oldLayout = (ViewGroup) imgSoldier.getParent();

                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        // do nothing
                        break;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        break;
                    case DragEvent.ACTION_DROP:

                        oldLayout.removeView(imgSoldier);
                        FlowLayout newLayout = (FlowLayout) v;
                        newLayout.addView(imgSoldier);
                        imgSoldier.setVisibility(View.VISIBLE);

                        // New mainStatus, Clear the sub status
                        if (oldLayout != newLayout) {
                            User sld = ((User) imgSoldier.getTag(R.string.soldier));
                            // Set main status
                            sld.setMainStatus((String) newLayout.getTag(R.string.main_status));

                            // Clear sub status
                            sld.setSubStatus(null);
                            sld.update();

                            // Update drawer
                            ((DrawerActivity) getActivity()).updateSoldiersStatuses();
                        }

                        break;
                    case DragEvent.ACTION_DRAG_ENDED:

                    default:
                        break;
                }
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

    public void showPopup(final View imgSoldier) {

        User sld = null;
        // If there is already selected sub status - select it
        if (imgSoldier.getTag(R.string.soldier) != null) {
            sld = ((User) imgSoldier.getTag(R.string.soldier));
        }

        if (sld != null) {
            List<String> subStatuses = mapMainStatusToSub.get(sld.getMainStatus());
            // Open spinner only if there is sub status
            if ((subStatuses == null) || (subStatuses.size() == 0)) {
                Toast.makeText(getActivity(), "no sub statuses", Toast.LENGTH_SHORT).show();
            }
            else {

                LayoutInflater layoutInflater =
                        LayoutInflater.from(getActivity());
                View popupView = layoutInflater.inflate(R.layout.sub_status_popup, null);
                final PopupWindow popupWindow = new PopupWindow(
                        popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                final MySpinner popupSpinner = (MySpinner) popupView.findViewById(R.id.popupspinner);
                TextView txtSoldierName = (TextView) popupView.findViewById(R.id.txt_soldier_name);
//        if (imgSoldier.getTag(R.string.soldier_name) != null) {
//            txtSoldierName.setText(imgSoldier.getTag(R.string.soldier_name).toString());
//        }

                txtSoldierName.setText(((User) imgSoldier.getTag(R.string.soldier)).getName());

                ArrayAdapter<String> adapter =
                        new ArrayAdapter<String>(getActivity(),
                                android.R.layout.simple_spinner_item, subStatuses);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                popupSpinner.setAdapter(adapter);

                final Boolean[] bFirstTimeSeleceted = {true};

                popupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                imgSoldier.setTag(R.string.sub_status, i);
                        User soldier = (User) imgSoldier.getTag(R.string.soldier);
                        soldier.setSubStatus(popupSpinner.getSelectedItem().toString());
                        soldier.update();
                        // Update drawer
                        ((DrawerActivity) getActivity()).updateSoldiersStatuses();

                        if (!bFirstTimeSeleceted[0]) {
                            final Handler handler = new Handler();

                            final Runnable runnable = new Runnable() {
                                @Override
                                public void run() {
                                    if (popupWindow.isShowing()) {
                                        popupWindow.dismiss();
                                    }
                                }
                            };

                            handler.postDelayed(runnable, 1000);
                        }
                        bFirstTimeSeleceted[0] = false;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                // If there is already selected sub status - select it
                if ((sld.getSubStatus() != null) && (!sld.getSubStatus().equals(""))) {

                    String selectedOptionValue = sld.getSubStatus();
                    popupSpinner.setSelection(((ArrayAdapter<String>) popupSpinner.getAdapter()).getPosition(selectedOptionValue));

//                ArrayAdapter<CharSequence> charAdaper = ArrayAdapter.createFromResource(this, R.array.select_state, android.R.layout.simple_spinner_item);
//                charAdaper.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                popupSpinner.setAdapter(charAdaper);
//                if (!selectedOptionValue.equals(null)) {
//                    int spinnerPosition = charAdaper.getPosition(selectedOptionValue);
//                    popupSpinner.setSelection(spinnerPosition);
//                }
                }

                popupWindow.setFocusable(true);
                popupWindow.showAsDropDown(imgSoldier, 50, -30);
            }
        }
    }
}

