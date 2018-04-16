package kogvet.eye.Notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import java.util.Calendar;

import kogvet.eye.R;


/**
 * Controls for sending notifications.
 */
public class NotificationService extends AppCompatActivity {

    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_service);

        sendMorningAlarm();;
    }

    public void sendMorningAlarm()
    {
        alarmMgr = (AlarmManager) NotificationService.this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(NotificationService.this, AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(NotificationService.this, 0, intent, 0);

        // Set the alarm to start at xx:xx PM
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 13);
        calendar.set(Calendar.MINUTE, 43);

        // setRepeating() lets you specify a precise custom interval--in this case,
        // 1 day
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent);
    }

}
