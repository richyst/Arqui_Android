package com.couchbase.todo.libreria;

/**
 * Created by ricardosalcedotrejo on 5/7/17.
 */

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.SavedRevision;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CouchbaseUser {




    public static SavedRevision createUser(String title, String password, Document list, Database database) {
        Map<String, Object> ListInfo = new HashMap<String, Object>();
        ListInfo.put("id", list.getId());

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("type", "user");
        properties.put("list", ListInfo);
        properties.put("createdAt", new Date());
        properties.put("name", title);
        properties.put("password", password);

        Document document = database.createDocument();
        try {
            return document.putProperties(properties);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void deleteUser(final Document user) {
        try {
            user.delete();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }


    public static void updateUser(final Document user, String name) {
        Map<String, Object> updatedProperties = new HashMap<String, Object>();
        updatedProperties.putAll(user.getProperties());
        updatedProperties.put("name", name);

        try {
            user.putProperties(updatedProperties);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }


}
