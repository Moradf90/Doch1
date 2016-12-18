package t.a.m.com.doch1;

import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikepenz.fastadapter.commons.utils.RecyclerViewCacheUtil;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.itemanimators.AlphaCrossFadeAnimator;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.AbstractBadgeableDrawerItem;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.ExpandableDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.octicons_typeface_library.Octicons;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import t.a.m.com.doch1.Models.Group;
import t.a.m.com.doch1.Models.User;
import t.a.m.com.doch1.Models.UserInGroup;
import t.a.m.com.doch1.management.ManagementFragment;

public class DrawerActivity extends AppCompatActivity {
    private static final int PROFILE_SETTING = 100000;

    //save our header or result
    private AccountHeader headerResult = null;
    private Drawer result = null;
    public static FirebaseUser mCurrentUser;
    List<IDrawerItem>  lstSoldiersToExpand;
    ExpandableDrawerItem SoldiersDrawerItem;
    ExpandableDrawerItem allGroupsDrawerItem;

    // TODO: get from current user
    public static User currUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sample_dark_toolbar);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        //Remove line to test RTL support
        //getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        // Handle Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create a few sample profile
        // NOTE you have to define the loader logic too. See the CustomApplication for more details
//        final IProfile profile = new ProfileDrawerItem().withName(mCurrentUser.getDisplayName()).withEmail(mCurrentUser.getEmail()).withIcon(R.drawable.snowflake36).withIdentifier(100);


