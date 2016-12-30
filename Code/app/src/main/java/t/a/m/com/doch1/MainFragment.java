package t.a.m.com.doch1;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.format.DateUtils;
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
import com.squareup.picasso.Picasso;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import t.a.m.com.doch1.Models.Group;
import t.a.m.com.doch1.Models.StatusesInGroup;
import t.a.m.com.doch1.Models.User;
import t.a.m.com.doch1.Models.UserInGroup;
import t.a.m.com.doch1.common.SQLHelper;
import t.a.m.com.doch1.views.CircleImageView;
import t.a.m.com.doch1.views.MySpinner;

public class MainFragment extends Fragment {

    private static final int STATUSES_IN_ROW_AMOUNT = 3;
    Group shownGroup;
    private static final int ENLARGE_ON_DARG = 2;
    private int ImageSizeOnDrop = 125;
    private int CLUSTER_SIZE = 200;
    List<User> lstMembers;
    Map<String, List<String>> mapMainStatusToSub;
    List<String> lstMain;
    LinearLayout rootLinearLayout;
    View vFragmentLayout;
    ProgressDialog progress;
    public static User loginUser;
    Map<String, ViewGroup> mapMainStatusToView;
    Map<ViewGroup, List<CircleImageView>> mapLayoutToImages;
    private BroadcastReceiver mUserStatusReceiver;
    private Boolean bShowSubMembers;
    private int MAX_MEMBERS_IN_STATUS = 5;

    private static HashMap<Integer, Integer> mapNumberToImage;
    static
    {
        mapNumberToImage = new HashMap<>();
        mapNumberToImage.put(6, R.drawable.six);
        mapNumberToImage.put(7, R.drawable.seven);
        mapNumberToImage.put(8, R.drawable.eight);
        mapNumberToImage.put(9, R.drawable.nine);
        mapNumberToImage.put(10, R.drawable.ten);
        mapNumberToImage.put(11, R.drawable.eleven);
        mapNumberToImage.put(12, R.drawable.twelve);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            this.shownGroup = (Group) bundle.getSerializable(getString(R.string.group));
            this.loginUser = (User) bundle.getSerializable(getString(R.string.login_user));
            this.bShowSubMembers = bundle.getBoolean(getString(R.string.is_show_sub_members));
        }

        getActivity().setTitle(R.string.main_fragment_title);

        lstMembers = new ArrayList<>();
        mapMainStatusToSub = new HashMap<>();
        mapLayoutToImages = new HashMap<>();
        mapMainStatusToView = new HashMap<>();

        lstMain = new ArrayList<>();

        progress = new ProgressDialog(getActivity());
        progress.setTitle(getString(R.string.loading_title));
        progress.setMessage(getString(R.string.loading_message));
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        vFragmentLayout = inflater.inflate(R.layout.activity_main, container, false);

        getActivity().setTitle(R.string.main_fragment_title);

        rootLinearLayout = (LinearLayout) vFragmentLayout.findViewById(R.id.root);

        List<StatusesInGroup> statusesInGroup =
                new Select().from(StatusesInGroup.class).where(StatusesInGroup.STATUSES_ID_PROPERTY + " = " + shownGroup.getStatusesId()).execute();

        for (StatusesInGroup mainStatus : statusesInGroup) {
            mapMainStatusToSub.put(mainStatus.getName(), mainStatus.getSubStatuses() != null ? mainStatus.getSubStatuses() : new ArrayList<String>());
            lstMain.add(mainStatus.getName());
        }

        calculateImageSizeByStatuses(statusesInGroup);

        // TODO: buildLayout should be called once. - not any creation
        buildLayout();
        pullMembers(shownGroup);

