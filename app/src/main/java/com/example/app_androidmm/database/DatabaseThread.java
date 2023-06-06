package com.example.app_androidmm.database;

import android.os.Handler;

public class DatabaseThread extends Thread {
    final String URL = "jdbc:postgresql://rogue.db.elephantsql.com:5432/leenvhud";
    final String USER = "leenvhud";
    final String PASS = "VciE8aoGHK7o1vQDfVZWo-TKQh5_nXFs";
    private static final String TAG = "DatabaseThread";
    private Handler mainThreadHandler;
    private String alias, pass;


    public DatabaseThread(Handler mainThreadHandler, String alias, String pass) {
        this.mainThreadHandler = mainThreadHandler;
        this.alias = alias;
        this.pass = pass;
    }

    @Override
    public void run() {
        /*Usuario user = Usuario.getInstance();
        // Aquí se realiza la consulta a la base de datos en un hilo separado
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(URL, USER, PASS);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Conexion exitosa!");
            getUsuario(alias, pass, connection);
            System.out.println(user.toString());

            // Enviar los datos al hilo principal para su actualización
            mainThreadHandler.post(() -> {
                // Realizar cualquier acción en el hilo principal con los datos obtenidos
                // por ejemplo, actualizar la interfaz de usuario
                // ...
            });*/
    }

}
