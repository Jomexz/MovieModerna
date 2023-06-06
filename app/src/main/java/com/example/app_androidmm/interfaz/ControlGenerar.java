package com.example.app_androidmm.interfaz;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.app_androidmm.R;

public class ControlGenerar extends AppCompatActivity {
    Button btnCompartir;

    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.pagina_generar);

        btnCompartir = findViewById(R.id.shareButton);
    }
}
