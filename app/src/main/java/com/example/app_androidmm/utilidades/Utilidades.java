package com.example.app_androidmm.utilidades;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.*;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.example.app_androidmm.database.Usuario;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.SearchResultSnippet;
import com.google.api.services.youtube.model.Thumbnail;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.regex.Pattern;

import static androidx.core.content.ContextCompat.startActivity;

/**
 * @author Jose
 */
public class Utilidades {
    private static final String TAG = "Utilidades.java";
    public static final int REQUEST_IMAGE_GALLERY = 1;
    public static final int REQUEST_IMAGE_CAMERA = 2;
    public static final int REQUEST_IMAGE_FILES = 3;
    public static final int PERMISSION_REQUEST_INTERNET = 4;
    public static final int PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 5;
    public static final int REQUEST_CODE_SELECT_IMAGE = 6;

    // Método para obtener el trailer de una película
    public static String getTrailer(String title) {
        String API_KEY = "AIzaSyCx3cO-BR_E9WqV87hCVks4WOJ8yW-9hxg"; // API Key de YouTube
        try {
            YouTube youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), request -> {
            })
                    .setApplicationName("MovieModerna")
                    .build();

            YouTube.Search.List searchRequest = youtube.search().list("snippet");
            searchRequest.setKey(API_KEY);
            searchRequest.setQ(title); // Título del video que deseas buscar
            searchRequest.setType("video");
            searchRequest.setMaxResults(1L);

            SearchListResponse searchResponse = searchRequest.execute();
            List<SearchResult> searchResults = searchResponse.getItems();

            if (searchResults != null && !searchResults.isEmpty()) {
                SearchResult videoResult = searchResults.get(0);
                SearchResultSnippet snippet = videoResult.getSnippet();
                Thumbnail thumbnail = snippet.getThumbnails().getDefault();
                String videoId = videoResult.getId().getVideoId();
                String videoLink = "https://www.youtube.com/watch?v=" + videoId;

                return videoLink;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "No hay trailer de esta película";
    }

    public static void openDrawer(DrawerLayout drawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public static void closeDrawer(DrawerLayout drawerLayout) {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    public static void redirectActivity(Activity activity, Class secondActivity) {
        Intent intent = new Intent(activity, secondActivity);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }
    // Método para compartir los datos relevantes de la película
    public static void compartirPelicula(Context context, String imageUrl, String title, String description, String actor, String genero, String director, String plataforma, Date fechapublicacion) {
        LocalDate localDate = dateutilToLocalDate(fechapublicacion);
        int anio = localDate.getYear();
        // Crear el Intent para compartir
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        // Se define el buscador de youtube
        String buscador = title + "pelicula";
        buscador += " trailer español " + anio;
        Log.d(TAG, buscador);
        // Agregar los datos a compartir
        shareIntent.putExtra(Intent.EXTRA_TEXT, "¡Hey! Te recomiendo ver esta película:" +
                "\nTítulo: " + title +
                "\nDescripción: " + description +
                "\nActor principal: " + actor +
                "\nGénero: " + genero +
                "\nDirector: " + director +
                "\nPlataforma de Streaming: " + plataforma +
                "\nAño de estreno: " + anio +
                "\nTrailer: " + getTrailer(buscador) +
                "\nURL de la imagen: " + imageUrl +
                "\nPuedes visitar MovieModerna si quieres encontrar información acerca de esta y más películas ;) [link]");

        try {
            // Iniciar la actividad de compartir
            context.startActivity(Intent.createChooser(shareIntent, "Compartir película"));
        } catch (Exception e) {
            // Mostrar un mensaje de error en caso de que no se pueda compartir
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            mostrarErrorCampo(context, e.getMessage(), "Error");
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

    public static void descargarYCompartirImagen(String TAG, Context context, String imageUrl, String title, String description, String actor, String genero, String director, String plataforma, Date fechapublicacion) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    // Simplemente realizar la llamada al método compartirPeliculaPrueba con la URL directamente
                    compartirPelicula(context, imageUrl, title, description, actor, genero, director, plataforma, fechapublicacion);
                } catch (Exception e) {
                    Log.e(TAG, "Error al compartir la película: " + e.getMessage());
                }
                return null;
            }
        }.execute();
    }

