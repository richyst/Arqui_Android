package com.couchbase.arqui.libreria;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseOptions;
import com.couchbase.lite.Manager;

/**
 *
 *
 * Esta clase crea una base de datos de Couchbase con el string que se recibe por el usuario como
 * nombre de la base de datos. En caso de que la base de datos ya exista solo se abrirá la base
 * existente.
 *
 * @author A01337828 - A01336386 -  A01337375
 */

public class CouchbaseDatabase {

    /**
     * Método para la creación o abertura de una base de datos de Couchbase a partir de un Manager
     * creado anteriormente.
     *
     * @param dbname Se envía la base de datos donde se está operando que se creó en CouchbaseDatabase.
     * @param manager Manager que se pudo crear con CouchbaseManager o independientemente del paquete pero que sea del tipo Manager de Couchbase.
     * @return Se regresa una base de datos sin llave de encripción.
     */
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
