package com.example.app_androidmm.interfaz;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.drawerlayout.widget.DrawerLayout;
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
    private DrawerLayout drawerLayout;
    private ImageView menu, navAvatar;
    private TextView navUser, navNombre;
    private LinearLayout home, settings, info, logout, share;
    private static final int REQUEST_CODE_SELECT_IMAGE = 1;
    private static final String TAG = "ControlConfig";
    private Button btnAvatar, btnConfigurar;
    private ImageView imgAvatarConfig;
    private EditText txtAliasConfig, txtNombreConfig, txtApellidosconfig, txtPassConfig, txtConfirmPassConfig;
    private String alias, nombre, apellidos, pass, newPass;
    private ConnectionManager connectionManager = new ConnectionManager();
    private Usuario user = Usuario.getInstance();
    private Uri imageUri;
    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pagina_config);

        drawerLayout = findViewById(R.id.drawer_layout);
        menu = findViewById(R.id.navigation_menu);
        home = findViewById(R.id.ly_home);
        settings = findViewById(R.id.settings);
        info = findViewById(R.id.info);
        logout = findViewById(R.id.exit);
        share = findViewById(R.id.share);
        navAvatar = findViewById(R.id.nav_avatar);
        navUser = findViewById(R.id.nav_user);
        navNombre = findViewById(R.id.nav_nameuser);
        loadImageFromUrl(user.getAvatar(),navAvatar);
        navUser.setText(user.getAlias());
        navNombre.setText(user.getNombre() + " " + user.getApellidos());

        imgAvatarConfig = findViewById(R.id.imgAvatarC);
        loadImageFromUrl(user.getAvatar(),imgAvatarConfig);
        txtAliasConfig = findViewById(R.id.aliasEditText);
        txtNombreConfig = findViewById(R.id.nombreEditText);
        txtApellidosconfig = findViewById(R.id.apellidosEditText);
        txtPassConfig = findViewById(R.id.contrasenaNuevaEditText);
        txtConfirmPassConfig = findViewById(R.id.confirmarContrasenaEditText);
        btnAvatar = findViewById(R.id.changeAvatarButton);
        btnConfigurar = findViewById(R.id.guardarCambiosButton);
//        bytesToImageView(user.getAvatar(),imgAvatarConfig);
        btnAvatar.setOnClickListener(view -> showImagePickerDialog(this,this));

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
                                if (imageUri != null) {
                                    uploadImageToFirebase(imageUri, user, TAG);
                                } else if (photoUri != null) {
                                    uploadImageToFirebase(photoUri, user, TAG);
                                }
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
        menu.setOnClickListener(view -> {
            openDrawer(drawerLayout);
        });

        home.setOnClickListener(view -> {
            redirectActivity(this,ControlBienvenido.class);
        });

        settings.setOnClickListener(view -> {
            recreate();
        });

        info.setOnClickListener(view -> {
            redirectActivity(this, ControlInfo.class);
        });

        logout.setOnClickListener(view -> {
            Toast.makeText(this,"Has cerrado sesión correctamente", Toast.LENGTH_SHORT);
            user = null; // Borramos los datos del usuario
            redirectActivity(this, MainActivity.class);

        });

        share.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, "¿Quieres disfrutar de una experiencia cinematográfica única junto a tus amigos? " +
                    "Descubre películas increíbles y genera recomendaciones personalizadas con MovieModerna. ¡Explora el mundo del cine y comparte tus descubrimientos!" +
                    " Haz que cada noche de cine sea especial. Descarga MovieModerna ahora mismo: [link]");
            intent.setType("text/plain");
            if(intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Toast.makeText(this,"No hay permisos", Toast.LENGTH_SHORT);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(drawerLayout);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_GALLERY || requestCode == REQUEST_IMAGE_CAMERA || requestCode == REQUEST_IMAGE_FILES) {
                if (data != null && data.getData() != null) {
                    imageUri = data.getData();
//                    imgavatar.setImageURI(imageUri);
                    mostrarImagenSeleccionada(this,imageUri,imgAvatarConfig);
                } else if (photoUri != null) {
//                    imgavatar.setImageURI(photoUri);
                    mostrarImagenSeleccionada(this,photoUri,imgAvatarConfig);
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
