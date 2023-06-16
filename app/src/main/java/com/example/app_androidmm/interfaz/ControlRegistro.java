package com.example.app_androidmm.interfaz;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import com.example.app_androidmm.R;
import com.example.app_androidmm.database.*;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;


import static com.example.app_androidmm.utilidades.Utilidades.*;
import static java.lang.String.join;


public class ControlRegistro extends AppCompatActivity {

    private static String TAG = "RegistroControl";
    private String alias, nombre, apellidos, pass, passConfirm, email, pregunta, respuesta;
    private EditText edalias, ednombre, edapellidos, edpass, edpassConfirm, edemail, edrespuesta;
    private Date[] dtFechaNac = new Date[1];
    private Spinner cbPreguntas;
    private Button btnCambiarFoto, btnRegistro;
    private ImageView imgavatar;
    private ImageButton imageButton;
    private TextView textViewFecha;
    private Calendar calendar;
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private HashMap<String, String> datosUser = new HashMap<>();
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private String urlImagen = "https://firebasestorage.googleapis.com/v0/b/moviemoderna.appspot.com/o/avatar%2Fuser.jpg?alt=media&token=8191eb8a-a28d-45b7-80b2-20a859d7dcc8";
    private Usuario user = Usuario.getInstance();
    private Uri imageUri;
    private ConnectionManager connectionManager = new ConnectionManager();

    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pagina_registro);
        // Definir atributos
        imgavatar = findViewById(R.id.imgAvatarC);
        loadImageFromUrl(urlImagen, imgavatar);

        user.setAvatar(urlImagen); // Restablecer la URL por defecto mientras se sube la imagen

        // Cambiar foto con formato correcto
        btnCambiarFoto = findViewById(R.id.btnCambiarAvatar);
        btnCambiarFoto.setOnClickListener(view -> {
            // Lanzar el selector de imágenes
            showImagePickerDialog(this, this);
        });
        // Seleccionar fecha de nacimiento
        imageButton = findViewById(R.id.imageButton);
        textViewFecha = findViewById(R.id.viewFecha);

        calendar = Calendar.getInstance();

        imageButton.setOnClickListener(v -> mostrarDateDialog(ControlRegistro.this, calendar, dtFechaNac, textViewFecha, dateFormat));

        btnRegistro = findViewById(R.id.btnRegistrarse);
        btnRegistro.setOnClickListener(view -> {
            edalias = findViewById(R.id.txtAlias);
            alias = edalias.getText().toString();
            ednombre = findViewById(R.id.txtNombre);
            nombre = ednombre.getText().toString();
            edapellidos = findViewById(R.id.txtApellidos);
            apellidos = edapellidos.getText().toString();
            edpass = findViewById(R.id.txtPass);
            pass = edpass.getText().toString();
            edpassConfirm = findViewById(R.id.txtPassConfirm);
            passConfirm = edpassConfirm.getText().toString();
            edemail = findViewById(R.id.txtCorreo);
            email = edemail.getText().toString();
            cbPreguntas = findViewById(R.id.cbPreguntas);
            pregunta = cbPreguntas.getSelectedItem().toString();
            edrespuesta = findViewById(R.id.txtRespuesta);
            respuesta = edrespuesta.getText().toString();
            String fecha = "";
            if (alias.isEmpty() || pass.isEmpty() || nombre.isEmpty()
                    || apellidos == null || email.isEmpty() || dtFechaNac[0] == null || respuesta.isEmpty()) {
                System.out.println("Algún campo vacío");
                mostrarErrorCampo(ControlRegistro.this, "Algún campo vacío", "Error en la introducción de datos");
            } else if (!pass.equals(passConfirm)) {
                mostrarErrorCampo(ControlRegistro.this, "Las contraseñas no coinciden.", "Error en la introducción de datos");
            } else if (pass.equals(passConfirm)) {
                if (apellidos == null) {
                    mostrarErrorCampo(ControlRegistro.this, "Apellidos vacios", "Error en la introducción de datos");
                } else {
                    datosUser.put("alias", alias);
                    datosUser.put("pass", pass);
                    datosUser.put("nombre", nombre);
                    datosUser.put("apellidos", apellidos);
                    datosUser.put("email", email);
                    fecha = dateFormat.format(dtFechaNac[0]);
                    System.out.println(datosUser.toString());
                    if (validarFormulario(datosUser, ControlRegistro.this)) {
                        user.setAlias(alias);
                        user.setPass(pass);
                        user.setNombre(nombre);
                        user.setApellidos(apellidos);
                        user.setEmail(email);
                        user.setFechaNacimiento(dtFechaNac[0]);
                        user.setPregunta(pregunta);
                        user.setRespuesta(respuesta);
                        // Aqui iria el set del avatar con nuevos metodos
                        System.out.println(user.toString());
                        System.out.println("Pregunta: " + pregunta);
                        //iniciarConsulta

                        String finalFecha = fecha;
                        new Thread(() -> {
                            // Llamar al método executeQuery desde aquí
                            String query = "INSERT INTO usuario (alias, contrasena, nombre, apellidos, fechanace, email, avatar, pregunta, respuesta) VALUES ('" +
                                    alias + "','" + user.getPass() + "','" + nombre + "','" + apellidos + "','" + finalFecha + "','" + email + "','" + user.getAvatar() + "','" + pregunta + "','" + respuesta + "')";
                            connectionManager.executeQuery(query, new ConnectionManager.QueryCallback() {
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
                                            Log.d(TAG, "Usuario: " + user.toString());
                                            Log.d("LATER QUERY", photoUri.toString());
                                            mostrarErrorCampo(ControlRegistro.this, "La cuenta se ha creado con éxito", "Creación de cuenta");
                                            Intent intent = new Intent(ControlRegistro.this, ControlBienvenido.class);
                                            startActivity(intent);
                                            photoUri = null;
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
                    }
                }
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_SELECT_IMAGE) {
                if (data != null && data.getData() != null) {
                    // Mostrar imagen seleccionada desde archivos
                    imageUri = data.getData();
                    mostrarImagenSeleccionada(this, imageUri, imgavatar);
                    user.setAvatar(imageUri.toString());
                    Log.d("RESULT", imageUri.toString());
                }else {
                    // Mostrar imagen tomada con la cámara
                    mostrarImagenSeleccionada(this,photoUri,imgavatar);
                    user.setAvatar(photoUri.toString());
                    Log.d("RESULT", photoUri.toString());
                }


            }
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_IMAGE_GALLERY) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_IMAGE_GALLERY);
            } else {
                Toast.makeText(this, "Permiso denegado. No se puede acceder a la galería.", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_IMAGE_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_IMAGE_CAMERA);
            } else {
                Toast.makeText(this, "Permiso denegado. No se puede acceder a la cámara.", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_IMAGE_FILES) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_IMAGE_FILES);
            } else {
                Toast.makeText(this, "Permiso denegado. No se puede acceder a los archivos.", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
