package be.hcpl.android.beaconexample.beacon;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import be.hcpl.android.beaconexample.MainActivity;
import be.hcpl.android.beaconexample.R;

/**
 * see http://commonsware.com/blog/2010/08/11/activity-notification-ordered-broadcast.html
 * for this concept of either blocking the notification if activity in foreground or handling it
 * when in background
 * <p/>
 * Created by hanscappelle on 6/11/14.
 */
public class BeaconNotificationReceiver extends BroadcastReceiver {

    /**
     * we use this single id so we can always remove the notification if desired
     */
    public static final int SINGLE_NOTIFICATION_ID = 100;

    @Override
    public void onReceive(Context context, Intent intent) {

        // see link for details, this way we can intercept the notification in certain situations
        // http://commonsware.com/blog/2010/08/11/activity-notification-ordered-broadcast.html

        // raise the notification
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        .setContentTitle(context.getString(R.string.notification_title))
                        .setContentText(context.getString(R.string.notification_description))
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher))
                        .setAutoCancel(true);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(new Intent(context, MainActivity.class));
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        builder.setContentIntent(resultPendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(BeaconNotificationReceiver.SINGLE_NOTIFICATION_ID, builder.build());
    }

}
