package com.couchbase.todo.libreria;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.SavedRevision;
import com.couchbase.lite.UnsavedRevision;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by ricardosalcedotrejo on 5/9/17.
 */

public class CouchbaseList {
    public SavedRevision createList(Database database, String name, String username) throws CouchbaseLiteException {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("type", "list");
        properties.put("name", name);

        String docId = username + "." + UUID.randomUUID();

        Document document = database.getDocument(docId);
        return document.putProperties(properties);
    }

    private void deleteList(final Document list) {
        try {
            list.delete();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    public boolean update(UnsavedRevision newRevision, String name) {
        Map<String, Object> props = newRevision.getUserProperties();
        props.put("name", name);
        newRevision.setUserProperties(props);
        return true;
    }
}
