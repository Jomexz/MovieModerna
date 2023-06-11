package com.example.app_androidmm.interfaz;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.app_androidmm.R;
import com.example.app_androidmm.database.ConnectionManager;
import com.example.app_androidmm.database.Pelicula;
import com.example.app_androidmm.database.Usuario;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLOutput;
import java.util.*;

import static com.example.app_androidmm.interfaz.AdaptadorCompartir.PERMISSION_REQUEST_EXTERNAL_STORAGE;
import static com.example.app_androidmm.utilidades.Utilidades.*;
import static com.example.app_androidmm.utilidades.Utilidades.redirectActivity;

public class ControlGenerar extends AppCompatActivity {
    private final String TAG = "GenerarControl";
    private DrawerLayout drawerLayout;
    private ImageView menu, navAvatar;
    private LinearLayout home, settings, info, logout;
    private TextView navUser, navNombre;
    private ConnectionManager connectionManager = new ConnectionManager();
    private AdaptadorCompartir adaptadorCompartir;
    private Usuario user = Usuario.getInstance();
    private RecyclerView recyclerView;
    private List<Pelicula> peliculas = new ArrayList<>();
    private Pelicula pelicula = new Pelicula();
    private Pelicula pelicula1 = new Pelicula();
    private List<Pelicula> peliculasUsuario = new ArrayList<>();
    private int position;
    @SuppressLint({"WrongThread", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.pagina_generar);

        drawerLayout = findViewById(R.id.drawer_layout);
        menu = findViewById(R.id.navigation_menu);
        home = findViewById(R.id.ly_home);
        settings = findViewById(R.id.settings);
        info = findViewById(R.id.info);
        logout = findViewById(R.id.exit);
        navAvatar = findViewById(R.id.nav_avatar);
        navUser = findViewById(R.id.nav_user);
        navNombre = findViewById(R.id.nav_nameuser);
        loadImageFromUrl(user.getAvatar(),navAvatar);
        navUser.setText(user.getAlias());
        navNombre.setText(user.getNombre() + " " + user.getApellidos());

        menu.setOnClickListener(view -> {
            openDrawer(drawerLayout);
        });

        home.setOnClickListener(view -> {
            redirectActivity(this,ControlBienvenido.class);
        });

        settings.setOnClickListener(view -> {
            redirectActivity(this, ControlConfig.class);
        });

        info.setOnClickListener(view -> {
            redirectActivity(this, ControlInfo.class);
        });

        logout.setOnClickListener(view -> {
            Toast.makeText(this,"Has cerrado sesión correctamente", Toast.LENGTH_SHORT);
            user = null; // Borramos los datos del usuario
            redirectActivity(this, MainActivity.class);

        });

        recyclerView = findViewById(R.id.recyclerView);

        new Thread(() -> {
            connectionManager.executeQuery("select * from pelicula where pkpelicula in (select akpelicula from visualizacion where akusuario = " + user.getPkUsuario() +")", new ConnectionManager.QueryCallback() {
                @Override
                public void onQueryCompleted(ResultSet resultSet, int rowsAffected) {
                    System.out.println("PELICULAS DEL USUARIO: ");
                    try {
                        while (resultSet.next()) {
                            pelicula1.setPkPelicula(resultSet.getInt("pkpelicula"));
                            pelicula1.setTitulo(resultSet.getString("titulo"));
                            pelicula1.setDescripcion(resultSet.getString("descripcion"));
                            pelicula1.setRating((float) resultSet.getDouble("rating"));
                            pelicula1.setImagen(resultSet.getString("imagen"));
                            pelicula1.setGenero(resultSet.getString("genero"));
                            pelicula1.setCalificacion(resultSet.getString("calificacion"));
                            pelicula1.setDirector(resultSet.getString("director"));
                            pelicula1.setFechaPublicacion(resultSet.getDate("fechapublicacion"));
                            pelicula1.setProtagonista(resultSet.getString("protagonista"));
                            pelicula1.setPlataforma(resultSet.getString("plataforma"));
                            System.out.println(pelicula1.getTitulo());
                            peliculasUsuario.add(pelicula1);
                        }
                        System.out.println("--------------------------");
                    } catch (SQLException e) {
                        Log.e(TAG,"Error al obtener películas: " + e.getMessage());
                        throw new RuntimeException(e);
                    }
                }

                @Override
                public void onQueryFailed(String error) {
                    Log.e(TAG, "Error al ejecutar query: " + error);
                }
            });
            connectionManager.executeQuery("select * from pelicula", new ConnectionManager.QueryCallback() {
                @Override
                public void onQueryCompleted(ResultSet resultSet, int rowsAffected) {
                    System.out.println("PELICULAS GENERALES: ");
                    try {
                        while (resultSet.next()) {
                            pelicula.setPkPelicula(resultSet.getInt("pkpelicula"));
                            pelicula.setTitulo(resultSet.getString("titulo"));
                            pelicula.setDescripcion(resultSet.getString("descripcion"));
                            pelicula.setRating((float) resultSet.getDouble("rating"));
                            pelicula.setImagen(resultSet.getString("imagen"));
                            pelicula.setGenero(resultSet.getString("genero"));
                            pelicula.setCalificacion(resultSet.getString("calificacion"));
                            pelicula.setDirector(resultSet.getString("director"));
                            pelicula.setFechaPublicacion(resultSet.getDate("fechapublicacion"));
                            pelicula.setProtagonista(resultSet.getString("protagonista"));
                            pelicula.setPlataforma(resultSet.getString("plataforma"));
                            System.out.println(pelicula.getTitulo());
                            peliculas.add(pelicula);
                        }
                        System.out.println("---------------------");
                        // Ejecutar la similitud para cada pelicula
                        // La lista de peliculas del usuario

                        // Crear una lista de todas las películas de la aplicación (excluyendo las del historial del usuario)
                        peliculas.removeAll(peliculasUsuario);

                        // Calcular la similitud entre cada película del historial y todas las películas de la aplicación
                        Map<Pelicula, Double> similitudPeliculas = new HashMap<>();
                        for (Pelicula peliculaHistorial : peliculasUsuario) {
                            for (Pelicula peliculaApp : peliculas) {
                                double similitud = peliculaHistorial.calcularSimilitud(peliculaApp);
                                similitudPeliculas.put(peliculaApp, similitud);
                            }
                        }

                        // Ordenar las películas según su similitud (de mayor a menor)
                        List<Pelicula> peliculasRecomendadas = new ArrayList<>(similitudPeliculas.keySet());
                        Collections.sort(peliculasRecomendadas, (p1, p2) -> Double.compare(similitudPeliculas.get(p2), similitudPeliculas.get(p1)));

                        // Obtener las mejores películas recomendadas (por ejemplo, las 5 primeras)
                        List<Pelicula> mejoresRecomendaciones = peliculasRecomendadas.subList(0, Math.min(5, peliculasRecomendadas.size()));

                        // Mostrar las mejores películas recomendadas al usuario
                        System.out.println("PELICULAS RECOMENDADAS:");
                        boolean resultados = false;
                        for (Pelicula recomendacion : mejoresRecomendaciones) {
                            System.out.println(recomendacion.getTitulo() + "  " + recomendacion.getImagen());
                            resultados = true;
                        }
                        boolean finalResultados = resultados;
                        runOnUiThread(() -> {
                            if (recyclerView != null) {
                                recyclerView.setHasFixedSize(true);
                                recyclerView.setLayoutManager(new LinearLayoutManager(ControlGenerar.this));
                            }
                            if(finalResultados) {
                                adaptadorCompartir = new AdaptadorCompartir(mejoresRecomendaciones, ControlGenerar.this, ControlGenerar.this);
                                recyclerView.setAdapter(adaptadorCompartir);
                                adaptadorCompartir.setOnShareClickListener((bitmap, title, description, actor, genero, director, plataforma, adapterPosition) -> {
                                    // Obtener la película correspondiente a la posición en el adaptador
                                    Pelicula pelicula = adaptadorCompartir.getPeliculaAtPosition(adapterPosition);
                                    String imageUrl = pelicula.getImagen();
                                    if (ContextCompat.checkSelfPermission(ControlGenerar.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                            != PackageManager.PERMISSION_GRANTED) {
                                        // Solicitar el permiso de almacenamiento
                                        ActivityCompat.requestPermissions(ControlGenerar.this,
                                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                PERMISSION_REQUEST_EXTERNAL_STORAGE);
                                    } else {
                                        // El permiso de almacenamiento ya está concedido, descargar y compartir la imagen
                                        runOnUiThread(() -> {
                                            descargarYCompartirImagen(TAG,ControlGenerar.this, imageUrl, title, description, actor, genero, director, plataforma);

                                        });
                                    }
                                });
                            }
                        });
                    } catch (SQLException e) {
                        Log.e(TAG,"Error al obtener películas: " + e.getMessage());
                        throw new RuntimeException(e);
                    }


                }

                @Override
                public void onQueryFailed(String error) {
                    Log.e(TAG, "Error al ejecutar query: " + error);
                }
            });
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
                    runOnUiThread(() -> {
                        descargarYCompartirImagen(TAG,this, imageUrl, title, description, actor, genero, director, plataforma);

                    });
                }
            } else {
                // Permiso de almacenamiento externo denegado, muestra un mensaje al usuario
                Toast.makeText(ControlGenerar.this, "Permiso de almacenamiento denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(drawerLayout);
    }
}
