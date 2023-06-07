package com.example.app_androidmm.interfaz;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.example.app_androidmm.R;
import com.example.app_androidmm.database.ConnectionManager;
import com.example.app_androidmm.database.Pelicula;
import com.example.app_androidmm.database.Usuario;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ControlGenerar extends AppCompatActivity {
    final String TAG = "GenerarControl";
    ConnectionManager connectionManager = new ConnectionManager();
    Usuario user = Usuario.getInstance();
    RecyclerView recyclerView;
    List<Pelicula> peliculas = new ArrayList<>();
    Pelicula pelicula = new Pelicula();
    Pelicula pelicula1 = new Pelicula();
    List<Pelicula> peliculasUsuario = new ArrayList<>();
    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.pagina_generar);

        new Thread(() -> {
            connectionManager.executeQuery("select * from pelicula where pkpelicula in (select * from visualizacion where akusuario = " + user.getPkUsuario() +")", new ConnectionManager.QueryCallback() {
                @Override
                public void onQueryCompleted(ResultSet resultSet, int rowsAffected) {
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
                            System.out.println(pelicula.getTitulo());
                            peliculasUsuario.add(pelicula);
                        }
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
                    } catch (SQLException e) {
                        Log.e(TAG,"Error al obtener películas: " + e.getMessage());
                        throw new RuntimeException(e);
                    }

                    // Ejecutar la similitud para cada pelicula
                    double similitud = 0;
                    for (Pelicula p : peliculasUsuario) {
                        for (Pelicula p2 : peliculas) {
                            similitud = p.calcularSimilitud(p2);
                        }
                    }
                }

                @Override
                public void onQueryFailed(String error) {
                    Log.e(TAG, "Error al ejecutar query: " + error);
                }
            });
        }).start();
    }
}
