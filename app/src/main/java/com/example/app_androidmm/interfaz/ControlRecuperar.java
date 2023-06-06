package com.example.app_androidmm.interfaz;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.app_androidmm.R;
import com.example.app_androidmm.database.ConnectionManager;
import com.example.app_androidmm.database.Usuario;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.example.app_androidmm.utilidades.Utilidades.mostrarErrorCampo;

public class ControlRecuperar extends AppCompatActivity {
    private static String TAG = "RecuperarControl";
    private Button btnValidar, btnCambiarPass;
    private EditText respuesta, newPass, newPassRepit;
    private Spinner spPreguntas;
    private ConnectionManager connectionManager = new ConnectionManager();
    Usuario user = Usuario.getInstance();

    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pagina_recuperar);

        btnValidar = findViewById(R.id.btnValidar);
        btnCambiarPass = findViewById(R.id.btnCambiar);
        respuesta = findViewById(R.id.txtRespuesta);
        newPass = findViewById(R.id.txtNewPass);
        newPassRepit = findViewById(R.id.txtNewPassRepit);
        spPreguntas = findViewById(R.id.cbPreguntas);

        btnValidar.setOnClickListener(view -> {
            if (!respuesta.getText().toString().isEmpty()) {
                if (respuesta.getText().toString().equals(spPreguntas.getSelectedItem().toString())) {
                    newPass.setEnabled(true);
                    newPassRepit.setEnabled(true);
                    btnCambiarPass.setEnabled(true);
                    if (newPass.getText().toString().equals(newPassRepit.getText().toString())) {
                        new Thread(() -> {
                            connectionManager.executeQuery("update usuario set contrasena='" + newPass.getText().toString() + "' where usuario like '" + user.getAlias(),
                                    new ConnectionManager.QueryCallback() {
                                        @Override
                                        public void onQueryCompleted(ResultSet resultSet, int rowsAffected) {
                                            try {
                                                if (resultSet != null) {
                                                    // Procesar los resultados de la consulta
                                                    while (resultSet.next()) {
                                                        String alias = resultSet.getString("alias");
                                                        String contrasena = resultSet.getString("contrasena");

                                                        // Realizar cualquier acción con los datos obtenidos
                                                        Log.d(TAG, "Usuario: " + alias + ", Pass: " + contrasena);
                                                    }
                                                } else {
                                                    // Manejar casos de inserción o actualización (rowsAffected contiene el número de filas afectadas)
                                                    Log.d(TAG, "Filas afectadas: " + rowsAffected);
                                                    mostrarErrorCampo(ControlRecuperar.this,"La cuenta se ha recuperado con éxito","Recuperación de cuenta");
                                                    Intent intent = new Intent(ControlRecuperar.this, MainActivity.class);
                                                    startActivity(intent);
                                                }
                                            } catch (SQLException e) {
                                                Log.e(TAG, "Error al procesar los resultados: " + e.getMessage());
                                            }
                                        }

                                        @Override
                                        public void onQueryFailed(String error) {
                                            Log.e(TAG, error);
                                        }
                                    });
                        });
                    }
                }
            } else {
                Toast.makeText(this, "No se ha introducido respuesta", Toast.LENGTH_SHORT);
            }
        });
    }
}
