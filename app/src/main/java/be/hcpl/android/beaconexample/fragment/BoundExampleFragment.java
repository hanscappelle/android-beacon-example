package be.hcpl.android.beaconexample.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;

import be.hcpl.android.beaconexample.BuildConfig;
import be.hcpl.android.beaconexample.MainActivity;
import be.hcpl.android.beaconexample.R;
import be.hcpl.android.beaconexample.beacon.BeaconNotificationReceiver;
import be.hcpl.android.beaconexample.framework.TemplateFragment;


/**
 * The bound example demonstrates how to perform scanning in foreground mode so we can handle
 * the results on screen directly
 *
 * Created by hanscappelle on 23/10/14.
 */
public class BoundExampleFragment extends TemplateFragment implements BeaconConsumer {

    /**
     * to get back after BT enabled
     */
    private static final int REQUEST_ENABLE_BT = 110;

    /**
     * bm is a singleton
     */
    private BeaconManager beaconManager = null;

    /**
     * ui reference
     */
    private TextView tvDetails;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // ignore the notification here
            // and block propagation
            abortBroadcast();
        }
    };

    /**
     * ctor
     * @return
     */
    public static BoundExampleFragment createInstance() {
        return new BoundExampleFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        beaconManager = BeaconManager.getInstanceForApplication(getActivity());
        beaconManager.bind(this);

        if (!BuildConfig.DEBUG) {
            verifyBluetooth();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ranging, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvDetails = (TextView)view.findViewById(R.id.ranging_details);
    }

    @Override
    public void onResume() {
        super.onResume();

        // remove any notification that was already showing (just in case)
        NotificationManager nm = (NotificationManager) getActivity().getSystemService(Activity.NOTIFICATION_SERVICE);
        nm.cancel(BeaconNotificationReceiver.SINGLE_NOTIFICATION_ID);

        // from http://commonsware.com/blog/2010/08/11/activity-notification-ordered-broadcast.html
        // Dynamically register a BroadcastReceiever in your activity, with an IntentFilter set up
        // for the aforementioned action string and with a positive priority (the default priority
        // for a filter is 0). This receiver should then have the activity do whatever it needs to
        // do to update the UI based on this event. The receiver should also call abortBroadcast()
        // to prevent others from getting it. Be sure to register the receiver in onStart() or
        // onResume() and unregister the receiver in the corresponding onStop or onPause() method
        IntentFilter ifi = new IntentFilter("be.hcpl.android.beaconexample.NOTIFY_FOR_BEACON");
        ifi.setPriority(10);
        getActivity().registerReceiver(mReceiver, ifi);

        beaconManager.setBackgroundMode(false);
    }

    @Override
    public void onPause() {
        super.onPause();

        // unregister receiver here
        getActivity().unregisterReceiver(mReceiver);

        // go back into background mode scanning here
        beaconManager.setBackgroundMode(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // unbind if needed
        if( beaconManager.isBound(this) ) beaconManager.unbind(this);
    }

    @Override
    public void onBeaconServiceConnect() {

        // configure a range notifier
        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(final Collection<Beacon> beacons, Region region) {

                // log beacons info once received
                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        tvDetails.setText(String.format("found %s beacons", beacons.size()));
                    }
                });
                if (beacons.size() > 0) {
                    Log.i(BoundExampleFragment.class.getSimpleName(), "Bound fragment is handling discovered beacons now");
                }
            }
        });

        try {
            // and start ranging for the given region. This region has a uuid specificed so will
            // only react on beacons with this uuid, the 2 other fields are minor and major version
            // to be more specific if desired
            beaconManager.startRangingBeaconsInRegion(new Region(getString(R.string.region_background),
                    Identifier.parse(getString(R.string.ble_uuid)), null, null));
        } catch (RemoteException e) {
            Log.e(BoundExampleFragment.class.getSimpleName(), "Failed to start ranging", e);
        }
    }

    @Override
    public Context getApplicationContext() {
        return getActivity().getApplicationContext();
    }

    @Override
    public void unbindService(ServiceConnection serviceConnection) {
        getActivity().unbindService(serviceConnection);
    }

    @Override
    public boolean bindService(Intent intent, ServiceConnection serviceConnection, int i) {
        return getActivity().bindService(intent, serviceConnection, i);
    }

    /**
     * check for bluetooth enabled or not
     */
    private void verifyBluetooth() {

        try {
            if (!beaconManager.checkAvailability()) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(getString(R.string.title_bluetooth_not_enabled));
                builder.setMessage(getString(R.string.enable_bluetooth));
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        //finish();
                    }
                });
                builder.setCancelable(false);
                builder.show();
            }
        } catch (RuntimeException e) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
            builder.setTitle(getString(R.string.title_bluetooth_le_not_available));
            builder.setMessage(getString(R.string.sorry_bluetooth_not_supported));
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    final Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    //finish();
                }
            });
            builder.show();

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                verifyBluetooth();
            } else {
                final Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                //finish();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    public int getTitleResourceId() {
        return R.string.title_bound_example;
    }

}
