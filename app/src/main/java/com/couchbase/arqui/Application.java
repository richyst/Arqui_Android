package com.couchbase.arqui;

import android.content.Intent;
import android.os.StrictMode;
import android.util.Log;

import com.couchbase.lite.Database;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;
import com.facebook.stetho.Stetho;
import com.robotpajamas.stetho.couchbase.CouchbaseInspectorModulesProvider;

import com.couchbase.arqui.libreria.CouchbaseDatabase;
import com.couchbase.arqui.libreria.CouchbaseManager;

import java.io.IOException;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class Application extends android.app.Application {
    public static final String TAG = "Todo";
    public static final String LOGIN_FLOW_ENABLED = "login_flow_enabled";

    private Boolean mLoginFlowEnabled = false;

    public Database getDatabase() {
        return database;
    }

    private Manager manager;
    private Database database;

    private String mUsername;

    @Override
    public void onCreate() {
        super.onCreate();

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        if (BuildConfig.DEBUG) {
            Stetho.initialize(
                    Stetho.newInitializerBuilder(this)
                            .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                            .enableWebKitInspector(new CouchbaseInspectorModulesProvider(this))
                            .build());
        }
        startSession("test");

        try {
            manager = new Manager(new AndroidContext(getApplicationContext()), Manager.DEFAULT_OPTIONS);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Logging

    private void enableLogging() {
        Manager.enableLogging(TAG, Log.VERBOSE);
        Manager.enableLogging(com.couchbase.lite.util.Log.TAG, Log.VERBOSE);
        Manager.enableLogging(com.couchbase.lite.util.Log.TAG_SYNC_ASYNC_TASK, Log.VERBOSE);
        Manager.enableLogging(com.couchbase.lite.util.Log.TAG_SYNC, Log.VERBOSE);
        Manager.enableLogging(com.couchbase.lite.util.Log.TAG_QUERY, Log.VERBOSE);
        Manager.enableLogging(com.couchbase.lite.util.Log.TAG_VIEW, Log.VERBOSE);
        Manager.enableLogging(com.couchbase.lite.util.Log.TAG_DATABASE, Log.VERBOSE);
    }

    // Session


    private void startSession(String username) {
        enableLogging();
        AndroidContext context = new AndroidContext(getApplicationContext());
        manager= CouchbaseManager.createManager(username, context);
        database=CouchbaseDatabase.openDatabase(username,manager);
        mUsername = username;
        showApp();
    }

    //Metodo para crear nueva base de datos (pueden ser infinitas)

    private void showApp() {
        Intent intent = new Intent();
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(getApplicationContext(), ListsActivity.class);
        intent.putExtra(LOGIN_FLOW_ENABLED, mLoginFlowEnabled);
        startActivity(intent);
    }

    public String getUsername() {
        return mUsername;
    }

}
