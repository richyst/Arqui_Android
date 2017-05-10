package com.couchbase.todo.libreria;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseOptions;
import com.couchbase.lite.Manager;

/**
 * Created by ricardosalcedotrejo on 5/9/17.
 */

public class CouchbaseDatabase {

    public static Database openDatabase(String dbname, Manager manager){
        DatabaseOptions options = new DatabaseOptions();
        options.setCreate(true);
        Database database = null;

        try {
            return database = manager.openDatabase(dbname, options);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

        return database;

    }
}
