package com.example.app_androidmm;

import com.example.app_androidmm.database.ConnectionManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {Config.OLDEST_SDK})

public class ConnectionTest {

    private ConnectionManager connectionManager;

    @Before
    public void setUp() {
        connectionManager = new ConnectionManager();
    }

    @After
    public void tearDown() {
        connectionManager.shutdown();
    }

    @Test
    public void testExecuteQuery_SuccessfulQuery_ResultSetNotNull() {
        String query = "SELECT * FROM usuario";
        connectionManager.executeQuery(query, new ConnectionManager.QueryCallback() {
            @Override
            public void onQueryCompleted(ResultSet resultSet, int rowsAffected) {
                assertNotNull(resultSet);
                // Pueden haber más comprobaciones
            }

            @Override
            public void onQueryFailed(String error) {
                fail("Query incorrecta: " + error);
            }
        });
    }

    @Test
    public void testExecuteQuery_FailedQuery_ErrorNotNull() {
        // Utilizo un select porque no me interesa realizar un update a la base de datos en un test
        String query = "SELECT * FROM noTablaa";
        connectionManager.executeQuery(query, new ConnectionManager.QueryCallback() {
            @Override
            public void onQueryCompleted(ResultSet resultSet, int rowsAffected) {
                fail("Query incorrecta pero se ha completado bien!");
            }

            @Override
            public void onQueryFailed(String error) {
                assertNotNull(error);
                // Pueden haber más comprobaciones
            }
        });
    }

    /* Los errores pueden deberse a que realizo algo complejo para conectarme, al tener que utilizar multihilo, he sobreescrito el metodo onQueryCompleted
    Al hacer esto recibo un CallBack que y se utiliza para verificar si se trata de una query de consulta (select) o de modificacion (update, insert),
    Si se trata de este último, el resultSet no devuelve nada y solo devuelve el número de filas afectadas por el update o insert */
}