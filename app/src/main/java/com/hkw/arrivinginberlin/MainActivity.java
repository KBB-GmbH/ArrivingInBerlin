package com.hkw.arrivinginberlin;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabSelectedListener;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.UpdateManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, CustomMapFragment.OnFragmentInteractionListener, LocationFragment.OnListFragmentInteractionListener {
    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    ExpandableListAdapter mMenuAdapter;
    ExpandableListView expandableList;
    List<ExpandedMenuItem> listDataHeader;
    HashMap<ExpandedMenuItem, List<String>> listDataChild;
    private ActionBarDrawerToggle drawerToggle;
    private static final String TAG = "MainActivity";
    private static final String MapTag = "MAP";
    private CustomMapFragment mapFragment;
    private LocationFragment listFragment;
    private GoogleApiClient client;
    public ArrayList<JSONObject> mainLocations = new ArrayList<JSONObject>();


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if ((savedInstanceState == null) || (mapFragment == null)){
            new FetchLocationsTask().execute();
            FragmentManager fragmentManager = getFragmentManager();
            mapFragment = CustomMapFragment.newInstance(mainLocations);
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.content_container, mapFragment, MapTag);
            fragmentTransaction.commit();
        }

        BottomBar bottomBar = BottomBar.attach(this, savedInstanceState);
        bottomBar.setItemsFromMenu(R.menu.bottom_navigation, new OnMenuTabSelectedListener() {
            @Override
            public void onMenuItemSelected(int itemId) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction ft = fragmentManager.beginTransaction();
                switch (itemId) {
                    case R.id.map_item:
                        if (mapFragment == null) {
                            mapFragment = CustomMapFragment.newInstance(mainLocations);
                            updateLocations();
                        }
                        ft.hide(listFragment);
                        ft.show(mapFragment);
                        break;
                    case R.id.info_item:
                        break;
                    case R.id.list_item:
                        if (listFragment == null) {
                            listFragment = LocationFragment.newInstance(1, mainLocations);
                            ft.add(R.id.content_container, listFragment, "LIST");
                            ft.hide(mapFragment);
                            updateLocations();
                        }
                        ft.hide(mapFragment);
                        ft.show(listFragment);
                        break;
                }
                ft.commit();
            }
        });


        // Set the color for the active tab. Ignored on mobile when there are more than three tabs.
        bottomBar.setActiveTabColor("#C2185B");
        bottomBar.selectTabAtPosition(0, false);

        // Set a Toolbar to replace the ActionBar.
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Find our drawer view
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        // Find our drawer view
        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        expandableList = (ExpandableListView) findViewById(R.id.navMenu);
        nvDrawer.setItemIconTintList(null);

        drawerToggle = setupDrawerToggle();
        // Tie DrawerLayout events to the ActionBarToggle

        mDrawer.addDrawerListener(drawerToggle);
        // Setup drawer view
        if (nvDrawer != null) {
            setupDrawerContent(nvDrawer);
        }

        prepareListData();
        mMenuAdapter = new ExpandableMenuAdapter(this, listDataHeader, listDataChild, expandableList);

        // setting list adapter
        expandableList.setAdapter(mMenuAdapter);

        expandableList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                //Log.d("DEBUG", "submenu item clicked");
                return false;
            }
        });
        expandableList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                //Log.d("DEBUG", "heading clicked");
                return false;
            }
        });

        // Hockeyapp
        checkForUpdates();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void prepareListData() {
        listDataHeader = new ArrayList<ExpandedMenuItem>();
        listDataChild = new HashMap<ExpandedMenuItem, List<String>>();

        ExpandedMenuItem item1 = new ExpandedMenuItem();
        item1.setIconName("heading1");
        item1.setIconImg(R.drawable.counseling);
        // Adding data header
        listDataHeader.add(item1);

        ExpandedMenuItem item2 = new ExpandedMenuItem();
        item2.setIconName("heading2");
        item2.setIconImg(R.drawable.favorite);
        listDataHeader.add(item2);

        ExpandedMenuItem item3 = new ExpandedMenuItem();
        item3.setIconName("heading3");
        item3.setIconImg(R.drawable.lawyers_residence_and_asylum_law);
        listDataHeader.add(item3);

        // Adding child data
        List<String> heading1 = new ArrayList<String>();
        heading1.add("Submenu of item 1");

        List<String> heading2 = new ArrayList<String>();
        heading2.add("Submenu of item 2");
        heading2.add("Submenu of item 2");
        heading2.add("Submenu of item 2");

        listDataChild.put(listDataHeader.get(0), heading1);// Header, Child data
        listDataChild.put(listDataHeader.get(1), heading2);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        //revision: this don't works, use setOnChildClickListener() and setOnGroupClickListener() above instead
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        mDrawer.closeDrawers();
                        return true;
                    }
                });
    }


    private void updateLocations() {
        if (mainLocations.size() == 0) {
            new FetchLocationsTask().execute();
        }
    }


    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.hkw.arrivinginberlin/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.hkw.arrivinginberlin/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    // Hockeyapp methods
    private void checkForCrashes() {
        CrashManager.register(this);
    }

    private void checkForUpdates() {
        // Remove this for store builds!
        UpdateManager.register(this);
    }

    private void unregisterManagers() {
        UpdateManager.unregister();
    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_search, menu);
