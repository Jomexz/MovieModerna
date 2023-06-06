package com.example.app_androidmm.interfaz;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import com.example.app_androidmm.R;
import com.example.app_androidmm.database.ConnectionManager;
import com.example.app_androidmm.database.Usuario;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.example.app_androidmm.utilidades.Utilidades.*;

public class ControlConfig extends AppCompatActivity {

    private static final int REQUEST_CODE_SELECT_IMAGE = 1;
    private static final String TAG = "ControlConfig";
    Button btnAvatar, btnConfigurar;
    ImageView imgAvatarConfig;
    EditText txtAliasConfig, txtNombreConfig, txtApellidosconfig, txtPassConfig, txtConfirmPassConfig;
    String alias, nombre, apellidos, pass, newPass;
    ConnectionManager connectionManager = new ConnectionManager();
    Usuario user = Usuario.getInstance();

    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pagina_config);
        imgAvatarConfig = findViewById(R.id.imgAvatarC);
        txtAliasConfig = findViewById(R.id.aliasEditText);
        txtNombreConfig = findViewById(R.id.nombreEditText);
        txtApellidosconfig = findViewById(R.id.apellidosEditText);
        txtPassConfig = findViewById(R.id.contrasenaNuevaEditText);
        txtConfirmPassConfig = findViewById(R.id.confirmarContrasenaEditText);
        btnAvatar = findViewById(R.id.changeAvatarButton);
        btnConfigurar = findViewById(R.id.guardarCambiosButton);
//        bytesToImageView(user.getAvatar(),imgAvatarConfig);
        btnAvatar.setOnClickListener(view -> seleccionarImagen(ControlConfig.this, REQUEST_CODE_SELECT_IMAGE));

        btnConfigurar.setOnClickListener(view -> {
            alias = txtAliasConfig.getText().toString();
            nombre = txtNombreConfig.getText().toString();
            apellidos = txtApellidosconfig.getText().toString();
            pass = txtPassConfig.getText().toString();
            newPass = txtConfirmPassConfig.getText().toString();
            // Crear lista para verificar datos
            HashMap<String, String> datos = new HashMap<>();
            datos.put("alias", alias);
            datos.put("pass", pass);
            datos.put("nombre", nombre);
            datos.put("apellidos", apellidos);
            datos.put("email", "");
            // Crear la consulta SQL para actualizar los datos del usuario
            StringBuilder sqlBuilder = new StringBuilder("UPDATE usuario SET");

            // Verificar y agregar los campos que se deben actualizar
            if (validarFormulario(datos, ControlConfig.this)) {
                List<String> updateFields = new ArrayList<>();
                if (!alias.isEmpty()) {
                    updateFields.add("alias = '" + alias + "'");
                }
                if (!nombre.isEmpty()) {
                    updateFields.add("nombre = '" + nombre + "'");
                }
                if (!apellidos.isEmpty()) {
                    updateFields.add("apellidos = '" + apellidos + "'");
                }
                if (!pass.isEmpty() && (pass.equals(newPass))) {
                    updateFields.add("contrasena = '" + pass + "'");
                }

                // Combinar los campos en la consulta SQL
                for (int i = 0; i < updateFields.size(); i++) {
                    sqlBuilder.append(" ").append(updateFields.get(i));
                    if (i < updateFields.size() - 1) {
                        sqlBuilder.append(",");
                    }
                }
            }
            System.out.println(sqlBuilder);
            new Thread(() -> {
                // Llamar al método executeQuery desde aquí
                connectionManager.executeQuery(String.valueOf(sqlBuilder), new ConnectionManager.QueryCallback() {
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
                                mostrarErrorCampo(ControlConfig.this, "La cuenta se ha actualizado con éxito", "Configuración de cuenta");
                                Intent intent = new Intent(ControlConfig.this, ControlBienvenido.class);
                                startActivity(intent);
                            }
                        } catch (SQLException e) {
                            Log.e(TAG, "Error al procesar los resultados: " + e.getMessage());
                        }
                    }

                    @Override
                    public void onQueryFailed(String error) {
                        // Manejar el error de la consulta
                        Log.e(TAG, error);
                    }
                });
            }).start();

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_SELECT_IMAGE) {
                if (data != null) {
                    Uri selectedImageUri = data.getData();
                    if (selectedImageUri != null) {
                        mostrarImagenSeleccionada(ControlConfig.this, selectedImageUri, imgAvatarConfig);
                    } else {
                        // Manejar el caso en el que la URI sea nula
                        Toast.makeText(ControlConfig.this, "No se pudo obtener la imagen seleccionada", Toast.LENGTH_SHORT).show();
                    }
                }
            } else if (requestCode == REQUEST_IMAGE_CAMERA) {
                // Obtener la URI de la imagen capturada desde el archivo de preferencias compartidas
                SharedPreferences sharedPreferences = getSharedPreferences("captured_image", Context.MODE_PRIVATE);
                String capturedImageUriString = sharedPreferences.getString("captured_image_uri", null);

                if (capturedImageUriString != null) {
                    Uri capturedImageUri = Uri.parse(capturedImageUriString);
                    mostrarImagenSeleccionada(ControlConfig.this, capturedImageUri, imgAvatarConfig);
                } else {
                    // Manejar el caso en el que la URI de la imagen capturada sea nula
                    Toast.makeText(ControlConfig.this, "No se pudo obtener la imagen capturada", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private Uri getCapturedImageUri() {
        File imageFile = createImageFile();
        if (imageFile != null) {
            return FileProvider.getUriForFile(ControlConfig.this, "com.example.app_androidmm.fileprovider", imageFile);
        } else {
            return null;
        }
    }

    private File createImageFile() {
        // Crea un archivo para guardar la imagen capturada
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + ".jpg";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try {
            File imageFile = File.createTempFile(imageFileName, ".jpg", storageDir);
            // Guarda la ruta del archivo en una variable global si necesitas acceder a ella posteriormente
            // imagePath = imageFile.getAbsolutePath();
            return imageFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


}
