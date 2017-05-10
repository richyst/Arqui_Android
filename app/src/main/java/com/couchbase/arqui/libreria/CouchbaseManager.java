package com.couchbase.arqui.libreria;

import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;

import java.io.IOException;

/**
 *
 *
 * Esta clase tiene como unico proposito la creación de un manager para la operación en una base de
 * datos que se creará con CouchbaseDatabase de este paquete o independientemente de este paquete.
 *
 * @author A01337828 - A01336386 -  A01337375
 */

public class CouchbaseManager {

    /**
     * Método para la creación de un Manager de Couchbase
     *
     * @param dbname Se envía la base de datos donde se está operando que se creó en CouchbaseDatabase
     * @param context Contexto de Android sobre el ambiente de la aplicación.
     * @return Regresa un Manager de Couchbase con opciones default.
     */
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
