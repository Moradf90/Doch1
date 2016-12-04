package t.a.m.com.doch1;

import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mikepenz.fastadapter.commons.utils.RecyclerViewCacheUtil;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.itemanimators.AlphaCrossFadeAnimator;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.ExpandableDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.octicons_typeface_library.Octicons;

public class DrawerActivity extends AppCompatActivity {
    private static final int PROFILE_SETTING = 100000;

    //save our header or result
    private AccountHeader headerResult = null;
    private Drawer result = null;
    public static FirebaseUser mCurrentUser;

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
        final IProfile profile = new ProfileDrawerItem().withName(mCurrentUser.getDisplayName()).withEmail(mCurrentUser.getEmail()).withIcon(mCurrentUser.getPhotoUrl()).withIdentifier(100);

        // Create the AccountHeader
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withTranslucentStatusBar(true)
                .withHeaderBackground(R.drawable.header)
                .addProfiles(
                        profile
                        //don't ask but google uses 14dp for the add account icon in gmail but 20dp for the normal icons (like manage account)
//                        new ProfileSettingDrawerItem().withName("Add Account").withDescription("Add new GitHub Account").withIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_plus).actionBar().paddingDp(5).colorRes(R.color.material_drawer_primary_text)).withIdentifier(PROFILE_SETTING),
//                        new ProfileSettingDrawerItem().withName("Manage Account").withIcon(GoogleMaterial.Icon.gmd_settings).withIdentifier(100001)
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean current) {
                        //sample usage of the onProfileChanged listener
                        //if the clicked item has the identifier 1 add a new profile ;)
                        if (profile instanceof IDrawerItem && profile.getIdentifier() == PROFILE_SETTING) {
                            int count = 100 + headerResult.getProfiles().size() + 1;
                            IProfile newProfile = new ProfileDrawerItem().withNameShown(true).withName("Batman" + count).withEmail("batman" + count + "@gmail.com").withIcon(R.drawable.profile5).withIdentifier(count);
                            if (headerResult.getProfiles() != null) {
                                //we know that there are 2 setting elements. set the new profile above them ;)
                                headerResult.addProfile(newProfile, headerResult.getProfiles().size() - 2);
                            } else {
                                headerResult.addProfiles(newProfile);
                            }
                        }

                        //false if you have not consumed the event and it should close the drawer
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .build();

        PrimaryDrawerItem MyProfileDrawerItem = new PrimaryDrawerItem().withName(R.string.profile_fragment).withIcon(GoogleMaterial.Icon.gmd_account).withIdentifier(1).withSelectable(false);
        PrimaryDrawerItem FillStatusesDrawerItem = new PrimaryDrawerItem().withName(R.string.main_fragment).withDescription(R.string.dsc_main_statuses).withIcon(FontAwesome.Icon.faw_wheelchair).withIdentifier(2).withSelectable(false);

        ExpandableDrawerItem SoldiersDrawerItem = new ExpandableDrawerItem().withName("My Soldiers").withIcon(GoogleMaterial.Icon.gmd_accounts_list).withIdentifier(19).withSelectable(false).withSubItems(
                new SecondaryDrawerItem().withName("Amit Hanuch").withLevel(2).withIcon(getDrawable(R.drawable.amit72)).withIdentifier(2002).withSelectable(false),
                new SecondaryDrawerItem().withName("Tal Itah").withLevel(2).withIcon(getDrawable(R.drawable.tal72)).withDescription("Status A - excuse 2").withIdentifier(2005).withSelectable(false).withTextColor(Color.rgb(47, 183, 47)),
                new SecondaryDrawerItem().withName("Omer Haimovich").withDescription("Status C - excuse 2").withTextColor(Color.rgb(255, 14, 14)).withLevel(2).withIcon(getDrawable(R.drawable.omer72)).withIdentifier(2009).withSelectable(false));

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
                        FillStatusesDrawerItem,
//                        new PrimaryDrawerItem().withName(R.string.drawer_item_multi_drawer).withDescription(R.string.drawer_item_multi_drawer_desc).withIcon(FontAwesome.Icon.faw_gamepad).withIdentifier(3).withSelectable(false),
                        new SectionDrawerItem().withName(R.string.drawer_item_section_header),

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
            result.setSelection(2, false);
            selectItem(2);

            //set the active profile
            headerResult.setActiveProfile(profile);
        }

//        result.updateBadge(4, new StringHolder(10 + ""));


    }

//    private OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {
//        @Override
//        public void onCheckedChanged(IDrawerItem drawerItem, CompoundButton buttonView, boolean isChecked) {
//            if (drawerItem instanceof Nameable) {
//                Log.i("material-drawer", "DrawerItem: " + ((Nameable) drawerItem).getName() + " - toggleChecked: " + isChecked);
//            } else {
//                Log.i("material-drawer", "toggleChecked: " + isChecked);
//            }
//        }
//    };

    /** Swaps fragments in the main content view */
    private void selectItem(int identifier) {

        Fragment fragment;
        // Create a new fragment and specify the planet to show based on position
        if (identifier == 1) {
            fragment = new ProfileFragment();
        }
        else if (identifier == 2) {
            fragment = new MainFragment();
        }
        else if (identifier == 9) {
            Toast.makeText(DrawerActivity.this, "Send...", Toast.LENGTH_SHORT).show();
            fragment = new MainFragment();
        }
        else if (identifier == 21) {
            Toast.makeText(DrawerActivity.this, "Call Morad", Toast.LENGTH_SHORT).show();
            fragment = new MainFragment();
        }
        // Call morad
        else if (identifier == 2501) {
            Toast.makeText(DrawerActivity.this, "Call Morad", Toast.LENGTH_SHORT).show();
            fragment = new MainFragment();
        }
        // sms Morad
        else if (identifier == 2502) {
            Toast.makeText(DrawerActivity.this, "sms Morad", Toast.LENGTH_SHORT).show();

            fragment = new MainFragment();
        }
        // email morad
        else if (identifier == 2503) {
            Toast.makeText(DrawerActivity.this, "mail Morad", Toast.LENGTH_SHORT).show();
            fragment = new MainFragment();
        }
        else {
            fragment = new MainFragment();

        }
//        Bundle args = new Bundle();
//        args.putInt(MainFragment.ARG_PLANET_NUMBER, position);
//        fragment.setArguments(args);

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.frame_container, fragment)
                .commit();

        // Highlight the selected item, update the title, and close the drawer
//        mDrawerList.setItemChecked(position, true);
//        setTitle(mPlanetTitles[position]);
//        mDrawerLayout.closeDrawer(mDrawerList);
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

}
