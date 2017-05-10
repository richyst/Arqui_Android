package com.couchbase.todo.libreria;

import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;

import java.io.IOException;

/**
 * Created by ricardosalcedotrejo on 5/9/17.
 */

public class CouchbaseManager {


    public static Manager createManager(String dbname, AndroidContext context){
        Manager manager = null;
        try {
            manager = new Manager(context, Manager.DEFAULT_OPTIONS);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return manager;
    }

}
