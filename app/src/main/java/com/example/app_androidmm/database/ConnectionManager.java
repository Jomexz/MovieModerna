package com.example.app_androidmm.database;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConnectionManager {
    private static final int THREAD_POOL_SIZE = 5;
    private static final int MESSAGE_QUERY = 1;

    private ExecutorService executorService;
    private HandlerThread handlerThread;
    private Handler handler;

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
    }

    public void executeQuery(String query, QueryCallback callback) {
        QueryTask queryTask = new QueryTask(query, callback);
        handler.sendMessage(handler.obtainMessage(MESSAGE_QUERY, queryTask));
    }

    private void executeQuery(QueryTask queryTask) {
        try (Connection connection = DriverManager.getConnection(queryTask.URL, queryTask.USER, queryTask.PASS);
             Statement statement = connection.createStatement()) {

            boolean isResultSet = statement.execute(queryTask.query);

            if (isResultSet) {
                try (ResultSet resultSet = statement.getResultSet()) {
                    queryTask.callback.onQueryCompleted(resultSet, -1);

                }
            } else {
                int rowsAffected = statement.getUpdateCount();
                queryTask.callback.onQueryCompleted(null, rowsAffected);
            }
        } catch (SQLException e) {
            queryTask.callback.onQueryFailed(e.getMessage());
        }
    }


    public void shutdown() {
        executorService.shutdown();
        handlerThread.quit();
    }

    private static class QueryTask {
        final String URL = "jdbc:postgresql://rogue.db.elephantsql.com:5432/leenvhud";
        final String USER = "leenvhud";
        final String PASS = "VciE8aoGHK7o1vQDfVZWo-TKQh5_nXFs";
        String query;
        QueryCallback callback;

        public QueryTask(String query, QueryCallback callback) {
            this.query = query;
            this.callback = callback;
        }
    }

    public interface QueryCallback {
        void onQueryCompleted(ResultSet resultSet, int rowsAffected);

        void onQueryFailed(String error);
    }
}
