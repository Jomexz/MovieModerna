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
import static com.example.app_androidmm.utilidades.Utilidades.REQUEST_CODE_SELECT_IMAGE;

public class ControlConfig extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ImageView menu, navAvatar;
    private TextView navUser, navNombre;
    private LinearLayout home, settings, info, logout, share;
    private static final String TAG = "ControlConfig";
    private Button btnAvatar, btnConfigurar;
    private ImageView imgAvatarConfig;
    private EditText txtAliasConfig, txtNombreConfig, txtApellidosconfig, txtPassConfig, txtConfirmPassConfig;
    private String alias, nombre, apellidos, pass, confirmPass;
    private ConnectionManager connectionManager = new ConnectionManager();
    private Usuario user = Usuario.getInstance();
    private Uri imageUri;

    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pagina_config);

        final boolean[] cambioFoto = {false};

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
        loadImageFromUrl(user.getAvatar(), navAvatar);
        navUser.setText(user.getAlias());
        navNombre.setText(user.getNombre() + " " + user.getApellidos());

        imgAvatarConfig = findViewById(R.id.imgAvatarC);
        loadImageFromUrl(user.getAvatar(), imgAvatarConfig);
        txtAliasConfig = findViewById(R.id.aliasEditText);
        txtNombreConfig = findViewById(R.id.nombreEditText);
        txtApellidosconfig = findViewById(R.id.apellidosEditText);
        txtPassConfig = findViewById(R.id.contrasenaNuevaEditText);
        txtConfirmPassConfig = findViewById(R.id.confirmarContrasenaEditText);
        btnAvatar = findViewById(R.id.changeAvatarButton);
        btnConfigurar = findViewById(R.id.guardarCambiosButton);
        btnAvatar.setOnClickListener(view -> {
            cambioFoto[0] = true;
            // Lanzar el selector de imágenes
            showImagePickerDialog(this, this);
        });

        btnConfigurar.setOnClickListener(view -> {
            alias = txtAliasConfig.getText().toString();
            nombre = txtNombreConfig.getText().toString();
            apellidos = txtApellidosconfig.getText().toString();
            pass = txtPassConfig.getText().toString();
            confirmPass = txtConfirmPassConfig.getText().toString();
            // Crear lista para verificar datos
            HashMap<String, String> datos = new HashMap<>();
            datos.put("alias", alias);
            datos.put("pass", pass);
            datos.put("nombre", nombre);
            datos.put("apellidos", apellidos);
            datos.put("email", "");

            // Verificar y agregar los campos que se deben actualizar
            if (validarFormulario(datos, ControlConfig.this)) {
                Usuario usuarioActualizado = new Usuario();
                usuarioActualizado.setAlias(alias);
                usuarioActualizado.setNombre(nombre);
                usuarioActualizado.setApellidos(apellidos);
                if(pass.equals(confirmPass)) {
                    usuarioActualizado.setPass(confirmPass);
                } else {
                    mostrarErrorCampo(this,"Las contraseñas introducidas no coinciden.","Contraseña incorrecta");
                }
                if (cambioFoto[0]) {
                    usuarioActualizado.setAvatar(user.getAvatar());
                }
                // Llamar al método para actualizar o insertar datos
                connectionManager.updateOrInsertData("usuarios", user.getAlias(), usuarioActualizado, new ConnectionManager.UpdateOrInsertCallback() {
                    @Override
                    public void onUpdateOrInsertCompleted() {
                        // La actualización o inserción fue exitosa
                        Log.d(TAG, "Datos actualizados o insertados correctamente en Firebase");

                        mostrarErrorCampo(ControlConfig.this,"Datos modificados correctamente.","Configuración de datos");
                    }

                    @Override
                    public void onUpdateOrInsertFailed(String error) {
                        // Ocurrió un error al actualizar o insertar los datos
                        Log.e(TAG, "Error al actualizar o insertar datos en Firebase: " + error);

                        mostrarErrorCampo(ControlConfig.this,error,"Configuración de datos incorrecta");
                    }
                });
            } else {
                // Los datos no son válidos, manejar según tus necesidades
                Log.e(TAG, "Los datos no son válidos. La actualización no se realizará.");
            }

        });

        menu.setOnClickListener(view -> {
            openDrawer(drawerLayout);
        });

        home.setOnClickListener(view -> {
            redirectActivity(this, ControlBienvenido.class);
        });

        settings.setOnClickListener(view -> {
            recreate();
        });

        info.setOnClickListener(view -> {
            redirectActivity(this, ControlInfo.class);
        });

        logout.setOnClickListener(view -> {
            Toast.makeText(this, "Has cerrado sesión correctamente", Toast.LENGTH_SHORT).show();
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
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Toast.makeText(this, "No hay permisos", Toast.LENGTH_SHORT).show();
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
            if (requestCode == REQUEST_CODE_SELECT_IMAGE) {
                if (data != null && data.getData() != null) {
                    // Mostrar imagen seleccionada desde archivos
                    imageUri = data.getData();
                    mostrarImagenSeleccionada(this, imageUri, imgAvatarConfig);
                    uploadImageToFirebase(imageUri,user,TAG);
                    Log.d("RESULT", imageUri.toString());
                } else {
                    // Mostrar imagen tomada con la cámara
                    mostrarImagenSeleccionada(this, photoUri, imgAvatarConfig);
                    uploadImageToFirebase(photoUri,user,TAG);
                    Log.d("RESULT", photoUri.toString());
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
