package t.a.m.com.doch1;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.format.DateUtils;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

import t.a.m.com.doch1.DragNDrop.MyDragShadowBuilder;
import t.a.m.com.doch1.Models.Group;
import t.a.m.com.doch1.Models.StatusesInGroup;
import t.a.m.com.doch1.Models.User;
import t.a.m.com.doch1.Models.UserInGroup;
import t.a.m.com.doch1.common.SQLHelper;
import t.a.m.com.doch1.common.Utils;
import t.a.m.com.doch1.common.VoiceRecognitionTest;
import t.a.m.com.doch1.common.utils.MyCallbackInterface;
import t.a.m.com.doch1.views.CircleImageView;
import t.a.m.com.doch1.views.MySpinner;


// todo: cant change sub status in cluster
public class MainFragment extends Fragment {

    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 121;

    private static final int STATUSES_IN_ROW_AMOUNT = 3;

    private static MainFragment mInstance;
    public static MainFragment instance() {
        if(mInstance == null){
            mInstance = new MainFragment();
        }
        return mInstance;
    }

    private Group shownGroup;
    private long mStatusesId;
    private int ImageSizeOnDrop = 125;
    private int CLUSTER_SIZE = 200;
    private List<User> lstMembers;
    private Map<String, List<String>> mapMainStatusToSub;
    private List<String> lstMain;

    private LinearLayout rootLinearLayoutStatuses;
    private FlowLayout clusterDialogLayout;

    private View vFragmentLayout;
    private ProgressDialog progress;
    public static User loginUser;
    private Map<String, ViewGroup> mapMainStatusToView;
    private Map<ViewGroup, List<CircleImageView>> mapLayoutToImages;
    private BroadcastReceiver mUserStatusReceiver;
    private Boolean bShowSubMembers;
    private int MAX_MEMBERS_IN_STATUS = 5;

    // todo: fix more than 12...
    private static HashMap<Integer, Integer> mapNumberToImage;

