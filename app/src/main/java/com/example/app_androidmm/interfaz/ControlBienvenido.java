package com.example.app_androidmm.interfaz;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;
import com.example.app_androidmm.R;
import com.example.app_androidmm.database.Usuario;

import static com.example.app_androidmm.utilidades.Utilidades.*;


public class ControlBienvenido extends AppCompatActivity {

    Button btnPuntuar, btnRecomendar, btnGenerar;
    ImageButton btnPerfil;
    TextView textViewUser, txtVerificado;
    Usuario user = Usuario.getInstance();
    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pagina_bienvenido);
        textViewUser = findViewById(R.id.lblUser);
        txtVerificado = findViewById(R.id.lblVerificado);
        btnPerfil = findViewById(R.id.imgAvatarC);

        if(!user.getAlias().isEmpty()) {
            textViewUser.setText(user.getAlias());
            loadimageButtonFromUrl(user.getAvatar(),btnPerfil);
            if(user.isVerificado()) {
                txtVerificado.setText("Verificado");
                int color = Color.parseColor("#FF8EFF0C");
                txtVerificado.setTextColor(color);
            }
        }



        btnPerfil.setOnClickListener(view -> {
            Intent intent = new Intent(ControlBienvenido.this, ControlConfig.class);
            startActivity(intent);
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
}
