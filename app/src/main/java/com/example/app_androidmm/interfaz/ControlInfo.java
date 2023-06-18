package com.example.app_androidmm.interfaz;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import com.example.app_androidmm.R;
import com.example.app_androidmm.database.Usuario;

import static com.example.app_androidmm.utilidades.Utilidades.*;
import static com.example.app_androidmm.utilidades.Utilidades.redirectActivity;

public class ControlInfo extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ImageView menu, navAvatar;
    private LinearLayout home, settings, info, logout, share;
    private TextView navUser, navNombre;
    private Usuario user = Usuario.getInstance();
    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pagina_info);

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
            recreate();
        });

        logout.setOnClickListener(view -> {
            Toast.makeText(this,"Has cerrado sesión correctamente", Toast.LENGTH_SHORT).show();
            user = null; // Borramos los datos del usuario
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            redirectActivity(this, MainActivity.class);

        });

        share.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, "¿Quieres disfrutar de una experiencia cinematográfica única junto a tus amigos? " +
                    "Descubre películas increíbles y genera recomendaciones personalizadas con MovieModerna. ¡Explora el mundo del cine y comparte tus descubrimientos!" +
                    " Haz que cada noche de cine sea especial. Descarga MovieModerna ahora mismo: [link]");
            intent.setType("text/plain");
            if(intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Toast.makeText(this,"No hay permisos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(drawerLayout);
    }
}
