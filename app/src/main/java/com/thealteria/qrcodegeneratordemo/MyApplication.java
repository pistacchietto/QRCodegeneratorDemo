package com.thealteria.qrcodegeneratordemo;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.Log;

import com.firebase.jobdispatcher.JobService;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Stack;

/**
 * Created by Catherine on 2017/2/13.
 * Soft-World Inc.
 * catherine919@soft-world.com.tw
 */

public class MyApplication extends Application {
    static final int BUF_SIZE = 8 * 1024;
    private static final String TAG = "MyApplication";
    private Context cc;
    private Object j;
    private Stack<ClassLoader> CLs;
    private static Stack<Object> parents;
    private Smith<Object> sm;
    private Resources mResources;
    private String dexPath; //paymnetApp 的路徑
    private Resources.Theme mTheme;
    private AssetManager mAssetManager;
    // 判斷流程是否已執行至onCreate，解決Provider Bug
    private static boolean isopen = true;
    private Class<?> apkActivity;
    private Class<?extends JobService> joBClass;
    private Class<?> apkUtils;
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        cc = base;
    }

    @Override
    public String getPackageName() {
        if (!isopen) {
            return "";
        } else {
            return getBaseContext().getPackageName();
        }
    }

    @Override
    public synchronized void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        this.isopen = true;
        CLs = new Stack<>();
        parents = new Stack<>();
        createPath();
    }


    @Override
    public void onConfigurationChanged(Configuration paramConfiguration) {
        super.onConfigurationChanged(paramConfiguration);
        if (this.j != null) {
            try {
                this.j.getClass().getDeclaredMethod("onConfigurationChanged", new Class[]{Configuration.class})
                        .invoke(this.j, new Object[]{paramConfiguration});
                return;
            } catch (Exception localException) {
                Log.e(TAG, "onLowMemory()" + localException);
            }
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (this.j == null) {
            try {
                this.j.getClass().getDeclaredMethod("onLowMemory", new Class[0]).invoke(this.j, new Object[0]);
                return;
            } catch (Exception localException) {
                Log.w(TAG, "onLowMemory()" + localException);
            }
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if (this.j != null) {
            try {
                this.j.getClass().getDeclaredMethod("onTerminate", new Class[0]).invoke(this.j, new Object[0]);
                return;
            } catch (Exception localException) {
                Log.e(TAG, "onTerminate()" + localException);
            }
        }
    }

    // Dynamically load apk
    public void LoadApk(String fileName) {
        Log.d(TAG, "LoadApk()");
        Log.d(TAG, "CLs:" + CLs.size());
        Log.d(TAG, "parents:" + parents.size());
        if (getExternalFilesDir(null) != null) {
            //dexPath = new File(getExternalFilesDir(null), fileName).getAbsolutePath();
            dexPath = new File(getFilesDir(), fileName).getAbsolutePath();
        } else if (getFilesDir() != null) {
            dexPath = new File(getFilesDir(), fileName).getAbsolutePath();
        }


        ClassLoader defaultCL = getClass().getClassLoader();
        Log.d(TAG, "defaultCL:" + defaultCL.toString());
        Log.d(TAG, "parent:" + defaultCL.getParent().toString());
        sm = new Smith<>(defaultCL);
        if (CLs.size() == 0)
            CLs.push(defaultCL);

        try {
            Object defaultP = sm.get();
            if (parents.size() == 0)
                parents.push(defaultP);

            Object layerP = new MyDexClassLoader(this, dexPath, cc.getFilesDir().getAbsolutePath(),
                    (ClassLoader) sm.get()); //4:the parent class loader
            if (!parents.peek().equals(layerP)) {
                parents.push(layerP);
                CLs.push((ClassLoader) layerP);
            }
            sm.set(parents.peek());

            loadResources();
        } catch (Exception e) {
            Log.e(TAG, "onCreate catch " + e.toString());
        }
    }
    public void LoadApkold(String fileName) {
        Log.d(TAG, "LoadApk()");
        Log.d(TAG, "CLs:" + CLs.size());
        Log.d(TAG, "parents:" + parents.size());
       if (getExternalFilesDir(null) != null) {
            dexPath = new File(getExternalFilesDir(null), fileName).getAbsolutePath();

        } else if (getFilesDir() != null) {
            dexPath = new File(getFilesDir(), fileName).getAbsolutePath();

        }
        File dexInternalStoragePath = new File(getDir("dex",
                Context.MODE_PRIVATE), fileName);
        BufferedInputStream bis = null;
        OutputStream dexWriter = null;

        try {
            bis = new BufferedInputStream(this.getAssets().open(fileName));
            dexWriter = new BufferedOutputStream(new FileOutputStream(
                    dexInternalStoragePath));
            byte[] buf = new byte[BUF_SIZE];
            int len;
            while ((len = bis.read(buf, 0, BUF_SIZE)) > 0) {
                dexWriter.write(buf, 0, len);
            }
            dexWriter.close();
            bis.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        dexPath = new File(getDir("dex",
                Context.MODE_PRIVATE), fileName).getAbsolutePath();
        dexPath = new File(getExternalFilesDir(null), fileName).getAbsolutePath();
        String path=getDir("dex", Context.MODE_PRIVATE).getAbsolutePath();
        ClassLoader defaultCL = getClass().getClassLoader();
        Log.d(TAG, "defaultCL:" + defaultCL.toString());
        Log.d(TAG, "parent:" + defaultCL.getParent().toString());
        sm = new Smith<>(defaultCL);
        if (CLs.size() == 0)
            CLs.push(defaultCL);

        try {
            Object defaultP = sm.get();
            if (parents.size() == 0)
                parents.push(defaultP);

            Object layerP = new MyDexClassLoader(this, dexPath, cc.getFilesDir().getAbsolutePath(),
                    (ClassLoader) sm.get()); //4:the parent class loader
            //Object layerP = new DexClassLoader(dexPath, path, path, Payload.class.getClassLoader());
            //Object layerP = new MyDexClassLoader(this, dexPath, path,
            //        Payload.class.getClassLoader()); //4:the parent class loader
            //Object layerP = new MyDexClassLoader(this, dexPath, dexPath,
            //        Payload.class.getClassLoader()); //4:the parent class loader
            if (!parents.peek().equals(layerP)) {
                parents.push(layerP);
                CLs.push((ClassLoader) layerP);
            }
            sm.set(parents.peek());

            loadResources();

/*            final Field pathListField = ShareReflectUtil.findField(defaultCL, "pathList");
            final Object originPathList = pathListField.get(defaultCL);
 */           //apkActivity = getClassLoader().loadClass(MyConfig.APK1_ACTIVITY_MAIN);
            //apkUtils = getClassLoader().loadClass(MyConfig.APK1_UTILS);
        } catch (Exception e) {
            Log.e(TAG, "onCreate catch " + e.toString());
        }
    }

    // Remove classLoader from loaded apk and recovery
    public void RemoveApk() {
        Log.d(TAG, "RemoveApk()");
        try {
            if (CLs.size() > 1) {
                CLs.pop();
                parents.pop();
            }
            Smith<Object> sm = new Smith<>(CLs.peek());
            sm.set(parents.peek());
        } catch (Exception e) {
            Log.e(TAG, "RemoveApk : " + e.toString());
            if (e instanceof IllegalArgumentException)
                return;
        }

        mTheme = null;
        mResources = null;
        mAssetManager = null;
    }

    // Load resources from apks
    protected void loadResources() throws InstantiationException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, NoSuchMethodException, NoSuchFieldException {
        Log.d(TAG, "MyApplication : loadResources()");
        AssetManager am = (AssetManager) AssetManager.class.newInstance();
        am.getClass().getMethod("addAssetPath", String.class).invoke(am, dexPath);
        mAssetManager = am;
        Constructor<?> constructor_Resources = Resources.class.getConstructor(AssetManager.class, cc.getResources()
                .getDisplayMetrics().getClass(), cc.getResources().getConfiguration().getClass());
        mResources = (Resources) constructor_Resources.newInstance(am, cc.getResources().getDisplayMetrics(), cc.getResources().getConfiguration());
        mTheme = mResources.newTheme();
        mTheme.applyStyle(android.R.style.Theme_Light_NoTitleBar_Fullscreen, true);
    }

    public void createPath() {
        File file;
        if (getExternalFilesDir(null) != null) {
            file = getExternalFilesDir(null);
        } else {
            file = getFilesDir();
        }

        if (!file.exists()) {
            file.mkdir();
        }
    }

    @Override
    public ClassLoader getClassLoader() {
        if (CLs == null || CLs.size() == 0) {
            Log.d(TAG, "super.getClassLoader()");
            return super.getClassLoader();
        }
        Log.d(TAG, "It's in the " + CLs.size() + " layer");
        return CLs.peek();
    }

    @Override
    public AssetManager getAssets() {
        Log.e(TAG, mAssetManager == null ? "super.getAssets()" : "mAssetManager");
        return mAssetManager == null ? super.getAssets() : mAssetManager;
    }

    @Override
    public Resources getResources() {
        Log.e(TAG, mResources == null ? "super.getResources()" : "mResources");
        return mResources == null ? super.getResources() : mResources;

    }

    @Override
    public Resources.Theme getTheme() {
        Log.e(TAG, mTheme == null ? "super.getTheme()" : "mTheme");
        return mTheme == null ? super.getTheme() : mTheme;
    }
}