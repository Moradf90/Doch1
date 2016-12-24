package t.a.m.com.doch1;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateUtils;
import android.util.Log;
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

import com.activeandroid.query.Select;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import t.a.m.com.doch1.Models.Group;
import t.a.m.com.doch1.Models.MainStatus;
import t.a.m.com.doch1.Models.User;

import java.util.Random;

import t.a.m.com.doch1.Models.UserInGroup;
import t.a.m.com.doch1.common.SQLHelper;
import t.a.m.com.doch1.views.CircleImageView;
import t.a.m.com.doch1.views.MySpinner;

public class MainFragment extends Fragment {

    String groupID = "3";
    private static final int ENLARGE_ON_DARG = 2;
    private static final long DOUBLE_PRESS_INTERVAL = 500;
    private int _nImageSizeOnDrop = 125;
    List<User> lstSoldiers;
    Map<String, List<String>> mapMainStatusToSub;
    List<String> lstMain;

    public MainFragment() {
    }

    public MainFragment(String strGroupID) {
        this.groupID = strGroupID;
    }

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

//        List<MainStatus> mainStatuses =  new Select().from(MainStatus.class).execute();
//
//        for (MainStatus cnotepaddurrStatus : mainStatuses) {
//            mapMainStatusToSub.put(currStatus.getName(), currStatus.getSubStatuses() != null ? currStatus.getSubStatuses() : new ArrayList<String>());
//            lstMain.add(currStatus.getName());
//        }
//
//        // TODO: buildLayout should be called once. - not any creation
//        Map<String, ViewGroup> mapMainStatusToView = buildLayout();
//        pullSoldiers(mapMainStatusToView);


