package com.example.app_androidmm.interfaz;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import com.example.app_androidmm.R;
import com.example.app_androidmm.database.Usuario;

import static com.example.app_androidmm.utilidades.Utilidades.loadimageButtonFromUrl;
import static com.example.app_androidmm.utilidades.Utilidades.*;


public class ControlBienvenido extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ImageView menu, imgPerfil, navAvatar;
    private LinearLayout home, settings, info, logout;
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
        navAvatar = findViewById(R.id.nav_avatar);
        navUser = findViewById(R.id.nav_user);
        navNombre = findViewById(R.id.nav_nameuser);
        loadImageFromUrl(user.getAvatar(),navAvatar);
        navUser.setText(user.getAlias());
        navNombre.setText(user.getNombre() + " " + user.getApellidos());

        textViewUser = findViewById(R.id.lblUser);
        txtVerificado = findViewById(R.id.lblVerificado);
        imgPerfil = findViewById(R.id.imgAvatarC);

        if(!user.getAlias().isEmpty()) {
            textViewUser.setText(user.getAlias());
            loadImageFromUrl(user.getAvatar(),imgPerfil);
            if(user.isVerificado()) {
                txtVerificado.setText("Verificado");
                int color = Color.parseColor("#FF8EFF0C");
                txtVerificado.setTextColor(color);
            }
        }

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
            Toast.makeText(this,"Has cerrado sesión correctamente", Toast.LENGTH_SHORT);
            user = null; // Borramos los datos del usuario
            redirectActivity(this, MainActivity.class);

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
            if(user.isVerificado()) {
                redirectActivity(this, ControlGenerar.class);
            } else {
                System.out.println("No es verificado");
                Toast.makeText(this,"El usuario debe estar verificado. Número de películas valoradas mínimo: 20.", Toast.LENGTH_LONG);
            }
        });
    }
    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(drawerLayout);
    }


}
