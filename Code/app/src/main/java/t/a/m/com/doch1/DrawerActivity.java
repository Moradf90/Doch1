package t.a.m.com.doch1;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mikepenz.fastadapter.commons.utils.RecyclerViewCacheUtil;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.itemanimators.AlphaCrossFadeAnimator;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.BadgeStyle;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialdrawer.interfaces.OnCheckedChangeListener;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.ExpandableBadgeDrawerItem;
import com.mikepenz.materialdrawer.model.ExpandableDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondarySwitchDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryToggleDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.SwitchDrawerItem;
import com.mikepenz.materialdrawer.model.ToggleDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.mikepenz.octicons_typeface_library.Octicons;

public class DrawerActivity extends AppCompatActivity {
    private static final int PROFILE_SETTING = 100000;

    //save our header or result
    private AccountHeader headerResult = null;
    private Drawer result = null;
    private FirebaseUser mCurrentUser;


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

        ExpandableDrawerItem soldiersDrawerItem = new ExpandableDrawerItem().withName("My Soldiers").withIcon(GoogleMaterial.Icon.gmd_accounts_list).withIdentifier(19).withSelectable(false).withSubItems(
                new SecondaryDrawerItem().withName("Amit Hanuch").withLevel(2).withIcon(getDrawable(R.drawable.amit72)).withIdentifier(2002),
//                new SecondaryDrawerItem().withName("Tom Dinur").withLevel(2).withIcon(getDrawable(R.drawable.tom72)).withIdentifier(2003),
//                new SecondaryDrawerItem().withName("Michal Teverovsky ").withLevel(2).withIcon(getDrawable(R.drawable.michal72)).withIdentifier(2004),
                new SecondaryDrawerItem().withName("Tal Itah").withLevel(2).withIcon(getDrawable(R.drawable.tal72)).withIdentifier(2005),
//                new SecondaryDrawerItem().withName("Batel Daudi").withLevel(2).withIcon(getDrawable(R.drawable.batel72)).withIdentifier(2006),
//                new SecondaryDrawerItem().withName("Yair Vered").withLevel(2).withIcon(getDrawable(R.drawable.yair72)).withIdentifier(2007),
//                new SecondaryDrawerItem().withName("Lior Hirch").withLevel(2).withIcon(getDrawable(R.drawable.lior72)).withIdentifier(2008),
                new SecondaryDrawerItem().withName("Omer Haimovich").withLevel(2).withIcon(getDrawable(R.drawable.omer72)).withIdentifier(2009));


        //Create the drawer
        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withHasStableIds(true)
                .withItemAnimator(new AlphaCrossFadeAnimator())
                .withAccountHeader(headerResult) //set the AccountHeader we created earlier for the header
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.profile_fragment).withIcon(GoogleMaterial.Icon.gmd_account).withIdentifier(1).withSelectable(false),
                        new PrimaryDrawerItem().withName(R.string.main_fragment).withDescription(R.string.dsc_main_statuses).withIcon(FontAwesome.Icon.faw_wheelchair).withIdentifier(2).withSelectable(false),
//                        new PrimaryDrawerItem().withName(R.string.drawer_item_multi_drawer).withDescription(R.string.drawer_item_multi_drawer_desc).withIcon(FontAwesome.Icon.faw_gamepad).withIdentifier(3).withSelectable(false),
                        new SectionDrawerItem().withName(R.string.drawer_item_section_header),

                        soldiersDrawerItem,
                        new PrimaryDrawerItem().withName(R.string.send_statuses).withIcon(Octicons.Icon.oct_radio_tower).withIdentifier(9),
                        new DividerDrawerItem(),

                        new SecondaryDrawerItem().withName(R.string.drawer_item_contact).withIcon(GoogleMaterial.Icon.gmd_phone).withIdentifier(21).withTag("Bullhorn")
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

        result.updateBadge(4, new StringHolder(10 + ""));
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
    private void selectItem(int position) {

        Fragment fragment;
        // Create a new fragment and specify the planet to show based on position
        if (position == 1) {
            fragment = new ProfileFragment();
        }
        else if (position == 2) {
            fragment = new MainFragment();
        }
        else if (position == 9) {
            Toast.makeText(DrawerActivity.this, "Send...", Toast.LENGTH_SHORT).show();
            fragment = new MainFragment();
        }
        else if (position == 21) {
            Toast.makeText(DrawerActivity.this, "Call Morad", Toast.LENGTH_SHORT).show();
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
