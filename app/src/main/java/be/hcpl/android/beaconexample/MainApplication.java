package be.hcpl.android.beaconexample;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

/**
 * Wa have our custom application object (singleton) to manager the state of the background scanning
 * service. This has to be bound (so implement BeaconConsumer) in order to update the interval from
 * preferences and to toggle background mode
 *
 * regionbootstrap can be created without being bound
 *
 * Created by hanscappelle on 10/11/14.
 */
public class MainApplication extends Application implements BootstrapNotifier, BeaconConsumer {

    /**
     * a region bootstrap will create notifications if we enter (or exit) a specific beacon region
     *
     * a region bootstrap is created with the region and the bootstrapNotifier
     *
     * to cancel these notifications you need to call disable() on it. Restarting the notifications
     * is then done by creating a new region bootstrap instance.
     */
    private RegionBootstrap regionBootstrap;

    /**
     * this can be used to reduce power drain. This will cause the scanning to go in background mode
     * as soon as the app is no longer in front of the user. Caution! this also causes the service
     * to go out of background mode as soon as the app is in front again.
     */
    //private BackgroundPowerSaver backgroundPowerSaver;

    /**
     * a single beacon manager for the app
     */
    private BeaconManager beaconManager = null;

    @Override
    public void onCreate() {
        super.onCreate();

        beaconManager = BeaconManager.getInstanceForApplication(this);

        // we need a specific beacon parser set up to react on the ibeacon spec (instead of the
        // default altbeacon spec)
        beaconManager.getBeaconParsers().clear();
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));

        // Simply constructing this class and holding a reference to it in your custom Application class
        // enables auto battery saving of about 60%
        // disabled because this toggles the background mode automatically while we have it covered
        // already in the example fragments
        //backgroundPowerSaver = new BackgroundPowerSaver(this);

        // make a specific region for background scanning
        Region region = new Region(getString(R.string.region_background),
                Identifier.parse(getString(R.string.ble_uuid)), null, null);
        // init this region
        regionBootstrap = new RegionBootstrap(this, region);

        // debug option depends on build config
        beaconManager.setDebug(BuildConfig.DEBUG);
        // also in debug we use the beaconsimulator
        if(BuildConfig.DEBUG) {

            TimedBeaconSimulator simulator = new TimedBeaconSimulator(getApplicationContext());
            simulator.createTimedSimulatedBeacons();
            beaconManager.setBeaconSimulator(simulator);

        }

        // we need to bind to update settings or toggle background mode
        // always run in background mode settings application object create
        // beaconManager.setBackgroundMode(true);
        // updateSettings();
        beaconManager.bind(this);
    }

    /**
     * helper to update the background interval from preferences if available
     */
    private void updateSettings() {

        // oncreate also check for settings
        try {
            SharedPreferences prefs = getSharedPreferences(BuildConfig.APPLICATION_ID + "_preferences", Context.MODE_PRIVATE);

            String scanTime = prefs.getString("scantime", "");
            String betweenTime = prefs.getString("betweentime", "");

            long scan = Long.parseLong(betweenTime);
            long between = Long.parseLong(scanTime);

            // update
            if (scan != 0 && between != 0) {
                beaconManager.setBackgroundBetweenScanPeriod(scan);
                beaconManager.setBackgroundScanPeriod(between);
                beaconManager.updateScanPeriods();
                Log.d(MainApplication.class.getSimpleName(), String.format("Update interval with scan time %f, in between time %f", scan, between));
            }
        } catch (Exception e) {
            Log.e(MainApplication.class.getSimpleName(), "Ignoring interval settings", e);
        }
    }

    @Override
    public void didEnterRegion(Region arg0) {

        // create the notification here
        // use the receiver now to handle the notification so that we can intercept it
        Intent intent = new Intent();
        intent.setAction("be.hcpl.android.beaconexample.NOTIFY_FOR_BEACON");
        getApplicationContext().sendOrderedBroadcast(intent, null);

        // you can also start an activity instead
        // Intent intent = new Intent(this, MainActivity.class);
        // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Important:  make sure to add android:launchMode="singleInstance" in the manifest
        // to keep multiple copies of this activity from getting created if the user has
        // already manually launched the app.
        //this.startActivity(intent);
    }

    @Override
    public void didDetermineStateForRegion(int arg0, Region arg1) {
        // ignore in this example
    }

    @Override
    public void didExitRegion(Region region) {
        // ignore in this example
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        // release whatever is needed
        beaconManager.unbind(this);
        beaconManager = null;
    }

    @Override
    public void onBeaconServiceConnect() {
        // callback for when service connection is complete

        beaconManager.setBackgroundMode(true);
        updateSettings();
    }
}
