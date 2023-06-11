package com.example.app_androidmm.interfaz;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.*;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.example.app_androidmm.R;
import com.example.app_androidmm.database.*;

import java.sql.*;

import static com.example.app_androidmm.utilidades.Utilidades.*;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Button btnEntrar, btnRegistro, btnRecuperar;
    private EditText edUser, edpass;
    private String alias, pass;
    private ConnectionManager connectionManager = new ConnectionManager();
    String userName = "", passUser = "";

    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btnEntrar = findViewById(R.id.loginButton);
        btnEntrar.setOnClickListener(view -> {

            edUser = findViewById(R.id.usernameEditText);
            edpass = findViewById(R.id.passwordEditText);
            alias = edUser.getText().toString();
            pass = edpass.getText().toString();

            if (alias.isEmpty() || pass.isEmpty()) {
                mostrarErrorCampo(this, "Algún campo vacío.", "Error en la introducción de datos");
                return;
            }
            Usuario user = Usuario.getInstance();

            // Llamar al método executeQuery desde aquí
            Long tiempo = System.currentTimeMillis();
            connectionManager.executeQuery("SELECT * FROM usuario where alias like '" + alias + "'", new ConnectionManager.QueryCallback() {
                @Override
                public void onQueryCompleted(ResultSet resultSet, int rowsAffected) {
                    try {
                        // Procesar los resultados de la consulta
                        while (resultSet.next()) {
                            userName = resultSet.getString("alias");
                            passUser = resultSet.getString("contrasena");
                            user.setAlias(userName);
                            user.setPass(passUser);
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
                            // Verificar la contraseña
                            if (pass.equals(passUser)) {
                                System.out.println("Se ha iniciado sesión");
                                Intent intent = new Intent(MainActivity.this, ControlBienvenido.class);
                                startActivity(intent);
//                                            runOnUiThread(new Runnable() {
//                                                @Override
//                                                public void run() {
//                                                    System.out.println("Se ha iniciado sesión");
//                                                    Intent intent = new Intent(MainActivity.this, BienvenidoControl.class);
//                                                    startActivity(intent);
//                                                }
//                                            });
                            } else {
                                System.out.println("No se ha inciado sesión");
                                mostrarErrorCampo(MainActivity.this, "Contraseña o usuario incorrectos.", "Error en la introducción de datos");
//                                            runOnUiThread(new Runnable() {
//                                                @Override
//                                                public void run() {
//                                                    System.out.println("No se ha inciado sesión");
//                                                    mostrarErrorCampo(MainActivity.this, "Contraseña o usuario incorrectos.", "Error en la introducción de datos");
//                                                }
//                                            });
                            }
                        }
                    } catch (SQLException e) {
                        Log.e(TAG, "Error al procesar los resultados: " + e.getMessage());
                    }
                    System.out.println("Tiempo hilo: " + (System.currentTimeMillis() - tiempo));
                }

                @Override
                public void onQueryFailed(String error) {
                    // Manejar el error de la consulta
                    Log.e(TAG, error);
                }
            });
            System.out.println("Tiempo fuera:" + (System.currentTimeMillis() - tiempo));


        });

        btnRegistro = findViewById(R.id.registerButton);
        btnRegistro.setOnClickListener(view -> {
            redirectActivity(this, ControlRegistro.class);
        });

        btnRecuperar = findViewById(R.id.forgotPasswordButton);
        btnRecuperar.setOnClickListener(view -> {
            redirectActivity(this, ControlRecuperar.class);
        });
    }


}