//         = initMyGroupsDrawer(groups);
//        initProfileInDrawer(mCurrentUser);

        // Create the AccountHeader
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withTranslucentStatusBar(true)
                .withHeaderBackground(R.drawable.header)
                .withOnAccountHeaderListener(
                        new AccountHeader.OnAccountHeaderListener() {
                            @Override
                            // TODO: why yaalom cant be chosen
                            public boolean onProfileChanged(View view, IProfile profile, boolean current) {
                                String selectedProfileGroupID = ((ProfileDrawerItem) profile).getTag().toString();

                                initSoldiersDrawer(selectedProfileGroupID);
                                refreshCurrFragment(selectedProfileGroupID);

                                //false if you have not consumed the event and it should close the drawer
                                return false;
                            }

                            private void refreshCurrFragment(String selectedProfileGroupID) {
                                Fragment fCurrentDisplayedFragment = getFragmentManager().findFragmentById(R.id.frame_container);
                                if (fCurrentDisplayedFragment.getClass().getSimpleName().equals("MainFragment")) {

                                    Fragment newFragment = new MainFragment(selectedProfileGroupID);

                                    FragmentManager fragmentManager = getFragmentManager();
                                    fragmentManager.beginTransaction()
                                            .replace(R.id.frame_container, newFragment, newFragment.getClass().getSimpleName())
                                            .commit();
                                }
                            }
                        }
                )
                .withSavedInstance(savedInstanceState)
                .build();

        getCurrentUser();

        PrimaryDrawerItem MyProfileDrawerItem = new PrimaryDrawerItem().withName(R.string.profile_fragment).withIcon(GoogleMaterial.Icon.gmd_account).withIdentifier(1);
        PrimaryDrawerItem ManagementGroupsDrawerItem = new PrimaryDrawerItem().withName(R.string.managment_fragment).withIcon(GoogleMaterial.Icon.gmd_accounts_list_alt).withIdentifier(3);

        PrimaryDrawerItem FillStatusesDrawerItem = new PrimaryDrawerItem().withName(R.string.main_fragment).withDescription(R.string.dsc_main_statuses).withIcon(FontAwesome.Icon.faw_wheelchair).withIdentifier(2).withSelectable(false);

        SoldiersDrawerItem = new ExpandableDrawerItem().withName(R.string.my_members).withIcon(GoogleMaterial.Icon.gmd_accounts_list).withIdentifier(19);

        final PrimaryDrawerItem SendDrawerItem = new PrimaryDrawerItem().withName(R.string.send_statuses).withEnabled(true).withIcon(Octicons.Icon.oct_radio_tower).withIdentifier(9);

        ExpandableDrawerItem contactDrawerItem = new ExpandableDrawerItem().withName("Contact developer").withIcon(GoogleMaterial.Icon.gmd_code).withIdentifier(25).withSelectable(false).withSubItems(
                new SecondaryDrawerItem().withName("By Phone").withLevel(2).withIcon(GoogleMaterial.Icon.gmd_phone).withIdentifier(2501),
                new SecondaryDrawerItem().withName("By SMS").withLevel(2).withIcon(GoogleMaterial.Icon.gmd_tumblr).withIdentifier(2502),
                new SecondaryDrawerItem().withName("By Email").withLevel(2).withIcon(GoogleMaterial.Icon.gmd_email).withIdentifier(2503));


        //Create the drawer
        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withHasStableIds(true)
                .withItemAnimator(new AlphaCrossFadeAnimator())
                .withAccountHeader(headerResult) //set the AccountHeader we created earlier for the header
                .addDrawerItems(
                        MyProfileDrawerItem,
                        ManagementGroupsDrawerItem,
                        new SectionDrawerItem().withName(R.string.drawer_item_section_header),
                        FillStatusesDrawerItem,
                        SoldiersDrawerItem,
                        SendDrawerItem,
                        new DividerDrawerItem(),

                        contactDrawerItem
                        //                        new SwitchDrawerItem().withName("Switch").withIcon(Octicons.Icon.oct_tools).withChecked(true).withOnCheckedChangeListener(onCheckedChangeListener),
//                        new SwitchDrawerItem().withName("Switch2").withIcon(Octicons.Icon.oct_tools).withChecked(true).withOnCheckedChangeListener(onCheckedChangeListener).withSelectable(false),
//                        new ToggleDrawerItem().withName("Toggle").withIcon(Octicons.Icon.oct_tools).withChecked(true).withOnCheckedChangeListener(onCheckedChangeListener),
//                        new DividerDrawerItem(),
//                        new SecondarySwitchDrawerItem().withName("Secondary switch").withIcon(Octicons.Icon.oct_tools).withChecked(true).withOnCheckedChangeListener(onCheckedChangeListener),
//                        new SecondarySwitchDrawerItem().withName("Secondary Switch2").withIcon(Octicons.Icon.oct_tools).withChecked(true).withOnCheckedChangeListener(onCheckedChangeListener).withSelectable(false),
                ) // add the items we want to use with our Drawer
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        //check if the drawerItem is set.
                        //there are different reasons for the drawerItem to be null
                        //--> click on the header
                        //--> click on the footer
                        //those items don't contain a drawerItem


                        if (drawerItem != null) {
                            selectItem((int) drawerItem.getIdentifier());
                        }

                        if (drawerItem.getIdentifier() == 9) {
                            SendDrawerItem.withName("Sent...").withDescription("You can update your doch1").withIcon(GoogleMaterial.Icon.gmd_airplane);
                            result.updateItem(SendDrawerItem);
                        }

                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .withShowDrawerOnFirstLaunch(true)
                .build();

        //if you have many different types of DrawerItems you can magically pre-cache those items to get a better scroll performance
        //make sure to init the cache after the DrawerBuilder was created as this will first clear the cache to make sure no old elements are in
        //RecyclerViewCacheUtil.getInstance().withCacheSize(2).init(result);
        new RecyclerViewCacheUtil<IDrawerItem>().withCacheSize(2).apply(result.getRecyclerView(), result.getDrawerItems());

        //only set the active selection or active profile if we do not recreate the activity
        if (savedInstanceState == null) {
            // set the selection to the item with the identifier 2 - main fragment
            result.setSelection(1, false);
            selectItem(1);

            //set the active profile
//            headerResult.setActiveProfile(profile);
        }

//        result.updateBadge(4, new StringHolder(10 + ""));

    }

    private List<IProfile> initMyGroupsDrawer(List<Group> groups) {
        return null;
    }

