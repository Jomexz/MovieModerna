package com.example.app_androidmm.interfaz;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.app_androidmm.R;
import com.example.app_androidmm.database.ConnectionManager;
import com.example.app_androidmm.database.Pelicula;
import com.squareup.picasso.Picasso;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.example.app_androidmm.utilidades.Utilidades.compartirPelicula;
import static com.example.app_androidmm.utilidades.Utilidades.validaAnio;

public class ControlRecomendar extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_EXTERNAL_STORAGE = 1;
    static final String TAG = "RecomendarControl";
    ImageButton btnBuscar;
    EditText buscador;
    Spinner condiciones;
    ConnectionManager connectionManager = new ConnectionManager();
    Pelicula pelicula = Pelicula.getInstance();
    List<Pelicula> peliculas;

    RecyclerView recyclerView;
    int position;

    @SuppressLint({"WrongThread", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pagina_recomendar);
        buscador = findViewById(R.id.txtSearchP);
        condiciones = findViewById(R.id.spinnerSearchByP);
        btnBuscar = findViewById(R.id.btnBuscarP);
        peliculas = new ArrayList<>();

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
                            Toast.makeText(ControlRecomendar.this, "Año introducido incorrecto.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        break;
                }

                String finalQuery = query;
                new Thread(() -> {
                    connectionManager.executeQuery(finalQuery, new ConnectionManager.QueryCallback() {
                        @Override
                        public void onQueryCompleted(ResultSet resultSet, int rowsAffected) {
                            try {
                                while (resultSet.next()) {
                                    Pelicula p = new Pelicula();
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
                                    peliculas.add(p);
                                }

                                runOnUiThread(() -> {
                                    if (!peliculas.isEmpty()) {
                                        AdaptadorCompartir adaptador = new AdaptadorCompartir(peliculas, ControlRecomendar.this);
                                        recyclerView.setHasFixedSize(true);
                                        recyclerView.setLayoutManager(new LinearLayoutManager(ControlRecomendar.this));
                                        recyclerView.setAdapter(adaptador);

                                        adaptador.setOnShareClickListener((bitmap, title, description, actor, genero, director, plataforma, adapterPosition) -> {
                                            // Obtener la película correspondiente a la posición en el adaptador
                                            Pelicula pelicula = adaptador.getPeliculaAtPosition(adapterPosition);
                                            String imageUrl = pelicula.getImagen();
                                            if (ContextCompat.checkSelfPermission(ControlRecomendar.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                                    != PackageManager.PERMISSION_GRANTED) {
                                                // Solicitar el permiso de almacenamiento
                                                ActivityCompat.requestPermissions(ControlRecomendar.this,
                                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                        PERMISSION_REQUEST_EXTERNAL_STORAGE);
                                            } else {
                                                // El permiso de almacenamiento ya está concedido, descargar y compartir la imagen
                                                descargarYCompartirImagen(imageUrl, title, description, actor, genero, director, plataforma);
                                            }
                                        });
                                    } else {
                                        Toast.makeText(ControlRecomendar.this, "No se encontraron películas.", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(ControlRecomendar.this, "Por favor, ingrese un término de búsqueda.", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void descargarYCompartirImagen(String imageUrl, String title, String description, String actor, String genero, String director, String plataforma) {
        new Thread(() -> {
            try {
                Bitmap bitmap = Picasso.get().load(imageUrl).get();
                runOnUiThread(() -> compartirPelicula(ControlRecomendar.this, bitmap, title, description, actor, genero, director, plataforma));
            } catch (Exception e) {
                Log.e(TAG, "Error al descargar la imagen: " + e.getMessage());
            }
        }).start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso de almacenamiento externo concedido, descargar y compartir la imagen
                if (position >= 0 && position < peliculas.size()) {
                    Pelicula pelicula = peliculas.get(position);
                    String imageUrl = pelicula.getImagen();
                    String title = pelicula.getTitulo();
                    String description = pelicula.getDescripcion();
                    String actor = pelicula.getProtagonista();
                    String genero = pelicula.getGenero();
                    String director = pelicula.getDirector();
                    String plataforma = pelicula.getPlataforma();
                    descargarYCompartirImagen(imageUrl, title, description, actor, genero, director, plataforma);
                }
            } else {
                // Permiso de almacenamiento externo denegado, muestra un mensaje al usuario
                Toast.makeText(ControlRecomendar.this, "Permiso de almacenamiento denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
