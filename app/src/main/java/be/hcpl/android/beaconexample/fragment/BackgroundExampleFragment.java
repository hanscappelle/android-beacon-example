package be.hcpl.android.beaconexample.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import org.altbeacon.beacon.BeaconManager;

import be.hcpl.android.beaconexample.BuildConfig;
import be.hcpl.android.beaconexample.R;
import be.hcpl.android.beaconexample.framework.TemplateFragment;


/**
 * The background example is only used to update the intervals of the background scanning service.
 * The service itself is created in the oncreate of the application object where bind also happens
 *
 * Created by hanscappelle on 23/10/14.
 */
public class BackgroundExampleFragment extends TemplateFragment {

    /**
     * the input fields for config here
     */
    private EditText scanTime, inBetweenTime;

    /**
     * bm is a singleton
     */
    private BeaconManager beaconManager = null;

    /**
     * the app preferences for storing the intervals
     */
    private SharedPreferences prefs;

    /**
     * ctor
     * @return
     */
    public static BackgroundExampleFragment createInstance() {
        return new BackgroundExampleFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        // always get a single instance of the beacon manager
        beaconManager = BeaconManager.getInstanceForApplication(getActivity());

        // store these in prefs
        prefs = getActivity().getSharedPreferences(BuildConfig.APPLICATION_ID+"_preferences", Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_background, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        // restore prefs
        String scan = prefs.getString("scantime", "");
        String between = prefs.getString("betweentime", "");
        scanTime.setText(scan);
        inBetweenTime.setText(between);
        // these values are set to the beaconmanager in the application object
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        scanTime = (EditText) view.findViewById(R.id.edit_scantime);
        inBetweenTime = (EditText) view.findViewById(R.id.edit_inbetweentime);

    }

    private void handleNewValues(){
        try {
            long scan = Long.parseLong(inBetweenTime.getText().toString());
            long between = Long.parseLong(scanTime.getText().toString());

            // store prefs
            prefs.edit().putString("scantime", String.valueOf(scan)).putString("betweentime", String.valueOf(between)).commit();

            // these intervals are only applied to service running in background mode
            beaconManager.setBackgroundBetweenScanPeriod(scan);
            beaconManager.setBackgroundScanPeriod(between);
            beaconManager.updateScanPeriods();

            Toast.makeText(getActivity(), R.string.msg_values_updated, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getActivity(), R.string.err_input_incomplete, Toast.LENGTH_SHORT).show();
            Log.e(BackgroundExampleFragment.class.getSimpleName(), "failed to update background scanning intervals", e);
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.settings, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
        case R.id.action_confirm:
            // update settings here from input
            handleNewValues();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public int getTitleResourceId() {
        return R.string.title_background_example;
    }

}