        return vFragmentLayout;
    }

    private void calculateImageSizeByStatuses(List<StatusesInGroup> statusesInGroup) {
        // TODO: find a function of image sizes - maybe all images same size or depends on the layout.
        ImageSizeOnDrop = 350 - (statusesInGroup.size() / STATUSES_IN_ROW_AMOUNT) * 50;
    }

    private void buildLayout() {

        int colsSize = STATUSES_IN_ROW_AMOUNT;
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
                mapLayoutToImages.put(newCol, new ArrayList<CircleImageView>());

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

    // todo: should be called on broadcast
    private void pullMembers(Group g) {
        handleUsersOfGroup(g, g.getUsers(), vFragmentLayout, progress);

        if (bShowSubMembers) {
            List<Group> subGroups =  new Select().from(Group.class).where(Group.PARENT_ID_PROPERTY + " = " + g.getId()).execute();
            for (Group currSubGroup : subGroups) {
                pullMembers(currSubGroup);
            }
        }
    }

    private void handleUsersOfGroup(Group groupOfUsers, final List<Long> usersID, final View vFragmentLayout, final ProgressDialog progress) {

        List<User> groupUsers = new Select().from(User.class).where("id " + SQLHelper.getInQuery(usersID)).execute();

        List<UserInGroup> usersInGroups = new Select().from(UserInGroup.class).where(UserInGroup.GROUP_PROPERTY + " = " + groupOfUsers.getId()).execute();

        HashMap<Long, UserInGroup> mapUserIdToStatus = new HashMap<>();

        // Set statuses in hashmap
        for (UserInGroup userInGroup : usersInGroups) {
            if (!mapUserIdToStatus.containsKey(userInGroup.getUserId())) {
                mapUserIdToStatus.put(userInGroup.getUserId(), userInGroup);
            }
        }

        for (User currUser : groupUsers) {
            UserInGroup userInGroup = mapUserIdToStatus.get(currUser.getId());

            if (userInGroup != null) {
                // When we got the status of the current user
                currUser.setMainStatus(userInGroup.getMainStatus());
                currUser.setSubStatus(userInGroup.getSubStatus());
                currUser.setLastUpdateDate(userInGroup.getLastUpdateDate());
            }
            currUser.setGroupId(groupOfUsers.getId());
            lstMembers.add(currUser);
        }

        setMembersOnStatuses(vFragmentLayout, groupUsers);

        progress.dismiss();
    }

    private void setMembersOnStatuses(View vFragmentLayout, List<User> users) {

        FlowLayout btm = (FlowLayout) vFragmentLayout.findViewById(R.id.defaultStatus);

        for (int i = 0; i < users.size(); i++) {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ImageSizeOnDrop, ImageSizeOnDrop);

            CircleImageView memberImage = new CircleImageView(getActivity());

            User currGroupMember = users.get(i);

            memberImage.setLayoutParams(layoutParams);
            Picasso.with(getActivity()).load(currGroupMember.getImage()).into(memberImage);

            String memberMainStatus = currGroupMember.getMainStatus();
            // If there is no status,
            // or the status is irrelevant
            // or main status in the DB isnt valid
            // Then give default status
            if ((currGroupMember.getLastUpdateDate() == null) ||
                    (!DateUtils.isToday(currGroupMember.getLastUpdateDate().getTime())) ||
                    (memberMainStatus.equals("")) ||
                    (!mapMainStatusToView.containsKey(memberMainStatus))) {
                currGroupMember.setMainStatus((String) btm.getTag(R.string.main_status));
                currGroupMember.setSubStatus("");
                currGroupMember.updateUserStatuses();

                addImageToView(btm, memberImage);
            }
            // If there is already main status on DB, and it's update date from today
            else {
                addImageToView((FlowLayout) mapMainStatusToView.get(memberMainStatus), memberImage);
            }

            memberImage.setTag(R.string.user, currGroupMember);

            // the member's image will be able to be dragged only if the logged on user is manager or
            // he is the specific member itself
            if ((shownGroup.getIsManager()) ||
                (currGroupMember.getId().equals(loginUser.getId()))) {
                memberImage.setOnTouchListener(new MyTouchListener());
            }
            else {
                // Even if it's isnt dragable we want to show the sub status when click
                memberImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showPopupSubStatus(view, false);
                    }
                });
            }
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
            CircleImageView imgMember = (CircleImageView) event.getLocalState();

            if (imgMember != null) {
                ViewGroup oldLayout = (ViewGroup) imgMember.getParent();

                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        // do nothing
                        break;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        break;
                    case DragEvent.ACTION_DROP:

                        FlowLayout newLayout = (FlowLayout) v;
                        imgMember.setVisibility(View.VISIBLE);

                        if (oldLayout == newLayout) {
                            showPopupSubStatus(imgMember);
                        }
                        // New mainStatus, Clear the sub status
                        else {
                            removeImageFromView(oldLayout, imgMember);
                            addImageToView(newLayout, imgMember);

                            User sld = ((User) imgMember.getTag(R.string.user));
                            // Set main status
                            sld.setMainStatus((String) newLayout.getTag(R.string.main_status));

                            // Clear sub status
                            sld.setSubStatus(null);
                            sld.updateUserStatuses();
                        }
                        break;
                    case DragEvent.ACTION_DRAG_ENDED:

                    default:
                        break;
                }
            }
            return true;
        }

        private void arrangeImagesInLayout(ViewGroup layout) {
            final int childCount = layout.getChildCount();

            // If there are any images in this layout
            if (childCount > 1) {
                int beforeSqrt = (layout.getHeight() * layout.getWidth()) / (childCount - 1);
                int ImageSize = (int) Math.sqrt(beforeSqrt);


                for (int i = 0; i < childCount; i++) {
                    View v = layout.getChildAt(i);
                    if (v instanceof CircleImageView) {
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ImageSize, ImageSize);

                        v.setLayoutParams(layoutParams);
                        v.invalidate();
                    }
                }
            }
        }
    }

    private void removeImageFromView(ViewGroup oldLayout, CircleImageView memberImage) {
        oldLayout.removeView(memberImage);
        mapLayoutToImages.get(oldLayout).remove(memberImage);

        // If we now reach the number we can show
        if (mapLayoutToImages.get(oldLayout).size() == MAX_MEMBERS_IN_STATUS) {

            // Make all images visible
            for (CircleImageView circleImageView : mapLayoutToImages.get(oldLayout)) {
                circleImageView.setVisibility(View.INVISIBLE);
            }

            for (int i = 0; i < oldLayout.getChildCount(); i++) {
                if ((oldLayout.getChildAt(i).getTag() != null) &&
                        (oldLayout.getChildAt(i).getTag().equals("cluster"))) {
                    CircleImageView cluster = (CircleImageView) oldLayout.getChildAt(i);
                    oldLayout.removeView(cluster);
                    break;
                }
            }
        }
    }

    private void addImageToView(FlowLayout newLayout, CircleImageView memberImage) {
        newLayout.addView(memberImage);
        mapLayoutToImages.get(newLayout).add(memberImage);

        // If we now have too much images for (before the addition it was ok)
        if (mapLayoutToImages.get(newLayout).size() - 1 == MAX_MEMBERS_IN_STATUS) {

            // Make all images not visible
            for (CircleImageView circleImageView : mapLayoutToImages.get(newLayout)) {
                circleImageView.setVisibility(View.GONE);
            }

            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(CLUSTER_SIZE, CLUSTER_SIZE);
            CircleImageView cluster = new CircleImageView(getActivity());
            cluster.setLayoutParams(layoutParams);
            cluster.setImageResource(mapNumberToImage.get(mapLayoutToImages.get(newLayout).size()));
            cluster.setTag("cluster");

            newLayout.addView(cluster);
        }
        //  If we have too much but there is already cluster
        else if (mapLayoutToImages.get(newLayout).size() - 1 > MAX_MEMBERS_IN_STATUS) {
            memberImage.setVisibility(View.GONE);
            CircleImageView cluster = null;
            for (int i = 0; i < newLayout.getChildCount(); i++) {
                if ((newLayout.getChildAt(i).getTag() != null) &&
                    (newLayout.getChildAt(i).getTag().equals("cluster"))) {
                    cluster = (CircleImageView) newLayout.getChildAt(i);
                    break;
                }
            }

            if (cluster != null) {
                cluster.setImageResource(mapNumberToImage.get(mapLayoutToImages.get(newLayout).size()));
            }
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
    public void showPopupSubStatus(final View imgMember) {
        showPopupSubStatus(imgMember, true);
    }

    public void showPopupSubStatus(final View imgMember, final boolean bEnabled) {

        User user = null;
        // If there is already selected sub status - select it
        if (imgMember.getTag(R.string.user) != null) {
            user = ((User) imgMember.getTag(R.string.user));
        }

        if (user != null) {
            List<String> subStatuses = mapMainStatusToSub.get(user.getMainStatus());
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
                TextView txtMemberName = (TextView) popupView.findViewById(R.id.txt_member_name);

                popupSpinner.setEnabled(bEnabled);
                popupSpinner.setClickable(bEnabled);

                txtMemberName.setText(user.getName());

//                ImageView imgSmallImageMember = (ImageView) popupView.findViewById(R.id.img_small_member);
//                imgSmallImageMember.setImageDrawable(((ImageView)imgMember).getDrawable());

                ArrayAdapter<String> adapter =
                        new ArrayAdapter<String>(getActivity(),
                                android.R.layout.simple_spinner_item, subStatuses);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                popupSpinner.setAdapter(adapter);

                final Integer[] nTimesSelected = {0};

                final User finalMember = user;
                popupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        finalMember.setSubStatus(popupSpinner.getSelectedItem().toString());
                        finalMember.updateUserStatuses();

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
                if ((user.getSubStatus() != null) && (!user.getSubStatus().equals(""))) {

                    String selectedOptionValue = user.getSubStatus();
                    popupSpinner.setSelection(((ArrayAdapter<String>) popupSpinner.getAdapter()).getPosition(selectedOptionValue));
                }

                popupWindow.setFocusable(true);
                popupWindow.showAsDropDown(imgMember, 0, 0);
            }
        }
    }
}

