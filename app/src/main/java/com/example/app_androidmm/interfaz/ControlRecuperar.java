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
    private String alias, respuesta, pregunta;
    private Button btnValidar, btnCambiarPass;
    private EditText edalias, edrespuesta, newPass, newPassRepit;
    private Spinner spPreguntas;
    private ConnectionManager connectionManager = new ConnectionManager();
    private Usuario user = new Usuario();
    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pagina_recuperar);

        edalias = findViewById(R.id.txtUsuario);
        alias = edalias.getText().toString();
        btnValidar = findViewById(R.id.btnValidar);
        btnCambiarPass = findViewById(R.id.btnCambiar);
        edrespuesta = findViewById(R.id.txtRespuesta);
        newPass = findViewById(R.id.txtNewPass);
        newPassRepit = findViewById(R.id.txtNewPassRepit);
        spPreguntas = findViewById(R.id.cbPreguntas);

        btnValidar.setOnClickListener(view -> {
            respuesta = edrespuesta.getText().toString();
            pregunta = spPreguntas.getSelectedItem().toString();

            if (!respuesta.isEmpty() && !alias.isEmpty()) {
                // Crear una instancia de UsuarioQuery que implementa la interfaz Query
                Usuario.UsuarioQuery usuarioQuery = new Usuario.UsuarioQuery(alias);

                // Ejecutar la consulta utilizando el ConnectionManager de Firebase
                connectionManager.executeQuery(usuarioQuery, new ConnectionManager.QueryCallback<Usuario>() {
                    @Override
                    public void onQueryCompleted(Usuario userSesion) {
                        user.actualizarDatos(userSesion);
                        if (userSesion != null && pregunta.equals(userSesion.getPregunta()) && respuesta.equals(userSesion.getRespuesta())) {
                            btnCambiarPass.setEnabled(true);
                        } else {
                            System.out.println("No se encontró un usuario con el alias proporcionado");
                            // Manejar el caso cuando no se encuentra un usuario con el alias proporcionado
                        }
                    }

                    @Override
                    public void onQueryFailed(String error) {
                        // Manejar el error de la consulta
                        Log.e(TAG, error);
                    }
                });

            } else {
                Toast.makeText(this, "No se ha introducido respuesta o usuario", Toast.LENGTH_SHORT).show();
            }
        });

        btnCambiarPass.setOnClickListener(view -> {
            if (newPass.getText().toString().equals(newPassRepit.getText().toString())) {
                HashMap<String, String> pass = new HashMap<>();
                pass.put("pass", newPass.getText().toString());
                if (validarFormulario(pass, this)) {
                    // Llamar al método para actualizar o insertar datos
                    connectionManager.updateOrInsertData("usuarios", alias, user, new ConnectionManager.UpdateOrInsertCallback() {
                        @Override
                        public void onUpdateOrInsertCompleted() {
                            // La actualización o inserción fue exitosa
                            Log.d(TAG, "Datos actualizados o insertados correctamente en Firebase");

                            mostrarErrorCampo(ControlRecuperar.this,"Datos modificados correctamente.","Configuración de datos");
                        }

                        @Override
                        public void onUpdateOrInsertFailed(String error) {
                            // Ocurrió un error al actualizar o insertar los datos
                            Log.e(TAG, "Error al actualizar o insertar datos en Firebase: " + error);

                            mostrarErrorCampo(ControlRecuperar.this,error,"Recuperación de datos fallida");
                        }
                    });
                }
            }
        });
    }

}
