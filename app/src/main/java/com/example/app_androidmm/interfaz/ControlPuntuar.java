package com.example.app_androidmm.interfaz;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.app_androidmm.R;
import com.example.app_androidmm.database.ConnectionManager;
import com.example.app_androidmm.database.Pelicula;
import com.example.app_androidmm.database.Usuario;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.example.app_androidmm.utilidades.Utilidades.mostrarErrorCampo;
import static com.example.app_androidmm.utilidades.Utilidades.validaAnio;

public class ControlPuntuar extends AppCompatActivity {
    private AdaptadorPuntuar adaptadorPuntuar;
    private static final int PERMISSION_REQUEST_EXTERNAL_STORAGE = 1;
    static final String TAG = "PuntuarControl";
    ImageButton btnBuscar;
    EditText buscador;
    Spinner condiciones;
    ConnectionManager connectionManager = new ConnectionManager();
    Pelicula pelicula = new Pelicula();
    Usuario user = Usuario.getInstance();
    List<Pelicula> listaPeliculas;

    RecyclerView recyclerView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pagina_puntuar);
        btnBuscar = findViewById(R.id.btnBuscarP);
        buscador = findViewById(R.id.txtSearchP);
        condiciones = findViewById(R.id.spinnerSearchByP);
        listaPeliculas = new ArrayList();
        recyclerView = findViewById(R.id.recyclerView);
        btnBuscar.setOnClickListener(view -> {
            String busca = buscador.getText().toString();
            if (!busca.isEmpty()) {
                // Obtener el criterio de búsqueda seleccionado
                String criterio = condiciones.getSelectedItem().toString();

                // Construir la consulta base
                String query = "SELECT * FROM pelicula WHERE fechapublicacion <= CURRENT_DATE";

                // Verificar el criterio de búsqueda seleccionado
                switch (criterio) {
                    case "POR TÍTULO":
                        query += " AND titulo LIKE '%" + busca +"%'";
                        break;
                    case "POR DIRECTOR":
                        query += " AND director LIKE '%" + busca +"%'";
                        break;
                    case "POR PROTAGONISTA":
                        query += " AND protagonista LIKE '%" + busca +"%'";
                        break;
                    case "POR AÑO DE ESTRENO":
                        // Verificar si se ingresó un año de búsqueda válido
                        if (validaAnio(busca)) {
                            query += " AND EXTRACT(YEAR FROM fechapublicacion) = '" + busca +"'";
                        } else {
                            Toast.makeText(ControlPuntuar.this, "Año introducido incorrecto.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        break;
                }

                String finalQuery = query;
                new Thread(() -> {
                    connectionManager.executeQuery(finalQuery, new ConnectionManager.QueryCallback() {
                        @Override
                        public void onQueryCompleted(ResultSet resultSet, int rowsAffected) {
                            runOnUiThread(() -> {
                                if (adaptadorPuntuar != null) {
                                    adaptadorPuntuar.clearViews();
                                    adaptadorPuntuar.notifyDataSetChanged();
                                }
                            });
                            boolean resultados = false;
                            try {
                                while (resultSet.next()) {
                                    Pelicula p = new Pelicula();
                                    p.setPkPelicula(resultSet.getInt("pkpelicula"));
                                    p.setTitulo(resultSet.getString("titulo"));
                                    p.setDescripcion(resultSet.getString("descripcion"));
                                    p.setRating((float) resultSet.getDouble("rating"));
                                    p.setImagen(resultSet.getString("imagen"));
                                    p.setGenero(resultSet.getString("genero"));
                                    p.setCalificacion(resultSet.getString("calificacion"));
                                    p.setDirector(resultSet.getString("director"));
                                    p.setFechaPublicacion(resultSet.getDate("fechapublicacion"));
                                    p.setProtagonista(resultSet.getString("protagonista"));
                                    p.setPlataforma(resultSet.getString("plataforma"));
                                    Log.d(TAG, p.toString());
                                    listaPeliculas.add(p);
                                    resultados = true;
                                }
                                System.out.println("Peliculas cargadas: " + rowsAffected);
                                boolean finalResultados = resultados;
                                runOnUiThread(() -> {
                                    if (finalResultados) {

                                        adaptadorPuntuar = new AdaptadorPuntuar(listaPeliculas);
                                        recyclerView.setHasFixedSize(true);
                                        recyclerView.setLayoutManager(new LinearLayoutManager(ControlPuntuar.this));

                                        adaptadorPuntuar.setOnRatingClickListener((position, rating) -> {
                                            pelicula = listaPeliculas.get(position);
                                            pelicula.setRating(rating);
                                            // Imprimir la calificación por pantalla para comprobar que se ha calificado correctamente
                                            System.out.println("Puntuacion: " + rating);
                                        });

                                        adaptadorPuntuar.setOnCalificarClickListener((position, rating) -> {
                                            // Manejo el evento de clic en el botón "CALIFICAR" aquí
                                            pelicula = listaPeliculas.get(position);
                                            pelicula.setRating(rating);
                                            // Imprimir la calificación por pantalla para comprobar que se ha calificado correctamente
                                            System.out.println("Calificación desde el botón CALIFICAR: " + rating);
                                            float ratingEscalado = rating / (float) 5 * 10; // Valor a escalado a una puntuación sobre 10
                                            // AQUI IRIA EL UPDATE DE LA TABLA VISUALIZACION, AÑADIENDO LA CALIFICACION DEL USUARIO EN DICHA PELICULA
                                            System.out.println("Calificación escaladada: " + ratingEscalado);
                                            new Thread(() -> {
                                                connectionManager.executeQuery("insert into visualizacion (akusuario, fecha, ratingvista, akpelicula) values (" + user.getPkUsuario() +
                                                        ", CURRENT_DATE, " + ratingEscalado + ", " + pelicula.getPkPelicula() + ")", new ConnectionManager.QueryCallback() {
                                                    @Override
                                                    public void onQueryCompleted(ResultSet resultSet, int rowsAffected) {
                                                        System.out.println("Antes de puntuar");
                                                        try {
                                                            if (resultSet != null) {
                                                                // Procesar los resultados de la consulta
                                                                while (resultSet.next()) {
                                                                    int akuser = resultSet.getInt("akusuario");
                                                                    int akpelicula = resultSet.getInt("akpelicula");
                                                                    float rating = resultSet.getFloat("ratingvista");

                                                                    // Realizar cualquier acción con los datos obtenidos
                                                                    Log.d(TAG, "Usuario: " + akuser + ", Pelicula: " + akpelicula + "Rating: " + rating);
                                                                }
                                                            } else {
                                                                // Manejar casos de inserción o actualización (rowsAffected contiene el número de filas afectadas)
                                                                Log.d(TAG, "Filas afectadas: " + rowsAffected);
                                                                Toast.makeText(ControlPuntuar.this, "La película se ha puntuado con éxito. Puntuación: " + rating, Toast.LENGTH_SHORT).show();
                                                            }
                                                        } catch (SQLException e) {
                                                            Log.e(TAG, "Error al procesar los resultados: " + e.getMessage());
                                                        }
                                                        System.out.println("Después de puntuar");
                                                    }

                                                    @Override
                                                    public void onQueryFailed(String error) {
                                                        Log.e(TAG, "Error al procesar los resultados: " + error);
                                                        Toast.makeText(ControlPuntuar.this, "Calificación: " + rating, Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }).start();
                                        });
                                        recyclerView.setAdapter(adaptadorPuntuar);
                                        adaptadorPuntuar.notifyDataSetChanged();
                                    } else {
                                        Toast.makeText(ControlPuntuar.this, "No se encontraron películas.", Toast.LENGTH_SHORT).show();
                                    }
                                });
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
                Toast.makeText(ControlPuntuar.this, "Por favor, ingrese un término de búsqueda.", Toast.LENGTH_SHORT).show();
            }

        });


    }
}
