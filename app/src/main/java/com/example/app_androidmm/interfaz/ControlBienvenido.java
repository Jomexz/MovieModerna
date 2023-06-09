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
    private ImageView menu, imgPerfil;
    private LinearLayout home, settings, info, logout;
    Button btnPuntuar, btnRecomendar, btnGenerar;
    ImageButton btnPerfil;
    TextView textViewUser, txtVerificado;
    Usuario user = Usuario.getInstance();
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
            redirectActivity(ControlBienvenido.this, ControlConfig.class);
        });

        info.setOnClickListener(view -> {
            redirectActivity(ControlBienvenido.this, ControlInfo.class);
        });

        logout.setOnClickListener(view -> {
            Toast.makeText(this,"Has cerrado sesión correctamente", Toast.LENGTH_SHORT);
            user = null; // Borramos los datos del usuario
            redirectActivity(ControlBienvenido.this, MainActivity.class);

        });

        btnPuntuar = findViewById(R.id.btnPuntuar);
        btnPuntuar.setOnClickListener(view -> {
            Intent intent = new Intent(ControlBienvenido.this, ControlPuntuar.class);
            startActivity(intent);
        });

        btnRecomendar = findViewById(R.id.btnRecomendar);
        btnRecomendar.setOnClickListener(view -> {
            Intent intent = new Intent(ControlBienvenido.this, ControlRecomendar.class);
            startActivity(intent);
        });

        btnGenerar = findViewById(R.id.btnGenerarRecomendacion);
        btnGenerar.setOnClickListener(view -> {
            if(user.isVerificado()) {
                Intent intent = new Intent(ControlBienvenido.this, ControlGenerar.class);
                startActivity(intent);
            } else {
                System.out.println("No es verificado");
                Toast.makeText(ControlBienvenido.this,"El usuario debe estar verificado. Número de películas valoradas mínimo: 20.", Toast.LENGTH_LONG);
            }
        });
    }
    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(drawerLayout);
    }


}
