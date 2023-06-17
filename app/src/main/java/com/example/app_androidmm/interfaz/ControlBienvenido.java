package com.example.app_androidmm.interfaz;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import com.example.app_androidmm.R;
import com.example.app_androidmm.database.ConnectionManager;
import com.example.app_androidmm.database.Usuario;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.example.app_androidmm.utilidades.Utilidades.loadimageButtonFromUrl;
import static com.example.app_androidmm.utilidades.Utilidades.*;


public class ControlBienvenido extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ConnectionManager connectionManager = new ConnectionManager();
    private final String TAG = "BienvenidoControl";
    private ImageView menu, imgPerfil, navAvatar;
    private LinearLayout home, settings, info, logout, share;
    private Button btnPuntuar, btnRecomendar, btnGenerar;
    private TextView textViewUser, txtVerificado, navUser, navNombre;
    private Usuario user = Usuario.getInstance();

    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pagina_bienvenido);

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
        navUser.setText(user.getAlias());
        navNombre.setText(user.getNombre() + " " + user.getApellidos());

        textViewUser = findViewById(R.id.lblUser);
        txtVerificado = findViewById(R.id.lblVerificado);
        imgPerfil = findViewById(R.id.imgAvatarC);

        connectionManager.executeQuery("SELECT * FROM usuario where alias like '" + user.getAlias() + "'", new ConnectionManager.QueryCallback() {
            @Override
            public void onQueryCompleted(ResultSet resultSet, int rowsAffected) {
                try {
                    // Procesar los resultados de la consulta
                    while (resultSet.next()) {
                        user.setAlias(resultSet.getString("alias"));
                        user.setPass(resultSet.getString("contrasena"));
                        user.setPkUsuario(resultSet.getInt("pkusuario"));
                        user.setEmail(resultSet.getString("email"));
                        user.setNombre(resultSet.getString("nombre"));
                        user.setApellidos(resultSet.getString("apellidos"));
                        user.setFechaNacimiento(resultSet.getDate("fechanace"));
                        user.setVerificado(resultSet.getBoolean("verificado"));
                        user.setPregunta(resultSet.getString("pregunta"));
                        user.setRespuesta(resultSet.getString("respuesta"));
                        user.setAvatar((resultSet.getString("avatar")));
                        runOnUiThread(() -> {
                            textViewUser.setText(user.getAlias());
                            loadImageFromUrl(user.getAvatar(), imgPerfil);
                            loadImageFromUrl(user.getAvatar(), navAvatar);
                            if (user.isVerificado()) {
                                txtVerificado.setText("Verificado");
                                int color = Color.parseColor("#FF8EFF0C");
                                txtVerificado.setTextColor(color);
                            }
                        });
                        Log.d(TAG, user.toString());
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

        menu.setOnClickListener(view -> {
            openDrawer(drawerLayout);
        });

        home.setOnClickListener(view -> {
            recreate();
        });

        settings.setOnClickListener(view -> {
            redirectActivity(this, ControlConfig.class);
        });

        info.setOnClickListener(view -> {
            redirectActivity(this, ControlInfo.class);
        });

        logout.setOnClickListener(view -> {
            Toast.makeText(this, "Has cerrado sesión correctamente", Toast.LENGTH_SHORT);
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
                Toast.makeText(this, "No hay permisos", Toast.LENGTH_SHORT);
            }
        });

        btnPuntuar = findViewById(R.id.btnPuntuar);
        btnPuntuar.setOnClickListener(view -> {
            redirectActivity(this, ControlPuntuar.class);
        });

        btnRecomendar = findViewById(R.id.btnRecomendar);
        btnRecomendar.setOnClickListener(view -> {
            redirectActivity(this, ControlRecomendar.class);
        });

        btnGenerar = findViewById(R.id.btnGenerarRecomendacion);
        btnGenerar.setOnClickListener(view -> {
            if (user.isVerificado()) {
                redirectActivity(this, ControlGenerar.class);
            } else {
                System.out.println("No es verificado");
                mostrarErrorCampo(this,"El usuario debe estar verificado. Número de películas valoradas mínimo: 20.","¡Valora primero!");
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(drawerLayout);
    }


}
