package be.hcpl.android.beaconexample;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;

import be.hcpl.android.beaconexample.fragment.AboutFragment;
import be.hcpl.android.beaconexample.framework.NavigationDrawerFragment;


/**
 * this main activity is only in place to handle the fragment navigation, no beacon related stuff
 * to be found here
 *
 */
public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * used to keep track of last fragment showing
     */
    public static final String KEY_LAST_FRAGMENT = "last_fragment";

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * current fragment
     */
    private Fragment mContentFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set layout
        setContentView(R.layout.activity_main);

        // and title
        setTitle(R.string.app_title);
        //getActionBar().setIcon(getResources().getDrawable(R.drawable.ic_logo));
        // set system bar tint done in style using official L preview impl

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);
        // Restore state members from saved instance
        mContentFragment = getSupportFragmentManager().getFragment(savedInstanceState, KEY_LAST_FRAGMENT);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        if (mContentFragment != null && mContentFragment.isAdded() && getSupportFragmentManager() != null) {
            getSupportFragmentManager().putFragment(savedInstanceState,
                    KEY_LAST_FRAGMENT, mContentFragment);
        }
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);

    }

    @Override
    protected void onResume() {
        super.onResume();
        // make sure we always have some default
        if (mContentFragment == null) {
            mContentFragment = AboutFragment.createInstance();
        }
        // no backstack here
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, mContentFragment)
                .commit();
    }

    @Override
    public void onNavigationDrawerItemSelected(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        // use default here
        if (fragment != null) {
            // update the transfer content by replacing fragments
            switchContent(fragment);
        }
    }

    /**
     * helper to switch content with backstack
     *
     * @param fragment
     */
    public void switchContent(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                        // add to backstack
                .addToBackStack(fragment.getClass().getSimpleName())
                .commit();
        mContentFragment = fragment;
    }

}