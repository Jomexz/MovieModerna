package com.example.app_androidmm.interfaz;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import com.squareup.picasso.Picasso;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.example.app_androidmm.utilidades.Utilidades.*;

public class ControlRecomendar extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_EXTERNAL_STORAGE = 1;
    static final String TAG = "RecomendarControl";
    private AdaptadorCompartir adaptadorCompartir;
    private ImageButton btnBuscar;
    private ImageView menu, navAvatar;
    private DrawerLayout drawerLayout;
    private LinearLayout home, settings, info, logout;
    private TextView navUser, navNombre;
    private EditText buscador;
    private Spinner condiciones;
    private ConnectionManager connectionManager = new ConnectionManager();
    private List<Pelicula> listaPeliculas;
    private RecyclerView recyclerView;
    private Usuario user = Usuario.getInstance();
    private Pelicula pelicula = new Pelicula();
    private int position;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pagina_recomendar);
        System.out.println(user.toString());

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
            finish();
        });

        buscador = findViewById(R.id.txtSearchP);
        condiciones = findViewById(R.id.spinnerSearchByP);
        btnBuscar = findViewById(R.id.btnBuscarP);
        listaPeliculas = new ArrayList<>();

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
                            Toast.makeText(this, "Año introducido incorrecto.", Toast.LENGTH_SHORT).show();
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
                                if (adaptadorCompartir != null) {
                                    adaptadorCompartir.clearViews();
                                    adaptadorCompartir.notifyDataSetChanged();
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
                                        adaptadorCompartir = new AdaptadorCompartir(listaPeliculas, ControlRecomendar.this);
                                        recyclerView.setHasFixedSize(true);
                                        recyclerView.setLayoutManager(new LinearLayoutManager(ControlRecomendar.this));

                                        adaptadorCompartir.setOnShareClickListener((bitmap, title, description, actor, genero, director, plataforma, adapterPosition) -> {
                                            // Obtener la película correspondiente a la posición en el adaptador
                                            pelicula = listaPeliculas.get(adapterPosition);
                                            String imageUrl = pelicula.getImagen();
                                            if (ContextCompat.checkSelfPermission(ControlRecomendar.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                                    != PackageManager.PERMISSION_GRANTED) {
                                                // Solicitar el permiso de almacenamiento
                                                ActivityCompat.requestPermissions(ControlRecomendar.this,
                                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                        PERMISSION_REQUEST_EXTERNAL_STORAGE);
                                            } else {
                                                // El permiso de almacenamiento ya está concedido, descargar y compartir la imagen
                                                runOnUiThread(() -> {
                                                    descargarYCompartirImagen(TAG,ControlRecomendar.this, imageUrl, title, description, actor, genero, director, plataforma);

                                                });
                                            }
                                        });
                                        recyclerView.setAdapter(adaptadorCompartir);
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



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso de almacenamiento externo concedido, descargar y compartir la imagen
                if (position >= 0 && position < listaPeliculas.size()) {
                    Pelicula pelicula = listaPeliculas.get(position);
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
                Toast.makeText(ControlRecomendar.this, "Permiso de almacenamiento denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(drawerLayout);
    }
}