//    private void SetMyGroupID() {
//        FirebaseDatabase.getInstance().getReference(User.USERS_REFERENCE_KEY)
//                // TODO: not by mail
//                .orderByChild(User.EMAIL_PROPERTY)
//                .equalTo(mCurrentUser.getEmail())
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        currUserGroupID = dataSnapshot.getValue(User.class).getGroupId();
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
//    }

    private void initUnderMyCommandGroups(final ExpandableDrawerItem allMygroupsDrawerItem, String... groupsId) {

        // Build my groups
        final List<IDrawerItem> lstMyGroupsDrawerItems = new ArrayList<IDrawerItem>();

        for (String currGroupId : groupsId) {
            FirebaseDatabase.getInstance().getReference(Group.GROUPS_REFERENCE_KEY)
                    .orderByChild(Group.ID_PROPERTY)
                    .equalTo(currGroupId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {

                                // Get my groups - which im in.
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    handleGroupRecursive(lstMyGroupsDrawerItems, ds);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }
        allMygroupsDrawerItem.withSubItems(lstMyGroupsDrawerItems);
//        synchronized(allMygroupsDrawerItem){
//            allMygroupsDrawerItem.notify();
//        }
    }



    private void addAllSubUnitsToProfiles(String groupID, final IProfile parentProfile, final ExpandableDrawerItem parentGroup) {
        FirebaseDatabase.getInstance().getReference(Group.GROUPS_REFERENCE_KEY)
                .orderByChild(Group.PARENT_ID_PROPERTY)
                .equalTo(groupID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {

                            List<IDrawerItem> lstSubGroupsDrawerItems = new ArrayList<IDrawerItem>();

                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                handleGroupRecursive(lstSubGroupsDrawerItems, ds);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void handleGroupRecursive(List<IDrawerItem> lstSubGroupsDrawerItems, DataSnapshot ds) {
        Group g = ds.getValue(Group.class);

        ExpandableDrawerItem currGroupDrawerItem = new ExpandableDrawerItem().withName(g.getName());

        IProfile newProfile =
                new ProfileDrawerItem().withName(g.getName()).withIdentifier(10041).withTag(g.getId());

        // TODO: update the images in drawer
        drawableFromUrl(g.getImage(), newProfile, currGroupDrawerItem);

        addAllSubUnitsToProfiles(g.getId(), newProfile, currGroupDrawerItem);

        lstSubGroupsDrawerItems.add(currGroupDrawerItem);
    }

    private void initSoldiersDrawer(final String groupID) {

        FirebaseDatabase.getInstance().getReference(Group.GROUPS_REFERENCE_KEY).child(groupID)
//                .equalTo(groupID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        lstSoldiersToExpand = new ArrayList<>();

                        if (dataSnapshot.exists()) {
                            final Group myGroup = dataSnapshot.getValue(Group.class);

                            for (final String userId : myGroup.getUsers()) {
                                FirebaseDatabase.getInstance().getReference(User.USERS_REFERENCE_KEY).child(userId)
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {

                                                if (dataSnapshot.exists()) {
                                                    final User currUser = dataSnapshot.getValue(User.class);
                                                    final SecondaryDrawerItem currSoldierDrawer = new SecondaryDrawerItem().withName(currUser.getName()).withLevel(2)
                                                            .withIdentifier(Long.parseLong(currUser.getPersonalId()))
                                                            .withSelectable(false);

                                                    drawableFromUrl(currUser.getImage(), currSoldierDrawer);

                                                    // Get current status of current user
                                                    FirebaseDatabase.getInstance().getReference(UserInGroup.USERS_IN_GROUP_REFERENCE_KEY)
                                                            .child(groupID).child(currUser.getId()).addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                                            if (dataSnapshot.exists()) {
                                                                UserInGroup userInGroup = dataSnapshot.getValue(UserInGroup.class);

                                                                // If there is main status
                                                                if (userInGroup.getMainStatus() != "") {
                                                                    currSoldierDrawer.withDescription(getDescription(userInGroup)).withTextColor(Color.rgb(20, 170, 20));
                                                                } else {
                                                                    currSoldierDrawer.withDescription(R.string.no_status).withTextColor(Color.rgb(170, 20, 20));
                                                                }
                                                            }

                                                            // TODO: doesnt work.
                                                            addDrawerToList(lstSoldiersToExpand, currSoldierDrawer);
                                                        }

                                                        private void addDrawerToList(List<IDrawerItem> lstSoldiersToExpand, SecondaryDrawerItem currSoldierDrawer) {
                                                            int indexToRemove = -1;
                                                            for (int i =0; i < lstSoldiersToExpand.size(); i++) {
                                                                if (((SecondaryDrawerItem)lstSoldiersToExpand.get(i)).getName().equals(currSoldierDrawer.getName())) {
                                                                    indexToRemove = i;
                                                                    break;
                                                                }
                                                            }

                                                            if (indexToRemove >= 0 ) {
                                                                lstSoldiersToExpand.remove(indexToRemove);
                                                            }
                                                            lstSoldiersToExpand.add(currSoldierDrawer);

//                                                            // TODO: should fix problem
//                                                            synchronized(SoldiersDrawerItem){
//                                                                SoldiersDrawerItem.notifyAll();
//                                                            }
                                                        }

                                                        @NonNull
                                                        private String getDescription(UserInGroup currUser) {
                                                            if (!currUser.getSubStatus().equals("")) {
                                                                return currUser.getMainStatus() + ", " + currUser.getSubStatus();
                                                            } else {
                                                                return currUser.getMainStatus();
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });
                                                }
//                                                PrimaryDrawerItem MyProfileDrawerItem = new PrimaryDrawerItem().withName(R.string.profile_fragment).withIcon(GoogleMaterial.Icon.gmd_account).withIdentifier(1);
                                            }


                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                            }

                            SoldiersDrawerItem.withSubItems(lstSoldiersToExpand);
                            // TODO: should fix problem
                            synchronized(SoldiersDrawerItem){
                                SoldiersDrawerItem.notifyAll();
                            }
                            result.updateItem(SoldiersDrawerItem);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public void drawableFromUrl(String url, final AbstractBadgeableDrawerItem item) {

            AsyncTask<String, Void, Bitmap> task = new AsyncTask<String, Void, Bitmap>(){
                @Override
                protected Bitmap doInBackground(String... params) {
                    Bitmap x = null;
                    try {
                        HttpURLConnection connection = (HttpURLConnection) new URL(params[0]).openConnection();
                        connection.connect();
                        InputStream input = connection.getInputStream();

                        x = BitmapFactory.decodeStream(input);
                    }
                    catch (Exception ex){}

                    return x;
                }

                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    if(bitmap != null){
                        item.withIcon(new BitmapDrawable(bitmap));
                    }
                    else {
                        item.withIcon(DrawerActivity.this.getResources().getDrawable(R.drawable.face_icon));
                    }
                }
            };
        
        task.execute(url);
    }

    public void drawableFromUrl(String url, final ExpandableDrawerItem item) {

        AsyncTask<String, Void, Bitmap> task = new AsyncTask<String, Void, Bitmap>(){
            @Override
            protected Bitmap doInBackground(String... params) {
                Bitmap x = null;
                try {
                    HttpURLConnection connection = (HttpURLConnection) new URL(params[0]).openConnection();
                    connection.connect();
                    InputStream input = connection.getInputStream();

                    x = BitmapFactory.decodeStream(input);
                }
                catch (Exception ex){}

                return x;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if(bitmap != null){
                    item.withIcon(new BitmapDrawable(bitmap));
                }
                else {
                    item.withIcon(DrawerActivity.this.getResources().getDrawable(R.drawable.face_icon));
                }
            }
        };

        task.execute(url);
    }

    private void drawableFromUrl(String url, final IProfile newProfile, final ExpandableDrawerItem currGroupDrawerItem) {
        AsyncTask<String, Void, Bitmap> task = new AsyncTask<String, Void, Bitmap>(){
            @Override
            protected Bitmap doInBackground(String... params) {
                Bitmap x = null;
                try {
                    HttpURLConnection connection = (HttpURLConnection) new URL(params[0]).openConnection();
                    connection.connect();
                    InputStream input = connection.getInputStream();

                    x = BitmapFactory.decodeStream(input);
                }
                catch (Exception ex){}

                return x;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if(bitmap != null){
                    newProfile.withIcon(new BitmapDrawable(bitmap));
                    currGroupDrawerItem.withIcon(new BitmapDrawable(bitmap));
                }
                else {
                    newProfile.withIcon(DrawerActivity.this.getResources().getDrawable(R.drawable.face_icon));
                    currGroupDrawerItem.withIcon(DrawerActivity.this.getResources().getDrawable(R.drawable.face_icon));
                }

                headerResult.addProfiles(newProfile);
            }
        };

        task.execute(url);
    }

    // TODO: prevent double code
    public void drawableFromUrl(String url, final IProfile item) {

        AsyncTask<String, Void, Bitmap> task = new AsyncTask<String, Void, Bitmap>(){
            @Override
            protected Bitmap doInBackground(String... params) {
                Bitmap x = null;
                try {
                    HttpURLConnection connection = (HttpURLConnection) new URL(params[0]).openConnection();
                    connection.connect();
                    InputStream input = connection.getInputStream();

                    x = BitmapFactory.decodeStream(input);
                }
                catch (Exception ex){}

                return x;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if(bitmap != null){
                    item.withIcon(new BitmapDrawable(bitmap));
                }
                else {
                    item.withIcon(DrawerActivity.this.getResources().getDrawable(R.drawable.face_icon));
                }

                headerResult.addProfiles(item);
            }
        };

        task.execute(url);
    }

    /** Swaps fragments in the main content view */
    private void selectItem(int identifier) {

        Fragment newFragment;

        // Get the current displayed fragment
        Fragment fCurrentDisplayedFragment = getFragmentManager().findFragmentById(R.id.frame_container);

        // Create a new fragment and specify the planet to show based on position
        if (identifier == 1) {
            newFragment = new ProfileFragment();
        }
        else if (identifier == 2) {
            if ((ProfileDrawerItem) headerResult.getActiveProfile() == null) {
                Toast.makeText(DrawerActivity.this, R.string.select_group_message, Toast.LENGTH_SHORT).show();
                newFragment = fCurrentDisplayedFragment;
            }
            else{
                String selectedProfileGroupID = ((ProfileDrawerItem) headerResult.getActiveProfile()).getTag().toString();
                newFragment = new MainFragment(selectedProfileGroupID);
            }
        }
        else if (identifier == 3) {
            newFragment = new ManagementFragment();
        }
        else if (identifier == 9) {
            Toast.makeText(DrawerActivity.this, "Send...", Toast.LENGTH_SHORT).show();
            newFragment = fCurrentDisplayedFragment;
        }
        else if (identifier == 19) {
            // No change
            newFragment = fCurrentDisplayedFragment;
        }
        else if (identifier == 21) {
            Toast.makeText(DrawerActivity.this, "Call Morad", Toast.LENGTH_SHORT).show();
            newFragment = fCurrentDisplayedFragment;
        }
        // Call morad
        else if (identifier == 2501) {
            Toast.makeText(DrawerActivity.this, "Call Morad", Toast.LENGTH_SHORT).show();
            newFragment = fCurrentDisplayedFragment;
        }
        // sms Morad
        else if (identifier == 2502) {
            Toast.makeText(DrawerActivity.this, "sms Morad", Toast.LENGTH_SHORT).show();

            newFragment = fCurrentDisplayedFragment;
        }
        // email morad
        else if (identifier == 2503) {
            Toast.makeText(DrawerActivity.this, "mail Morad", Toast.LENGTH_SHORT).show();
            newFragment = fCurrentDisplayedFragment;
        }
        else {
            newFragment = fCurrentDisplayedFragment;
        }
//        Bundle args = new Bundle();
//        args.putInt(MainFragment.ARG_PLANET_NUMBER, position);
//        fragment.setArguments(args);



        // If the current displayed is the same as the one we want to switch to - do nothing. else - switch.
        if ((fCurrentDisplayedFragment == null) ||
            (!fCurrentDisplayedFragment.getClass().getSimpleName().equals(newFragment.getClass().getSimpleName()))) {

            // Insert the fragment by replacing any existing fragment
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, newFragment, newFragment.getClass().getSimpleName())
                    .commit();

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the drawer to the bundle
        outState = result.saveInstanceState(outState);
        //add the values which need to be saved from the accountHeader to the bundle
        outState = headerResult.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        //handle the back press :D close the drawer first and if the drawer is closed close the activity
        if (result != null && result.isDrawerOpen()) {
            result.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }


    public static void setCurrUser(User user) {
        currUser = user;
    }

    public void getCurrentUser() {
        if(mCurrentUser != null) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();

            database.getReference(User.USERS_REFERENCE_KEY)
                    .orderByChild(User.EMAIL_PROPERTY)
                    .equalTo(mCurrentUser.getEmail())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                for (DataSnapshot user : dataSnapshot.getChildren()) {
                                    currUser = user.getValue(User.class);
                                    break;
                                }

                                // Update relevant drawers for current user
                                updateDrawers();
                            }
                        }

                        private void updateDrawers() {
                            if (currUser.getGroups() != null && currUser.getGroups().size() > 0) {
                                allGroupsDrawerItem = new ExpandableDrawerItem().withName(R.string.my_groups).withIcon(GoogleMaterial.Icon.gmd_group).withIdentifier(20);

                                // TODO: why not working
                                String[] groupsId = Arrays.copyOf(currUser.getGroups().toArray(), currUser.getGroups().size(), String[].class);
                                initUnderMyCommandGroups(allGroupsDrawerItem, groupsId);
//                            initUnderMyCommandGroups(groupsDrawerItem, currUser.getGroupId());

                                result.addItem(allGroupsDrawerItem);
//                            initUnderMyCommandGroups(currUser.getGroupId(), groups, groupsDrawerItem);

                                initSoldiersDrawer(currUser.getGroups().get(0));
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }
    }
}
