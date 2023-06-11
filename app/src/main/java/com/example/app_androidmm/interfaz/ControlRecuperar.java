package com.example.app_androidmm.interfaz;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.app_androidmm.R;
import com.example.app_androidmm.database.ConnectionManager;
import com.example.app_androidmm.database.Usuario;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import static com.example.app_androidmm.utilidades.Utilidades.*;

public class ControlRecuperar extends AppCompatActivity {
    private static String TAG = "RecuperarControl";
    private Button btnValidar, btnCambiarPass;
    private EditText respuesta, newPass, newPassRepit;
    private Spinner spPreguntas;
    private ConnectionManager connectionManager = new ConnectionManager();
    private Usuario user = Usuario.getInstance();

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
            String resp = respuesta.getText().toString();
            String pregunta = spPreguntas.getSelectedItem().toString();

            if (!resp.isEmpty()) {
                new Thread(() -> {
                    connectionManager.executeQuery("select * from usuario where pregunta like '" + pregunta + "' and respuesta like '" + resp + "'", new ConnectionManager.QueryCallback() {
                        @Override
                        public void onQueryCompleted(ResultSet resultSet, int rowsAffected) {
                            boolean resultados = false;
                            try {
                                while (resultSet.next()) {
                                    user.setAlias(resultSet.getString("alias"));
                                    user.setPass(resultSet.getString("contrasena"));
                                    user.setPkUsuario(resultSet.getInt("pkusuario"));
                                    user.setEmail(resultSet.getString("email"));
                                    user.setNombre(resultSet.getString("nombre"));
                                    user.setApellidos(resultSet.getString("apellidos"));
                                    user.setFechaNacimiento(resultSet.getDate("fechanace"));
                                    user.setVerificado(resultSet.getBoolean("verificado"));
                                    user.setPregunta(resultSet.getString("pregunta"));
                                    user.setRespuesta(resultSet.getString("respuesta"));
                                    user.setAvatar((resultSet.getString("avatar")));
                                    Log.d(TAG, user.toString());
                                    resultados = true;
                                }
                                if (resultados && pregunta.equals(user.getPregunta()) && resp.equalsIgnoreCase(user.getRespuesta())) {
                                    System.out.println("Entra en el if!!!!");
                                    newPass.setEnabled(true);
                                    newPassRepit.setEnabled(true);
                                    btnCambiarPass.setEnabled(true);

                                } else {
                                    Toast.makeText(ControlRecuperar.this, "No existe usuario con esta pregunta y respuesta de recuperación", Toast.LENGTH_SHORT);
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
                }).start();

            } else {
                Toast.makeText(this, "No se ha introducido respuesta", Toast.LENGTH_SHORT);
            }
        });

        btnCambiarPass.setOnClickListener(view -> {
            if (newPass.getText().toString().equals(newPassRepit.getText().toString())) {
                HashMap<String, String> pass = new HashMap<>();
                pass.put("pass", newPass.getText().toString());
                if (validarFormulario(pass, this)) {
                    new Thread(() -> {
                        connectionManager.executeQuery("update usuario set contrasena='" + newPass.getText().toString() + "' where alias like '" + user.getAlias() + "'",
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
                                                mostrarErrorCampo(ControlRecuperar.this, "La cuenta se ha recuperado con éxito", "Recuperación de cuenta");
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
                    }).start();
                }
            }
        });
    }

}
