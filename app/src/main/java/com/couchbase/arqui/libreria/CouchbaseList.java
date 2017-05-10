package com.couchbase.arqui.libreria;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.SavedRevision;
import com.couchbase.lite.UnsavedRevision;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 *
 *
 * Esta clase es un permite crear, consultar, eliminar y generar vistas para listas de usuarios.
 *
 * @author A01337828 - A01336386 -  A01337375
 */
public class CouchbaseList {

    /**
     * Método para la creación de una nueva lista contenedora de Usuarios.
     *
     * @param database Se envía la base de datos donde se está operando que se creó en CouchbaseDatabase
     * @param name String que va a tener la lista en el atributo name
     * @param username Username con el que se está operando la aplicación
     * @return Regresa un SavedRevision de la nueva lista
     */
    public static SavedRevision createList(Database database, String name, String username) throws CouchbaseLiteException {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("type", "list");
        properties.put("name", name);

        String docId = username + "." + UUID.randomUUID();

        Document document = database.getDocument(docId);
        return document.putProperties(properties);
    }

    /**
     * Método para la eliminación de la lista deseada creada anteriormente.
     *
     * @param list Se envía la base de datos donde se está operando que se creó en CouchbaseDatabase
     *
     */
    public static void deleteList(final Document list) {
        try {
            list.delete();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método para la eliminación de la lista deseada creada anteriormente.
     *
     * @param test String del nombre de la lista que se va a recuperar
     * @param database Se envía la base de datos donde se está operando que se creó en CouchbaseDatabase
     * @return Se regresa la lista como Documento para poder usarla más adelante
     */
    public static Document getList(String test, Database database){
        return database.getDocument(test);
    }

    /**
     * Método para la eliminación de la lista deseada creada anteriormente.
     *
     * @param test String del nombre de la lista que se va a usar para agrupar documentos.
     * @param database Se envía la base de datos donde se está operando que se creó en CouchbaseDatabase
     * @return Se regresa la agrupación de datos de acuerdo a como el usuario la haya solicitado.
     */
    public static com.couchbase.lite.View getVista(Database database, String test){
        return database.getView(test);
    }
}
