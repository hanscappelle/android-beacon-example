package be.hcpl.android.beaconexample;

import android.content.Context;

import org.altbeacon.beacon.AltBeacon;
import org.altbeacon.beacon.Beacon;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by hanscappelle on 12/11/14.
 */
public class TimedBeaconSimulator implements org.altbeacon.beacon.simulator.BeaconSimulator {

    private List<Beacon> beacons;
    private String uuid;
    private Random random = new Random();

    /**
     * Creates empty beacons ArrayList.
     */
    public TimedBeaconSimulator(Context context) {
        uuid = context.getString(R.string.ble_uuid);
        beacons = new ArrayList<Beacon>();
    }

    /**
     * Required getter method that is called regularly by the Android Beacon Library.
     * Any beacons returned by this method will appear within your test environment immediately.
     */
    public List<Beacon> getBeacons() {
        return beacons;
    }

    /**
     * Creates simulated beacons all at once, only used for preparing data
     */
    private void createBasicSimulatedBeacons() {

        // just some random beacons here with random data
        Beacon beacon0 = new AltBeacon.Builder().setId1(uuid)
                .setRssi(-1 * (40 + random.nextInt(60)))
                .setTxPower(-55)
                .build();

        Beacon beacon1 = new AltBeacon.Builder().setId1(uuid)
                .setId2("123").setId3("456").setRssi(-1 * (40 + random.nextInt(60))).setTxPower(-55).build();

        Beacon beacon2 = new AltBeacon.Builder().setId1(uuid)
                .setId2("789").setId3("012").setRssi(-1 * (40 + random.nextInt(60))).setTxPower(-55).build();

        beacons.add(beacon0);
        beacons.add(beacon1);
        beacons.add(beacon2);
    }

    private ScheduledExecutorService scheduleTaskExecutor;


    /**
     * Simulates a new beacon every 10 seconds until it runs out of new ones to add and then starts
     * removing these beacons again
     */
    public void createTimedSimulatedBeacons() {

        // populate beacons
        createBasicSimulatedBeacons();

        // empty list holding the emulated beacons
        final List<Beacon> finalBeacons = new ArrayList<Beacon>(beacons);

        // these are the discovered beacons
        //Clearing beacons list to prevent all beacons from appearing immediately.
        //These will be added back into the beacons list from finalBeacons later.
        beacons.clear();

        scheduleTaskExecutor = Executors.newScheduledThreadPool(5);

        // This schedules an beacon to appear every 10 seconds and removes them again
        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                try {
                    //putting a single beacon into the beacons list
                    if (finalBeacons.size() > beacons.size()) {
                        // update the rssi to a random value
                        Beacon b = finalBeacons.get(beacons.size());
                        b.setRssi(-1 * (40 + random.nextInt(60)));
                        beacons.add(b);
                    }
                    // remove the beacons again also
                    else if (!beacons.isEmpty()) {
                        beacons.remove(beacons.size() - 1);
                    } else {
                        scheduleTaskExecutor.shutdown();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, 10, TimeUnit.SECONDS);
    }

}