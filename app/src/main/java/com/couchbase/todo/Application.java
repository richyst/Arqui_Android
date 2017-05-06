package com.couchbase.todo;

import android.content.Intent;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import com.couchbase.lite.Attachment;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseOptions;
import com.couchbase.lite.Document;
import com.couchbase.lite.DocumentChange;
import com.couchbase.lite.LiveQuery;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;
import com.facebook.stetho.Stetho;
import com.robotpajamas.stetho.couchbase.CouchbaseInspectorModulesProvider;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static java.lang.Math.min;

public class Application extends android.app.Application {
    public static final String TAG = "Todo";
    public static final String LOGIN_FLOW_ENABLED = "login_flow_enabled";

    private Boolean mLoginFlowEnabled = false;
    private Boolean mEncryptionEnabled = false;

    public Database getDatabase() {
        return database;
    }

    private Manager manager;
    private Database database;
    private ArrayList<Document> accessDocuments = new ArrayList<Document>();

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
        startSession("todo");

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
        openDatabase(username);
        mUsername = username;
        showApp();
    }

    //Metodo para crear nueva base de datos (pueden ser infinitas)
    private void openDatabase(String username) {
        String dbname = username;
        DatabaseOptions options = new DatabaseOptions();
        options.setCreate(true);

        Manager manager = null;
        try {
            manager = new Manager(new AndroidContext(getApplicationContext()), Manager.DEFAULT_OPTIONS);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            database = manager.openDatabase(dbname, options);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

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