    public static final int MANY_MEMBERS = 99;

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
        mapNumberToImage.put(MANY_MEMBERS, R.drawable.many_people);
    }


    private MainFragment(){
        lstMembers = new ArrayList<>();
        mapMainStatusToSub = new HashMap<>();
        mapLayoutToImages = new HashMap<>();
        mapMainStatusToView = new HashMap<>();
        lstMain = new ArrayList<>();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_fragment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_speech_recognizer) {
            startSpeechRecognition();
        }

        return super.onOptionsItemSelected(item);
    }


    private void startSpeechRecognition() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    android.Manifest.permission.RECORD_AUDIO)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_REQUEST_RECORD_AUDIO);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        else {
            VoiceRecognitionTest.vVoiceRecognitionTest1.handleSpeech(lstMembers, lstMain, new MyCallbackInterface() {
                @Override
                public void onSpeechRecognitionFinished(HashMap<User, String> result) {
                    handleSpeechResult(result);
                    int x = 3;
                }
            });
        }
    }

    private void handleSpeechResult(HashMap<User, String> result) {

        HashMap<CircleImageView, ViewGroup> mapImageToOldLayout = new HashMap<>();
        HashMap<User, CircleImageView> mapUserToImage = new HashMap<>();

        for(Map.Entry<ViewGroup, List<CircleImageView>> entry : mapLayoutToImages.entrySet()) {
            for (CircleImageView img : entry.getValue()) {
                mapUserToImage.put(((User) img.getTag(R.string.user)), img);
                mapImageToOldLayout.put(img, entry.getKey());
            }
        }

        for(Map.Entry<User, String> entry : result.entrySet()) {
            User key = entry.getKey();
            String value = entry.getValue();

            moveUserFromToLayout(mapUserToImage.get(key), mapImageToOldLayout.get(mapUserToImage.get(key)), (FlowLayout) mapMainStatusToView.get(value));
        }
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_RECORD_AUDIO: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    VoiceRecognitionTest.vVoiceRecognitionTest1.handleSpeech(lstMembers, lstMain, new MyCallbackInterface() {
                        @Override
                        public void onSpeechRecognitionFinished(HashMap<User, String> result) {
                            int x = 3;
                        }
                    });

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getActivity().setTitle(R.string.main_fragment_title);
        setHasOptionsMenu(true);

        if (vFragmentLayout == null) {
            vFragmentLayout = inflater.inflate(R.layout.activity_main, container, false);
            rootLinearLayoutStatuses = (LinearLayout) vFragmentLayout.findViewById(R.id.statuses_root);
            clusterDialogLayout = (FlowLayout) vFragmentLayout.findViewById(R.id.dialog_layout);
            clusterDialogLayout.setOnDragListener(new MyDragListener());
        }

        clusterDialogLayout.setVisibility(View.GONE);

        // If we click outside the cluster dialog - close it
        rootLinearLayoutStatuses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeAllImagesOfView(clusterDialogLayout);
                clusterDialogLayout.setVisibility(View.GONE);
            }
        });

        showProgress();

        Bundle bundle = this.getArguments();
        if (bundle != null) {

            loginUser = (User) bundle.getSerializable(getString(R.string.login_user));

            refresh((Group) bundle.getSerializable(getString(R.string.group)),
                    bundle.getBoolean(getString(R.string.is_show_sub_members)));
        }

        return vFragmentLayout;
    }

    private void showProgress() {
        progress = new ProgressDialog(getActivity());
        progress.setTitle(getString(R.string.loading_title));
        progress.setMessage(getString(R.string.loading_message));
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
    }

    private void calculateImageSizeByStatuses(List<StatusesInGroup> statusesInGroup) {
        // TODO: find a function of image sizes - maybe all images same size or depends on the layout.
        ImageSizeOnDrop = 350 - (statusesInGroup.size() / STATUSES_IN_ROW_AMOUNT) * 50;
    }

    private void buildLayout() {

        int colsSize = STATUSES_IN_ROW_AMOUNT;
        int rowsSize = lstMain.size() / colsSize;

        rootLinearLayoutStatuses.setWeightSum(rowsSize);
        rootLinearLayoutStatuses.removeAllViewsInLayout();
        mapLayoutToImages.clear();
        mapMainStatusToView.clear();

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
// Morad try
//                newCol.setOnTouchListener(new View.OnTouchListener() {
//                    @Override
//                    public boolean onTouch(View view, MotionEvent motionEvent) {
////                        if(motionEvent.getAction() == 141) {
//                            FlowLayout l = (FlowLayout) view;
//                            if (l.getChildCount() > 1) {
//                                long downTime = SystemClock.uptimeMillis();
//                                long eventTime = SystemClock.uptimeMillis() + 1000 * 60;
//                                MotionEvent event = MotionEvent.obtain(
//                                        downTime,
//                                        eventTime,
//                                        MotionEvent.ACTION_DOWN, 0, 0, 0);
//
//                                l.getChildAt(1).dispatchTouchEvent(event);
//                            }
////                        }
//                        return false;
//                    }
//                });

                Random rnd = new Random();
                int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                newCol.setBackgroundColor(color);

                newRow.addView(newCol);
            }

            rootLinearLayoutStatuses.addView(newRow);
        }
    }

    // todo: should be called on broadcast
    private void pullMembers(Group group) {
        handleUsersOfGroup(group);

        if (bShowSubMembers) {
            List<Group> subGroups =  new Select().from(Group.class)
                    .where(Group.PARENT_ID_PROPERTY + " = " + group.getId()).execute();
            for (Group currSubGroup : subGroups) {
                pullMembers(currSubGroup);
            }
        }
    }

    private void handleUsersOfGroup(Group group) {

        List<User> groupUsers = new Select().from(User.class)
                .where(User.ID_PROPERTY + SQLHelper.getInQuery(group.getUsers())).execute();

        List<UserInGroup> usersInGroups = new Select().from(UserInGroup.class)
                .where(UserInGroup.GROUP_PROPERTY + " = " + group.getId()).execute();

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

            currUser.setGroupId(group.getId());
            lstMembers.add(currUser);
        }

        setMembersOnStatuses(groupUsers);
    }

    private void setMembersOnStatuses(List<User> users) {

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

    public void refresh(Group group, Boolean showSubMembers) {
        if(shownGroup == null || !Utils.isLongsEquals(group.getId(), shownGroup.getId())
                || bShowSubMembers != showSubMembers) {
            shownGroup = group;
            bShowSubMembers = showSubMembers;
            clearMembersViews();
            initStatusesOfGroup(shownGroup);
            lstMembers.clear();
            pullMembers(shownGroup);
        }

        progress.dismiss();
    }

    private void clearMembersViews() {
        for (ViewGroup view : mapLayoutToImages.keySet()) {
            removeAllImagesOfView(view);

            mapLayoutToImages.get(view).clear();
        }
    }

    private void removeAllImagesOfView(ViewGroup view) {
        List<View> viewsToRemove = new ArrayList<>();
        for(int index = 0; index < view.getChildCount(); index ++){
            View child = view.getChildAt(index);
            if(child instanceof CircleImageView){
                viewsToRemove.add(child);
            }
        }

        for (View v : viewsToRemove) {
            view.removeView(v);
        }
    }

    private void initStatusesOfGroup(Group group) {

        if(mStatusesId != group.getStatusesId().longValue()) {
            mStatusesId = shownGroup.getStatusesId();
            mapMainStatusToSub.clear();
            lstMain.clear();
            List<StatusesInGroup> statusesInGroup =
                    new Select().from(StatusesInGroup.class)
                            .where(StatusesInGroup.STATUSES_ID_PROPERTY + " = " + shownGroup.getStatusesId())
                            .execute();

            for (StatusesInGroup mainStatus : statusesInGroup) {
                mapMainStatusToSub.put(mainStatus.getName(), mainStatus.getSubStatuses() != null ? mainStatus.getSubStatuses() : new ArrayList<String>());
                lstMain.add(mainStatus.getName());
            }

            calculateImageSizeByStatuses(statusesInGroup);
            buildLayout();
        }
    }

    public final class MyTouchListener implements View.OnTouchListener {
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
            View imgMember = (View) event.getLocalState();

            if (imgMember != null) {
                ViewGroup oldLayout = (ViewGroup) imgMember.getParent();

                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        break;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        clusterDialogLayout.setOnDragListener(null);
                        clusterDialogLayout.setVisibility(View.GONE);
                        break;
                    case DragEvent.ACTION_DROP:

                        FlowLayout newLayout = (FlowLayout) v;
                        imgMember.setVisibility(View.VISIBLE);
                        Boolean bIsFromCluster = false;

                        // If we just finished dragging image from cluster to layout
                        if (oldLayout.getTag(R.string.is_from_cluster) != null &&
                                oldLayout.getTag(R.string.is_from_cluster).equals(true)) {

                            bIsFromCluster = true;

                            // If we drag from cluster dialog to itself
                            // todo: make sure this can happen = so we will be able to change sub status in cluster
                            if (newLayout == oldLayout) {
                                // Get the index of the image in the previous (real, clustered) layout
                                int imgMemberIndex = (int) imgMember.getTag(R.string.image_member_index);

                                // Get the actual image in the previous (real, clustered) layout
                                showPopupSubStatus(mapLayoutToImages.get(oldLayout.getTag(R.string.repesented_by_cluster_layout)).get(imgMemberIndex), imgMember);
                                return true;
                            }

                            removeAllImagesOfView(oldLayout);
                            oldLayout.setVisibility(View.GONE);

                            // Treat the old layout as the layout that contains the member
                            oldLayout = (ViewGroup) oldLayout.getTag(R.string.repesented_by_cluster_layout);

                            // Get the index of the image in the previous (real, clustered) layout
                            int imgMemberIndex = (int) imgMember.getTag(R.string.image_member_index);

                            // Get the actual image in the previous (real, clustered) layout
                            imgMember = mapLayoutToImages.get(oldLayout).get(imgMemberIndex);
                            imgMember.setVisibility(View.VISIBLE);
                        }

                        // If we got this from cluster and we return it to the same layout -> so the image
                        // should get back to cluster and not open popup
                        if (oldLayout == newLayout && !bIsFromCluster) {
                            showPopupSubStatus(imgMember);
                        }
                        // New mainStatus, Clear the sub status
                        else {
                            moveUserFromToLayout(imgMember, oldLayout, newLayout);
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

    public void moveUserFromToLayout(View imgMember, ViewGroup oldLayout, FlowLayout newLayout) {
        removeImageFromView(oldLayout, imgMember);
        addImageToView(newLayout, imgMember);

        User usr = ((User) imgMember.getTag(R.string.user));
        // Set main status
        usr.setMainStatus((String) newLayout.getTag(R.string.main_status));

        // Clear sub status
        usr.setSubStatus(null);
        usr.updateUserStatuses();
    }

    private void removeImageFromView(ViewGroup oldLayout, View memberImage) {
        oldLayout.removeView(memberImage);
        mapLayoutToImages.get(oldLayout).remove(memberImage);

        // If we now reach the number we can show
        if (mapLayoutToImages.get(oldLayout).size() == MAX_MEMBERS_IN_STATUS) {

            // Make all images visible
            for (CircleImageView circleImageView : mapLayoutToImages.get(oldLayout)) {
                circleImageView.setVisibility(View.VISIBLE);
            }

            oldLayout.removeView(getClusterByLayout(oldLayout));
        }
        // If we still can't show - decrease the number on the cluster
        else if (mapLayoutToImages.get(oldLayout).size() > MAX_MEMBERS_IN_STATUS) {
            CircleImageView cluster = getClusterByLayout(oldLayout);
            setImageToCluster(cluster, oldLayout);
        }
    }

    private void addImageToView(final FlowLayout newLayout, View memberImage) {
        newLayout.addView(memberImage);
        mapLayoutToImages.get(newLayout).add((CircleImageView) memberImage);

        // If we now have too much images for (before the addition it was ok)
        if (mapLayoutToImages.get(newLayout).size() - 1 == MAX_MEMBERS_IN_STATUS) {

            // Make all images not visible
            for (CircleImageView circleImageView : mapLayoutToImages.get(newLayout)) {
                circleImageView.setVisibility(View.GONE);
            }

            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(CLUSTER_SIZE, CLUSTER_SIZE);
            CircleImageView cluster = new CircleImageView(getActivity());
            cluster.setLayoutParams(layoutParams);
            setImageToCluster(cluster, newLayout);
            cluster.setTag("cluster");

            cluster.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    removeAllImagesOfView(clusterDialogLayout);
                    showClusterDialog(newLayout);
                }
            });

            newLayout.addView(cluster);
        }
        //  If we have too much but there is already cluster
        else if (mapLayoutToImages.get(newLayout).size() - 1 > MAX_MEMBERS_IN_STATUS) {
            memberImage.setVisibility(View.GONE);
            CircleImageView cluster = getClusterByLayout(newLayout);

            if (cluster != null) {
                cluster.setImageResource(mapNumberToImage.get(mapLayoutToImages.get(newLayout).size()));
            }
        }
    }

    private void setImageToCluster(CircleImageView cluster, ViewGroup newLayout) {
        int nCountInCluster = mapLayoutToImages.get(newLayout).size();
        if (mapNumberToImage.containsKey(nCountInCluster)) {
            cluster.setImageResource(mapNumberToImage.get(nCountInCluster));
        } else {
            cluster.setImageResource(mapNumberToImage.get(MANY_MEMBERS));
        }
    }

    // todo: fix with hashmap or something
    private CircleImageView getClusterByLayout(ViewGroup newLayout) {
        for (int i = 0; i < newLayout.getChildCount(); i++) {
            if ((newLayout.getChildAt(i).getTag() != null) &&
                (newLayout.getChildAt(i).getTag().equals("cluster"))) {
                return (CircleImageView) newLayout.getChildAt(i);
            }
        }
        return null;
    }

    public void showClusterDialog(FlowLayout newLayout) {
        List<CircleImageView> circleImageViews = mapLayoutToImages.get(newLayout);

        // custom dialog
        clusterDialogLayout.setVisibility(View.VISIBLE);
        clusterDialogLayout.setOnDragListener(new MyDragListener());

        clusterDialogLayout.setTag(R.string.is_from_cluster, true);
        clusterDialogLayout.setTag(R.string.repesented_by_cluster_layout, newLayout);

        int nImageSize = (480 / (int)(Math.sqrt(circleImageViews.size() / 2) + 1));

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(nImageSize, nImageSize);

        for(int index = 0; index < circleImageViews.size(); index++){
            CircleImageView copy = new CircleImageView(getActivity());
            copy.setImageDrawable(circleImageViews.get(index).getDrawable());
            copy.setTag(R.string.image_member_index, index);
//            copy.setTag(R.string.user, circleImageViews.get(index).getTag(R.string.user));
            copy.setOnTouchListener(new MyTouchListener());
            copy.setLayoutParams(layoutParams);

            clusterDialogLayout.addView(copy);
        }
    }


    public void test(ViewGroup parent, int index){
        FlowLayout l = (FlowLayout)parent;
        if(l.getChildCount() >= index){
            long downTime = SystemClock.uptimeMillis();
            long eventTime = SystemClock.uptimeMillis() + 1000 * 60;
            MotionEvent event = MotionEvent.obtain(
                    downTime,
                    eventTime,
                    MotionEvent.ACTION_DOWN, 0, 0, 0);

            l.getChildAt(index).dispatchTouchEvent(event);
        }
    }


    public void showPopupSubStatus(final View imgMember) {
        showPopupSubStatus(imgMember, true, null);
    }

    public void showPopupSubStatus(final View imgMember, View anchor) {
        showPopupSubStatus(imgMember, true, anchor);
    }

    public void showPopupSubStatus(final View imgMember, final boolean bEnabled) {
        showPopupSubStatus(imgMember, bEnabled, null);
    }

    public void showPopupSubStatus(final View imgMember, final boolean bEnabled, View anchor) {
        if (anchor == null) {
            anchor = imgMember;
        }

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

                if (!bEnabled) {
                    popupSpinner.setVisibility(View.GONE);
                    TextView txtSubStatus = (TextView)popupView.findViewById(R.id.txt_choose_sub_status);

                    // If there is sub status to show (but no to edit - cause boolean is false)
                    if (user.getSubStatus() != null && !user.getSubStatus().equals("")) {
                        txtSubStatus.setText(getString(R.string.sub_status) + " " + user.getSubStatus());
                    }
                    else {
                        txtSubStatus.setText(getString(R.string.no_sub_status));
                    }
                }
                else {
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
                }

                txtMemberName.setText(user.getName());

                popupWindow.setFocusable(true);
                popupWindow.showAsDropDown(anchor, 0, 0);
            }
        }
    }
}