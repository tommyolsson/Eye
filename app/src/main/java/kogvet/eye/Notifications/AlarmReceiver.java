package kogvet.eye.Notifications;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import kogvet.eye.MainActivity;

public class AlarmReceiver extends BroadcastReceiver {

    private static final int NOTI_PRIMARY1 = 1100;
    private static final int NOTI_PRIMARY2 = 1101;
    private static final int NOTI_SECONDARY1 = 1200;
    private static final int NOTI_SECONDARY2 = 1201;

    private NotificationHelper noti;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Meddelande", "Meddelande");
        Toast.makeText(context, "Alarm!", Toast.LENGTH_LONG).show();

        noti = new NotificationHelper(context);
        sendNotification(NOTI_PRIMARY1, "Dagens schema", "8 - 9: Skriva CV");
    }


    /**
     * Send activity notifications.
     *
     * @param id The ID of the notification to create
     * @param title The title of the notification
     * @param body The body of the notification
     */
    public void sendNotification(int id, String title, String body) {
        Notification.Builder nb = null;
        switch (id) {
            case NOTI_PRIMARY1:
                nb = noti.getNotification1(title, body);
                break;

            case NOTI_PRIMARY2:
                nb = noti.getNotification1(title, body);
                break;

            case NOTI_SECONDARY1:
                nb = noti.getNotification2(title, body);
                break;

            case NOTI_SECONDARY2:
                nb = noti.getNotification2(title, body);
                break;
        }
        if (nb != null) {
            noti.notify(id, nb);
        }
    }
 }
