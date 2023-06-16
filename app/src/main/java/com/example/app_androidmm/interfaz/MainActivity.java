package com.example.app_androidmm.interfaz;

import android.os.Build;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.*;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.app_androidmm.BuildConfig;
import com.example.app_androidmm.R;
import com.example.app_androidmm.database.*;

import java.sql.*;

import static com.example.app_androidmm.interfaz.AdaptadorCompartir.PERMISSION_REQUEST_EXTERNAL_STORAGE;
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
        // Obtener la versión de Android
        int androidVersion = Build.VERSION.SDK_INT;

        // Obtener el nombre de la versión de Android
        String androidVersionName = Build.VERSION.RELEASE;

        System.out.println("Versión de Android: " + androidVersion);
        System.out.println("Nombre de la versión de Android: " + androidVersionName);


        // Verificar permisos de escritura en almacenamiento externo
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Solicitar el permiso de escritura en almacenamiento externo
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_EXTERNAL_STORAGE);
        } else {
            Toast.makeText(MainActivity.this,"PERMISO DE ESCRITURA DENEGADO", Toast.LENGTH_SHORT);
            System.out.println("PERMISO DE ESCRITURA DENEGADO");
        }

        // Verificar permisos de acceso a Internet
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            // Solicitar el permiso de acceso a Internet
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.INTERNET},
                    PERMISSION_REQUEST_INTERNET);
        } else {
            // Permiso ya concedido
            Toast.makeText(MainActivity.this, "Permiso de acceso a Internet concedido", Toast.LENGTH_SHORT).show();
            System.out.println("Permiso de acceso a Internet concedidoo0o0o0o0o0o!");
        }

        // Verificar permisos de acceso a la galería de fotos
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Solicitar el permiso de acceso a la galería de fotos
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            Toast.makeText(MainActivity.this,"PERMISO DE LECTURA DENEGADO", Toast.LENGTH_SHORT);
            System.out.println("PERMISO DE LECTURA DENEGADO");
        }

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
                                redirectActivity(MainActivity.this, ControlBienvenido.class);
                                finish();
                            } else {
                                System.out.println("No se ha inciado sesión");
                                mostrarErrorCampo(MainActivity.this, "Contraseña o usuario incorrectos.", "Error en la introducción de datos");
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
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_INTERNET) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido
//                Toast.makeText(MainActivity.this, "Permiso de acceso a Internet concedido", Toast.LENGTH_SHORT).show();
                System.out.println("Permiso de acceso a Internet concedido");
            } else {
                // Permiso denegado
                Toast.makeText(MainActivity.this, "Permiso de acceso a Internet denegado", Toast.LENGTH_SHORT).show();
                System.out.println("Permiso de acceso a Internet denegado");
            }
        }

    }

}