//
//        final MenuItem searchItem = menu.findItem(R.id.search);
//        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
//        searchView.setOnQueryTextListener(this);
//
//        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
//            @Override
//            public boolean onClose() {
//                if (mapFragment != null) {
//                    mapFragment.displayAllMarkers();
//                }
//                return false;
//            }
//        });
//
//        return true;
//    }
//
    @Override
    public boolean onQueryTextSubmit(String query) {
        // User pressed the search button
        if (mapFragment != null) {
            mapFragment.displayMarkersForSearchTerm(query);
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText.isEmpty()) {
            if (mapFragment != null) {
                mapFragment.displayAllMarkers();
            }
        }
        return false;
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open, R.string.drawer_close);
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // The action bar home/up action should open or close the drawer.
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                mDrawer.openDrawer(GravityCompat.START);
//                return true;
//        }
//
//        Log.i(TAG, "item drawer selected with id " + item);
//        return super.onOptionsItemSelected(item);
//    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {

        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();

    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);

    }

//    private void setupDrawerContent(NavigationView navigationView) {
//        navigationView.setNavigationItemSelectedListener(
//                new NavigationView.OnNavigationItemSelectedListener() {
//                    @Override
//                    public boolean onNavigationItemSelected(MenuItem menuItem) {
//                        selectDrawerItem(menuItem);
//                        return true;
//                    }
//                });
//    }

//
//    public void selectDrawerItem(MenuItem menuItem) {
//        Log.i(TAG, "drawer selected");
//        if (mapFragment == null){
//            return;
//        }
//
//        switch (menuItem.getItemId()) {
//            case R.id.nav_all_categories:
//                mapFragment.displayAllMarkers();
//                break;
//            case R.id.nav_counseling:
//                mapFragment.displayMarkersForCategory(1);
//                break;
//            case R.id.nav_doctor_gp_arabic:
//                mapFragment.displayMarkersForCategory(2);
//                break;
//            case R.id.nav_doctor_gp_farsi:
//                mapFragment.displayMarkersForCategory(3);
//                break;
//            case R.id.nav_doctor_gyn_arabic:
//                mapFragment.displayMarkersForCategory(4);
//                break;
//            case R.id.nav_doctor_gyn_farsi:
//                mapFragment.displayMarkersForCategory(5);
//                break;
//            case R.id.nav_german_language_classes:
//                mapFragment.displayMarkersForCategory(6);
//                break;
//            case R.id.nav_lawyers:
//                mapFragment.displayMarkersForCategory(7);
//                break;
//            case R.id.nav_police:
//                mapFragment.displayMarkersForCategory(8);
//                break;
//            case R.id.nav_authorities:
//                mapFragment.displayMarkersForCategory(9);
//                break;
//            case R.id.nav_libraries:
//                mapFragment.displayMarkersForCategory(10);
//                break;
//            case R.id.nav_transport:
//                mapFragment.displayMarkersForCategory(11);
//                break;
//            case R.id.nav_shopping_food:
//                mapFragment.displayMarkersForCategory(12);
//                break;
//            case R.id.nav_sports_freetime:
//                mapFragment.displayMarkersForCategory(13);
//                break;
//
//            default:
//                break;
//        }
//
//
//        // Highlight the selected item has been done by NavigationView
//        menuItem.setChecked(true);
//        // Set action bar title
//        setTitle(menuItem.getTitle());
//        // Close the navigation drawer
//        mDrawer.closeDrawers();
//    }

    public void onFragmentInteraction(Uri uri){
        //you can leave it empty
    }

    @Override
    public void onListFragmentInteraction(LocationFragment.LocationItem item) {
    }


    /********* FETCHING AND SETTING LOCATION DATA**********************/
    public class FetchLocationsTask extends AsyncTask<Void, Void, List<JSONObject>> {
        @Override
        protected List<JSONObject> doInBackground(Void... params) {
            return new UmapDataRequest().fetchLocations();
        }

        @Override
        protected void onCancelled() {
            ArrayList<JSONObject> locations = getStoredLocations();
            if ((locations != null) && (locations.size() > 0)) {
                mainLocations = locations;
                if (mapFragment != null){
                    mapFragment.receiveDataFromActivity(locations);
                }

                if (listFragment != null){
                    listFragment.receiveDataFromActivity(locations);
                }
            } else {
                showOfflineMessage();
            }
        }

        @Override
        protected void onPostExecute(List<JSONObject> locations) {
            //check if the map exists already
            Log.i("FETCH", "arrived at post exec with locations: " + locations);

            if ((locations != null) && (locations.size() > 0)) {
                //store locations
                mainLocations = (ArrayList<JSONObject>) locations;
                if (mapFragment != null){
                    mapFragment.receiveDataFromActivity((ArrayList<JSONObject>) locations);
                }
                if (listFragment != null){
                    listFragment.receiveDataFromActivity((ArrayList<JSONObject>) locations);
                }

                storeLocations((ArrayList<JSONObject>) locations);
            } else {
                if (!showStoredLocations()){
                    showOfflineMessage();
                }
            }
        }

        private boolean showStoredLocations() {
            List<JSONObject> storedLocations = getStoredLocations();
            if ((storedLocations != null) && (storedLocations.size() > 0)) {
                mainLocations = (ArrayList<JSONObject>) storedLocations;
                if (mapFragment != null){
                    mapFragment.receiveDataFromActivity((ArrayList<JSONObject>) storedLocations);
                }

                if (listFragment != null){
                    listFragment.receiveDataFromActivity((ArrayList<JSONObject>) storedLocations);
                }

                return true;
            } else {
                return false;
            }
        }

        private void showOfflineMessage() {
            String message = getString(R.string.offline_message);
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
        }


        private ArrayList<JSONObject> getStoredLocations() {
            SaveArray save = new SaveArray(MainActivity.this.getApplicationContext());
            ArrayList<JSONObject> locations = save.getArray("locations");
            return locations;
        }

        private void storeLocations(ArrayList<JSONObject> locations) {
            SaveArray save = new SaveArray(MainActivity.this.getApplicationContext());
            save.saveArray("locations", locations);
            Log.i(TAG, "Saved Locations");
        }
    }

}
