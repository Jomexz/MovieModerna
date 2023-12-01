package com.example.app_androidmm.database;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import com.google.firebase.database.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConnectionManager {
    private static final int THREAD_POOL_SIZE = 5;
    private static final int MESSAGE_QUERY = 1;

    private ExecutorService executorService;
    private HandlerThread handlerThread;
    private Handler handler;

    private DatabaseReference databaseReference;

    public ConnectionManager() {
        executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        handlerThread = new HandlerThread("ConnectionHandlerThread");
        handlerThread.start();

        handler = new Handler(handlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MESSAGE_QUERY) {
                    QueryTask queryTask = (QueryTask) msg.obj;
                    executeQuery(queryTask);
                }
            }
        };

        // Obtener la referencia de la base de datos de Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public <T> void executeQuery(Query<T> query, QueryCallback<T> callback) {
        QueryTask<T> queryTask = new QueryTask<>(query, callback);
        handler.sendMessage(handler.obtainMessage(MESSAGE_QUERY, queryTask));
    }

    private <T> void executeQuery(QueryTask<T> queryTask) {
        // Realizar operaciones en la base de datos de Firebase
        // Aquí deberías implementar lógica específica para tu aplicación

        // Ejemplo: Obtener datos de Firebase
        databaseReference.child(queryTask.query.getNode()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Manejar los datos obtenidos
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        // Procesar los resultados de la consulta
                        T result = snapshot.getValue(queryTask.query.getDataClass());

                        // Notificar al callback
                        queryTask.callback.onQueryCompleted(result);
                    }
                } else {
                    // No se encontraron datos para la consulta
                    queryTask.callback.onQueryCompleted(null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Manejar errores
                queryTask.callback.onQueryFailed(databaseError.getMessage());
            }
        });
    }

    public void shutdown() {
        executorService.shutdown();
        handlerThread.quit();
    }

    private static class QueryTask<T> {
        Query<T> query;
        QueryCallback<T> callback;

        public QueryTask(Query<T> query, QueryCallback<T> callback) {
            this.query = query;
            this.callback = callback;
        }
    }

    public interface Query<T> {
        String getNode(); // Obtener el nodo en Firebase
        Class<T> getDataClass(); // Obtener la clase de datos para deserialización
    }

    public interface QueryCallback<T> {
        void onQueryCompleted(T result); // Notificar cuando la consulta se haya completado
        void onQueryFailed(String error); // Notificar si la consulta falla
    }

    public <T> void updateOrInsertData(String node, String key, T data, UpdateOrInsertCallback callback) {
        DatabaseReference dataRef = databaseReference.child(node).child(key);

        // Actualizar o insertar datos
        dataRef.setValue(data)
                .addOnSuccessListener(aVoid -> callback.onUpdateOrInsertCompleted())
                .addOnFailureListener(e -> callback.onUpdateOrInsertFailed(e.getMessage()));
    }

    public interface UpdateOrInsertCallback {
        void onUpdateOrInsertCompleted(); // Notificar cuando la actualización o inserción se haya completado
        void onUpdateOrInsertFailed(String error); // Notificar si la actualización o inserción falla
    }
}