        // Get possible statuses
        FirebaseDatabase.getInstance().getReference(MainStatus.STATUSES_REFERENCE_KEY).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot statusSnapshot : dataSnapshot.getChildren()) {
                    MainStatus currStatus = statusSnapshot.getValue(MainStatus.class);
                    mapMainStatusToSub.put(currStatus.getName(), currStatus.getSubStatuses() != null ? currStatus.getSubStatuses() : new ArrayList<String>());
                    lstMain.add(currStatus.getName());
                }

                // TODO: buildLayout should be called once. - not any creation
                Map<String, ViewGroup> mapMainStatusToView = buildLayout();
                pullSoldiers(mapMainStatusToView);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

            private Map<String, ViewGroup> buildLayout() {

                Map<String, ViewGroup> mapMainStatusToView = new HashMap<String, ViewGroup>();

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

                        String mainStatus = lstMain.get(rowIndex * colsSize + colIndex);
                        newCol.setLayoutParams(colParams);
                        newCol.setTag(R.string.main_status, mainStatus);
                        mapMainStatusToView.put(mainStatus, newCol);

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

                return mapMainStatusToView;
            }

            private void pullSoldiers(final Map<String, ViewGroup> mapMainStatusToView) {

                Group shownGroup =  new Select().from(Group.class).where("id = " + groupID).executeSingle();

                handleUsersOfGroup(shownGroup.getUsers(), vFragmentLayout, mapMainStatusToView, progress);
            }
        });

        return vFragmentLayout;
    }

    private void handleUsersOfGroup(final List<Long> usersID, final View vFragmentLayout, final Map<String, ViewGroup> mapMainStatusToView, final ProgressDialog progress) {

        List<User> groupUsers =  new Select().from(User.class).where("id " + SQLHelper.getInQuery(usersID)).execute();

        List<UserInGroup> usersInGroups = new Select().from(UserInGroup.class).where(UserInGroup.GROUP_PROPERTY + " = " + groupID).execute();

        for (User currUser : groupUsers) {
            for (UserInGroup userInGroup : usersInGroups) {
                if (userInGroup.getUserId().equals(currUser.getId())) {
                    // When we got the status of the current user
                    currUser.setMainStatus(userInGroup.getMainStatus());
                    currUser.setSubStatus(userInGroup.getSubStatus());
                    currUser.setLastUpdateDate(userInGroup.getLastUpdateDate());

                    break;
                }
            }
            lstSoldiers.add(currUser);
        }

        setSoldiersOnStatuses(vFragmentLayout, mapMainStatusToView, lstSoldiers);

        progress.dismiss();

    }

    private void setSoldiersOnStatuses(View vFragmentLayout, Map<String, ViewGroup> mapMainStatusToView, List<User> users) {

        FlowLayout btm = (FlowLayout) vFragmentLayout.findViewById(R.id.defaultStatus);

        for (int i = 0; i < users.size(); i++) {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(_nImageSizeOnDrop, _nImageSizeOnDrop);

            CircleImageView soldierImage = new CircleImageView(getActivity());

            User currSoldier = users.get(i);

            soldierImage.setLayoutParams(layoutParams);
            Picasso.with(getActivity()).load(currSoldier.getImage()).into(soldierImage);

            String soldierMainStatus = currSoldier.getMainStatus();
            // If there is no status,
            // or the status is irrelevant
            // or main status in the DB isnt valid
            // Then give default status
            if ((currSoldier.getLastUpdateDate() == null) ||
                    (!DateUtils.isToday(currSoldier.getLastUpdateDate().getTime())) ||
                    (soldierMainStatus.equals("")) ||
                    (!mapMainStatusToView.containsKey(soldierMainStatus))) {
                currSoldier.setMainStatus((String) btm.getTag(R.string.main_status));
                currSoldier.setSubStatus("");
                currSoldier.updateUserStatuses(groupID);
                btm.addView(soldierImage);
            }
            // If there is already main status on DB, and it's update date from today
            else {
                mapMainStatusToView.get(soldierMainStatus).addView(soldierImage);
            }

            soldierImage.setTag(R.string.soldier, currSoldier);

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
                return true;
            } else {
                return false;
            }
        }
    }

    class MyDragListener implements View.OnDragListener {
        @Override
        public boolean onDrag(View v, DragEvent event) {
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

                        if (oldLayout == newLayout) {
                            showPopupSubStatus(imgSoldier);
                        }
                        // New mainStatus, Clear the sub status
                        else {
                            User sld = ((User) imgSoldier.getTag(R.string.soldier));
                            // Set main status
                            sld.setMainStatus((String) newLayout.getTag(R.string.main_status));

                            // Clear sub status
                            sld.setSubStatus(null);
                            sld.updateUserStatuses(groupID);
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

    public void showPopupSubStatus(final View imgSoldier) {

        User soldier = null;
        // If there is already selected sub status - select it
        if (imgSoldier.getTag(R.string.soldier) != null) {
            soldier = ((User) imgSoldier.getTag(R.string.soldier));
        }

        if (soldier != null) {
            List<String> subStatuses = mapMainStatusToSub.get(soldier.getMainStatus());
            // Open spinner only if there is sub status
            if ((subStatuses == null) || (subStatuses.size() == 0)) {
                Toast.makeText(getActivity(), R.string.noSubStatusMessage, Toast.LENGTH_SHORT).show();
            }
            else {

                LayoutInflater layoutInflater =
                        LayoutInflater.from(getActivity());
                View popupView = layoutInflater.inflate(R.layout.sub_status_popup, null);
                final PopupWindow popupWindow = new PopupWindow(
                        popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                final MySpinner popupSpinner = (MySpinner) popupView.findViewById(R.id.popupspinner);
                TextView txtSoldierName = (TextView) popupView.findViewById(R.id.txt_soldier_name);

                txtSoldierName.setText(soldier.getName());

                ArrayAdapter<String> adapter =
                        new ArrayAdapter<String>(getActivity(),
                                android.R.layout.simple_spinner_item, subStatuses);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                popupSpinner.setAdapter(adapter);

                final Integer[] nTimesSelected = {0};

                final User finalSoldier = soldier;
                popupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        finalSoldier.setSubStatus(popupSpinner.getSelectedItem().toString());
                        finalSoldier.updateUserStatuses(groupID);

                        if (nTimesSelected[0] > 1) {
                            final Handler handler = new Handler();

                            final Runnable runnable = new Runnable() {
                                @Override
                                public void run() {
                                    if (popupWindow.isShowing()) {
                                        popupWindow.dismiss();
                                    }
                                }
                            };

                            handler.postDelayed(runnable, 1500);
                        }
                        nTimesSelected[0]++;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                // If there is already selected sub status - select it
                if ((soldier.getSubStatus() != null) && (!soldier.getSubStatus().equals(""))) {

                    String selectedOptionValue = soldier.getSubStatus();
                    popupSpinner.setSelection(((ArrayAdapter<String>) popupSpinner.getAdapter()).getPosition(selectedOptionValue));
                }

                popupWindow.setFocusable(true);
                popupWindow.showAsDropDown(imgSoldier, 50, -30);
            }
        }
    }
}

