package com.example.app_androidmm.utilidades;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.*;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.app_androidmm.database.ConnectionManager;
import com.example.app_androidmm.database.Pelicula;
import com.example.app_androidmm.database.Usuario;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.regex.Pattern;

import static com.example.app_androidmm.interfaz.ControlRegistro.*;

/**
 * @author Jose
 */
public class Utilidades {

    public static final int REQUEST_IMAGE_GALLERY = 1;
    public static final int REQUEST_IMAGE_CAMERA = 2;
    public static final int REQUEST_IMAGE_FILES = 3;
    public static final int REQUEST_IMAGE_PICKER = 1;

    public static void compartirPelicula(Context context, Bitmap bitmap, String title, String description, String actor, String genero, String director, String plataforma) {
        // Guardar el bitmap en el almacenamiento local
        Uri imageUri = saveImageToStorage(context, bitmap);

        if (imageUri != null) {
            // Crear el Intent para compartir
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/*");

            // Agregar los datos a compartir
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Título: " + title +
                    "\nDescripción: " + description +
                    "\nActor principal: " + actor +
                    "\nGénero: " + genero +
                    "\nDirector: " + director +
                    "\nPlataforma de Streaming: " + plataforma);
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);

            try {
                // Iniciar la actividad de compartir
                context.startActivity(Intent.createChooser(shareIntent, "Compartir película"));
            } catch (Exception e) {
                // Mostrar un mensaje de error en caso de que no se pueda compartir
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                mostrarErrorCampo(context, e.getMessage(), "Error");
            }
        }
    }


    public static Uri saveImageToStorage(Context context, Bitmap bitmap) {
        // Verificar y crear el directorio si no existe
        File directory = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "AppAndroid_MM");

        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Crear un nombre único para el archivo de imagen
        String fileName = "shared_image.jpg";

        // Crear el archivo de imagen
        File file = new File(directory, fileName);

        try {
            // Guardar el bitmap en el archivo
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();

            // Obtener la Uri del archivo de imagen
            return Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void uploadImageToFirebase(Uri imageUri, Usuario user, String TAG) {
        // Crear una referencia al almacenamiento de Firebase y especificar la ubicación y el nombre del archivo (CAMBIAR POR images/ EN CASO DE FALLO
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("avatar/" + imageUri.getLastPathSegment());

        // Subir el archivo al almacenamiento de Firebase
        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // La imagen se ha subido exitosamente
                    // Obtener la URL de descarga del archivo
                    storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageUrl = uri.toString();
                            user.setAvatar(imageUrl);
                        }
                    });
                })
                .addOnFailureListener(e -> {
                    // Error al subir la imagen a Firebase Storage
                    Log.e(TAG, "Error al subir la imagen a Firebase: " + e.getMessage());
                });
    }
    // Metodo para pasar un date a local date
    public static LocalDate dateToLocalDate (java.sql.Date fechaPublicacion) {
        // Convierte la fecha de SQL a Instant
        Instant instant = Instant.ofEpochMilli(fechaPublicacion.getTime());

        // Crea una zona horaria (puedes ajustarla según tus necesidades)
        ZoneId zonaHoraria = ZoneId.systemDefault();

        // Crea un objeto LocalDate a partir del Instant y la zona horaria
        LocalDate fechaLocal = instant.atZone(zonaHoraria).toLocalDate();

        return fechaLocal;
    }

    public static byte[] imageViewToBytes(ImageView imageView) {
        // Obtener el bitmap del ImageView
        Bitmap bitmap = imageView.getDrawingCache();

        if (bitmap == null) {
            imageView.setDrawingCacheEnabled(true);
            imageView.buildDrawingCache();
            bitmap = Bitmap.createBitmap(imageView.getDrawingCache());
            imageView.setDrawingCacheEnabled(false);
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }


    public static String imageViewToHexBytes(ImageView imageView) {
        // Obtener el bitmap del ImageView
        Bitmap bitmap = imageView.getDrawingCache();

        if (bitmap == null) {
            imageView.setDrawingCacheEnabled(true);
            imageView.buildDrawingCache();
            bitmap = Bitmap.createBitmap(imageView.getDrawingCache());
            imageView.setDrawingCacheEnabled(false);
        }

        return bitmapToHexBytes(bitmap);
    }

    private static String bitmapToHexBytes(Bitmap bitmap) {
        // Comprimir el bitmap en formato PNG sin pérdida de calidad
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

        // Obtener los bytes resultantes del bitmap
        byte[] bytes = stream.toByteArray();

        // Convertir los bytes a formato hexadecimal
        StringBuilder hexBuilder = new StringBuilder();
        for (byte b : bytes) {
            String hex = String.format("%02X", b);
            hexBuilder.append(hex);
        }

        return hexBuilder.toString();
    }




    // Metodo para validar los valores del formulario
    public static boolean validarFormulario(HashMap<String, String> datosUsuario, Context context) {

        HashMap<String, String> condicionesUsuario = new HashMap<String, String>() {
            {
                put("alias", "^(?=.{5,12}$)[^\\s]+$"); // Alias sin espacios, con más de 5 caracteres y menos de 12
                put("pass", "^(?=.*[!@#$%^&*(),.?\\\":{}|<>])[A-Za-z0-9!@#$%^&*(),.?\\\":{}|<>]{12,}$"); // Pass con 12 o más caracteres y que deba contener algún símbolo
                put("nombre", "^[A-Za-zñÑáÁéÉíÍóÓúÚ\\s]*[A-Za-zñÑáÁéÉíÍóÓúÚ][A-Za-zñÑáÁéÉíÍóÓúÚ\\s]*$"); // Nombre o nombres sin símbolos ni números
                put("apellidos", "^[A-Za-zñÑáÁéÉíÍóÓúÚ\\s]+$"); // Apellido o apellidos sin símbolos ni números
                put("email", "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"); // Correo electrónico común
            }
        };

        boolean alMenosUnCampoValido = false;

        for (String campo : condicionesUsuario.keySet()) {
            if (datosUsuario.containsKey(campo)) {
                String valor = datosUsuario.get(campo);
                String patron = condicionesUsuario.get(campo);
                String mensajeError = "El campo " + campo + " es incorrecto.";

                if (!valor.isEmpty()) {
                    alMenosUnCampoValido = true;

                    if (patron != null && !Pattern.matches(patron, valor)) {
                        System.out.println(mensajeError);
                        if (campo.equals("alias")) {
                            mensajeError += " Recuerda que los nombres de usuario deben tener 5 caracteres como mínimo.";
                        } else if (campo.equals("pass")) {
                            mensajeError += " Recuerda que las contraseñas deben tener 12 caracteres como mínimo y tener algún símbolo.";
                        }
                        mostrarErrorCampo(context, mensajeError, "Error en la introducción de datos");
                        return false;
                    }
                }
            }
        }

        if (!alMenosUnCampoValido) {
            mostrarErrorCampo(context, "Debe introducir al menos un dato.", "Error en la introducción de datos");
            return false;
        }

        return true;
    }


    // Metodo para cargar imagen desde una url
    public static void loadImageFromUrl(String imageUrl, ImageView imageView) {
        Picasso.get()
                .load(imageUrl)
                .into(imageView);
    }

    // Metodo para cargar imagen desde una url a un imagebutton
    public static void loadimageButtonFromUrl(String url, ImageButton imageButton) {
        Picasso.get()
                .load(url)
                .into(imageButton);
    }

    // Metodo para mostrar un alert de error
    public static void mostrarErrorCampo(Context context, String mensaje, String titulo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(titulo)
                .setMessage(mensaje)
                .setPositiveButton("Aceptar", null)
                .show();
    }

    public static void seleccionarImagen(Activity activity, int requestCode) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Intent chooserIntent = Intent.createChooser(intent, "Seleccionar imagen");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{takePhotoIntent});

        activity.startActivityForResult(chooserIntent, requestCode);
    }



    public static void showImagePickerDialog(Context context, Activity activity) {
        final CharSequence[] options = {"Galería", "Fotos", "Cámara", "Archivos"};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Seleccionar imagen desde")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_IMAGE_GALLERY);
                            } else {
                                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                activity.startActivityForResult(intent, REQUEST_IMAGE_GALLERY);
                            }
                        } else if (which == 1) {
                            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            activity.startActivityForResult(intent, REQUEST_IMAGE_GALLERY);
                        } else if (which == 2) {
                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, REQUEST_IMAGE_CAMERA);
                            } else {
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                activity.startActivityForResult(intent, REQUEST_IMAGE_CAMERA);
                            }
                        } else if (which == 3) {
                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_IMAGE_FILES);
                            } else {
                                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                intent.setType("image/*");
                                activity.startActivityForResult(intent, REQUEST_IMAGE_FILES);
                            }
                        }
                    }
                })
                .show();
    }

    public static void mostrarImagenSeleccionada(Context context, Uri uri, ImageView imgavatar) {
        try {
            // Obtener el Bitmap de la imagen seleccionada
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);

            // Redimensionar el Bitmap si es necesario
            Bitmap resizedBitmap = redimensionarBitmap(bitmap, 500, 500);

            // Mostrar el Bitmap en el ImageView
            imgavatar.setImageBitmap(resizedBitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Bitmap redimensionarBitmap(Bitmap bitmap, int nuevoAncho, int nuevoAlto) {
        int ancho = bitmap.getWidth();
        int alto = bitmap.getHeight();

        float escalaAncho = ((float) nuevoAncho) / ancho;
        float escalaAlto = ((float) nuevoAlto) / alto;

        Matrix matrix = new Matrix();
        matrix.postScale(escalaAncho, escalaAlto);

        return Bitmap.createBitmap(bitmap, 0, 0, ancho, alto, matrix, false);
    }

    public static void mostrarDateDialog(Context context, Calendar calendar, Date[] dtFechaNac, TextView textViewFecha, DateFormat dateFormat) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                if (dtFechaNac[0] == null) {
                    dtFechaNac[0] = new Date(); // Inicializar con la fecha actual si es nulo
                }

                dtFechaNac[0].setTime(calendar.getTimeInMillis());
                String formattedDate = dateFormat.format(dtFechaNac[0]);
                textViewFecha.setText(textViewFecha.getText() + formattedDate);
            }
        }, year, month, day);

        datePickerDialog.show();
    }

    // Metodo para validar que un año sea verdadero
    public static boolean validaAnio(String anio) {
        try {
            int year = Integer.parseInt(anio);
            // Verificar si el año está dentro del rango aceptable, por ejemplo, entre 1900 y el año actual
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            return year >= 1895 && year <= currentYear;
        } catch (NumberFormatException e) {
            return false; // El valor ingresado no es un número válido
        }
    }


}
