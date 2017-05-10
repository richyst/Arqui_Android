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

/**
 * Esta clase es un permite crear, eliminar, actualizar para usuarios.
 *
 * @author A01337828 - A01336386 -  A01337375
 */
public class CouchbaseUser {



    /**
     * Método para la creación de una nueva lista contenedora de Usuarios.
     *
     * @param name Nombre que tendrá el usuario por crear.
     * @param password Password perteneciente al usuario por crear.
     * @param list Lista a la que pertenece el usuario, creada con CouchbaseList de este paquete.
     * @param database Base de Datos donde se va a guardar el usuario.
     * @return Regresa un SavedRevision del nuevo usuario.
     */
    public static SavedRevision createUser(String name, String password, Document list, Database database) {
        Map<String, Object> ListInfo = new HashMap<String, Object>();
        ListInfo.put("id", list.getId());

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("type", "user");
        properties.put("list", ListInfo);
        properties.put("createdAt", new Date());
        properties.put("name", name);
        properties.put("password", password);

        Document document = database.createDocument();
        try {
            return document.putProperties(properties);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Método para la eliminación de un usuario existente.
     *
     * @param user Documento de usuario que se va a eliminar.
     */
    public static void deleteUser(final Document user) {
        try {
            user.delete();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método para la actualización del nombre de un usuario.
     *
     * @param user Usuario cuyo nombre se quiere cambiar.
     * @param prop Propiedad del usuario que se quiere cambiar. (Se le deja escoger al usuario cual propiedad modificar pero puede crear confusiones si no se usa con cuidado.)
     * @param val Valor que se le asignará a la propiedad que se quiere cambiar.
     */
    public static void updateUser(final Document user, String prop, String val) {
        Map<String, Object> updatedProperties = new HashMap<String, Object>();
        updatedProperties.putAll(user.getProperties());
        updatedProperties.put(prop, val);

        try {
            user.putProperties(updatedProperties);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }


}
