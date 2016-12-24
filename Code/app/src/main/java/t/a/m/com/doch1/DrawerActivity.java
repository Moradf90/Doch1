package t.a.m.com.doch1;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.activeandroid.query.Select;
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
import t.a.m.com.doch1.common.SQLHelper;
import t.a.m.com.doch1.management.ManagementFragment;
import t.a.m.com.doch1.services.tasks.GroupsUpdaterTask;

public class DrawerActivity extends AppCompatActivity {
    private static final int PROFILE_SETTING = 100000;

    //save our header or result
    private AccountHeader headerResult = null;
    private Drawer result = null;
    public static FirebaseUser mCurrentUser;
    List<IDrawerItem>  lstSoldiersToExpand;
    ExpandableDrawerItem SoldiersDrawerItem;
    ExpandableDrawerItem allGroupsDrawerItem;
    private final Long MY_SOLDIERS_IDENTIFIERS = 20l;

    public static User loginUser;

    private BroadcastReceiver mGroupsReceiver;

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter(GroupsUpdaterTask.GROUP_UPDATED_ACTION);
        registerReceiver(mGroupsReceiver, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mGroupsReceiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sample_dark_toolbar);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        initCurrentUser();

        mGroupsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateGroupsInDrawer();
            }
        };

        //Remove line to test RTL support
        //getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        // Handle Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        updateGroupsInDrawer();

    }

    private void updateGroupsInDrawer() {
        if (loginUser != null &&
            loginUser.getGroups() != null &&
            loginUser.getGroups().size() > 0) {
            allGroupsDrawerItem = new ExpandableDrawerItem().withName(R.string.my_groups).withIcon(GoogleMaterial.Icon.gmd_group).withIdentifier(MY_SOLDIERS_IDENTIFIERS);

            // TODO: why not working
            Long[] groupsId = Arrays.copyOf(loginUser.getGroups().toArray(), loginUser.getGroups().size(), Long[].class);
            initUnderMyCommandGroups(allGroupsDrawerItem, groupsId);

            if (result.getDrawerItem((long)MY_SOLDIERS_IDENTIFIERS) != null) {
                result.updateItem(allGroupsDrawerItem);
            }
            else {
                result.addItem(allGroupsDrawerItem);
            }

            initSoldiersDrawer(loginUser.getGroups().get(0).toString());
        }
    }

    private void initUnderMyCommandGroups(final ExpandableDrawerItem allMygroupsDrawerItem, Long... groupsId) {

        // Build my groups
        final List<IDrawerItem> lstMyGroupsDrawerItems = new ArrayList<IDrawerItem>();

        List<Group> MyGroups =  new Select().from(Group.class).where("id " + SQLHelper.getInQuery(groupsId)).execute();

        // Get my groups - which im in.
        for (Group myGroup : MyGroups) {
            handleGroupRecursive(lstMyGroupsDrawerItems, myGroup);
        }

        if (allGroupsDrawerItem != null) {
            allGroupsDrawerItem.withSubItems(lstMyGroupsDrawerItems);
//        synchronized(allMygroupsDrawerItem){
//            allMygroupsDrawerItem.notify();
//        }
        }
    }


    private void addAllSubUnitsToProfiles(String groupID, final IProfile parentProfile, final ExpandableDrawerItem parentGroup) {

        List<Group> subGroups =  new Select().from(Group.class).where(Group.PARENT_ID_PROPERTY + " = " + groupID).execute();

        List<IDrawerItem> lstSubGroupsDrawerItems = new ArrayList<IDrawerItem>();

        for (Group subGroup : subGroups) {

            handleGroupRecursive(lstSubGroupsDrawerItems, subGroup);
        }
    }

    private void handleGroupRecursive(List<IDrawerItem> lstSubGroupsDrawerItems, Group g) {
//        Group g = ds.getValue(Group.class);

        ExpandableDrawerItem currGroupDrawerItem = new ExpandableDrawerItem().withName(g.getName());

        IProfile newProfile =
                new ProfileDrawerItem().withName(g.getName()).withIdentifier(10041).withTag(g.getId());

        // TODO: update the images in drawer
        drawableFromUrl(g.getImage(), newProfile, currGroupDrawerItem);

        addAllSubUnitsToProfiles(g.getId().toString(), newProfile, currGroupDrawerItem);

        lstSubGroupsDrawerItems.add(currGroupDrawerItem);
    }

    private void initSoldiersDrawer(final String groupID) {

        Group group = new Select().from(Group.class).where("id = " + groupID).executeSingle();

        List<User> groupUsers = new Select().from(User.class).where("id " + SQLHelper.getInQuery(group.getUsers())).execute();

        List<UserInGroup> usersInGroups = new Select().from(UserInGroup.class).where(UserInGroup.GROUP_PROPERTY + " = " + groupID).execute();

        lstSoldiersToExpand = new ArrayList<>();

        for (User user : groupUsers) {
            final SecondaryDrawerItem currSoldierDrawer = new SecondaryDrawerItem().withName(user.getName()).withLevel(2)
                    .withIdentifier(Long.parseLong(user.getPersonalId()))
                    .withSelectable(false);

            drawableFromUrl(user.getImage(), currSoldierDrawer);

            for (UserInGroup userInGroup : usersInGroups) {
                if (userInGroup.getUserId().equals(user.getId())) {
                    // If there is main status
                    if (userInGroup.getMainStatus() != "") {
                        currSoldierDrawer.withDescription(getDescription(userInGroup)).withTextColor(Color.rgb(20, 170, 20));
                    } else {
                        currSoldierDrawer.withDescription(R.string.no_status).withTextColor(Color.rgb(170, 20, 20));
                    }
                    break;
                }

                addDrawerToList(lstSoldiersToExpand, currSoldierDrawer);
            }
        }

        SoldiersDrawerItem.withSubItems(lstSoldiersToExpand);
        // TODO: should fix problem
//        synchronized(SoldiersDrawerItem){
//            SoldiersDrawerItem.notifyAll();
//        }
        result.updateItem(SoldiersDrawerItem);
    }

    private void addDrawerToList(List<IDrawerItem> lstSoldiersToExpand, SecondaryDrawerItem currSoldierDrawer) {
        int indexToRemove = -1;
        for (int i = 0; i < lstSoldiersToExpand.size(); i++) {
            if (((SecondaryDrawerItem) lstSoldiersToExpand.get(i)).getName().getText().equals(currSoldierDrawer.getName().getText())) {
                indexToRemove = i;
                break;
            }
        }

        if (indexToRemove >= 0) {
            lstSoldiersToExpand.remove(indexToRemove);
        }
        lstSoldiersToExpand.add(currSoldierDrawer);

//     // TODO: should fix problem
//     synchronized(SoldiersDrawerItem){
//         SoldiersDrawerItem.notifyAll();
//     }
    }

    @NonNull
    private String getDescription(UserInGroup user) {
        if (!user.getSubStatus().equals("")) {
            return user.getMainStatus() + ", " + user.getSubStatus();
        } else {
            return user.getMainStatus();
        }
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
                if (bitmap != null) {
                    newProfile.withIcon(new BitmapDrawable(bitmap));
                    currGroupDrawerItem.withIcon(new BitmapDrawable(bitmap));
                } else {
                    newProfile.withIcon(DrawerActivity.this.getResources().getDrawable(R.drawable.face_icon));
                    currGroupDrawerItem.withIcon(DrawerActivity.this.getResources().getDrawable(R.drawable.face_icon));
                }

                addNewProfile(newProfile);
            }
        };

        task.execute(url);
    }

    // TODO: prevent double code
    public void drawableFromUrl(String url, final IProfile newProfile) {

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
                }
                else {
                    newProfile.withIcon(DrawerActivity.this.getResources().getDrawable(R.drawable.face_icon));
                }

                addNewProfile(newProfile);
            }
        };

        task.execute(url);
    }

    private void addNewProfile(IProfile newProfile) {
        List<IProfile> profiles = headerResult.getProfiles();
        if (profiles != null && profiles.size() > 0) {
            for (int iProfileIndex = 0; iProfileIndex < profiles.size(); iProfileIndex++) {
                IProfile currProfile = profiles.get(iProfileIndex);
                if (currProfile.getName().getText().equals(newProfile.getName().getText())) {
                    headerResult.removeProfile(currProfile);
                }
                break;
            }
        }

        headerResult.addProfiles(newProfile);
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

    public void initCurrentUser() {
        if(mCurrentUser != null && loginUser == null) {
            loginUser = new Select().from(User.class).where(User.EMAIL_PROPERTY + " = '" + mCurrentUser.getEmail() + "'").executeSingle();
        }
    }
}
