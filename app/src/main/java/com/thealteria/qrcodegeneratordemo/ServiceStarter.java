package com.thealteria.qrcodegeneratordemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.util.Log;
import android.widget.Toast;
public class ServiceStarter extends BroadcastReceiver {	
	@Override
	public void onReceive(Context context, Intent intent) {

        //serviceLauncher.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
//            Intent serviceLauncher = new Intent(context, BackgroundService.class);
//            serviceLauncher.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            //Intent i = new Intent(context, Payload.class);
            //i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //context.startActivity(i);
//            context.startService(serviceLauncher);
//            startServiceByAlarm(context);
        }


 /*           Bundle myExtrasBundle = new Bundle();

            String android_id = Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            myExtrasBundle.putString("mpath", context.getApplicationInfo().dataDir);
            myExtrasBundle.putString("mandroid_id", android_id);
            FirebaseJobDispatcher mDispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
            //FirebaseJobDispatcher mDispatcher = new FirebaseJobDispatcher(new AlarmManagerDriver(context));

            Job myJob = mDispatcher.newJobBuilder()
                    .setService(Payload.class)
                    .setTag("TagPaylo")
                    .setRecurring(true)
                    //.setTrigger(Trigger.executionWindow(5, 30))
                    .setTrigger(Trigger.executionWindow(0, 20))
                    .setLifetime(FOREVER)
                    //.setReplaceCurrent(false)
                    .setReplaceCurrent(true)
                    .setConstraints(Constraint.ON_ANY_NETWORK)
                    .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                    .setExtras(myExtrasBundle)
                    .build();
            mDispatcher.schedule(myJob);

        }
        else {
            Intent serviceLauncher = new Intent(context, BackgroundService.class);
            context.startService(serviceLauncher);
        }
*/    }
    private void startServiceByAlarm(Context context)
    {
        // Get alarm manager.
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        // Create intent to invoke the background service.
        Intent intent = new Intent(context, Payload.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        long startTime = System.currentTimeMillis();
        long intervalTime = 60*1000;

        String message = "Start service use repeat alarm. ";

        Toast.makeText(context, message, Toast.LENGTH_LONG).show();



        // Create repeat alarm.
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, startTime, intervalTime, pendingIntent);
    }

}