    public static void uploadImageToFirebase(Uri imageUri, Usuario user, String TAG) {
        // Crear una referencia al almacenamiento de Firebase y especificar la ubicación y el nombre del archivo
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("avatar/" + imageUri.getLastPathSegment());

        // Subir el archivo al almacenamiento de Firebase
        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // La imagen se ha subido exitosamente

                    // Obtener la URL de descarga del archivo
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
//                        String imageUrl = uri.toString();
//                        Log.d(TAG, "URL de descarga: " + imageUrl);
//                        user.setAvatar(imageUrl);

                        // Aquí puedes realizar cualquier acción adicional que dependa de la URL de la imagen
                        // Por ejemplo, guardar el usuario en la base de datos o mostrar un mensaje de éxito.
                    }).addOnFailureListener(e -> {
                        // Error al obtener la URL de descarga
                        Log.e(TAG, "Error al obtener la URL de descarga: " + e.getMessage());
                    });

                })
                .addOnFailureListener(e -> {
                    // Error al subir la imagen a Firebase Storage
                    Log.e(TAG, "Error al subir la imagen a Firebase: " + e.getMessage());
                });
    }


    // Metodo para pasar un date util a local date
    public static LocalDate dateutilToLocalDate(Date fechaPublicacion) {
        // Convierte la fecha de SQL a Instant
        Instant instant = Instant.ofEpochMilli(fechaPublicacion.getTime());

        // Crea una zona horaria (puedes ajustarla según tus necesidades)
        ZoneId zonaHoraria = ZoneId.systemDefault();

        // Crea un objeto LocalDate a partir del Instant y la zona horaria
        LocalDate fechaLocal = instant.atZone(zonaHoraria).toLocalDate();

        return fechaLocal;
    }
    // Metodo para pasar un date sql a local date
    public static LocalDate datesqlToLocalDate(java.sql.Date fechaPublicacion) {
        // Convierte la fecha de SQL a Instant
        Instant instant = Instant.ofEpochMilli(fechaPublicacion.getTime());

        // Crea una zona horaria (puedes ajustarla según tus necesidades)
        ZoneId zonaHoraria = ZoneId.systemDefault();

        // Crea un objeto LocalDate a partir del Instant y la zona horaria
        LocalDate fechaLocal = instant.atZone(zonaHoraria).toLocalDate();

        return fechaLocal;
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

    // Variable miembro para almacenar la URI de la imagen capturada por la cámara
    public static Uri photoUri;

    // Método para elegir cómo subir tu foto de perfil
    public static void showImagePickerDialog(Context context, Activity activity) {
        // Verificar permisos de la cámara
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, REQUEST_IMAGE_CAMERA);
            return;
        }

        // Intent para capturar la imagen desde la cámara
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try {
            photoFile = createImageFile(context);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        if (photoFile != null) {
            photoUri = FileProvider.getUriForFile(context, "com.example.app.fileprovider", photoFile);
            takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        }

        // Intent para seleccionar una imagen de la galería
        Intent pickPhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
        pickPhotoIntent.setType("image/*");

        // Crear el intent de elección para la cámara
        Intent cameraChooserIntent = Intent.createChooser(takePhotoIntent, "Tomar foto");

        // Agregar el intent de la galería al intent de elección
        cameraChooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickPhotoIntent});

        // Iniciar la actividad con el intent de elección
        activity.startActivityForResult(cameraChooserIntent, REQUEST_CODE_SELECT_IMAGE);
    }

    // Método para crear la foto tomada por la cámara, que se utilizará posteriormente para el registro
    public static File createImageFile(Context context) throws IOException {
        // Crear un archivo con nombre único para guardar la imagen capturada
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    // Método para mostrar la imagen seleccionada
    public static void mostrarImagenSeleccionada(Context context, Uri uri, ImageView imgavatar) {
        try {
            Bitmap bitmap;
            if (uri.getScheme().equals("content")) {
                // Si la URI es de la galería, obtener el Bitmap de la imagen seleccionada
                bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
            } else {
                // Si la URI es de la cámara, obtener el Bitmap del archivo de imagen
                bitmap = BitmapFactory.decodeFile(uri.getPath());
            }

            // Calcular las dimensiones del ImageView
            int targetWidth = imgavatar.getWidth();
            int targetHeight = imgavatar.getHeight();

            // Redimensionar el Bitmap para ajustarlo al tamaño del ImageView sin cambiar su resolución
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true);

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
