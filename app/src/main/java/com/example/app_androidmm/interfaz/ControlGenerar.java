package com.example.app_androidmm.interfaz;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.example.app_androidmm.R;
import com.example.app_androidmm.database.ConnectionManager;
import com.example.app_androidmm.database.Usuario;

public class ControlGenerar extends AppCompatActivity {
    ConnectionManager connectionManager = new ConnectionManager();
    Usuario user = Usuario.getInstance();
    RecyclerView recyclerView;
    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.pagina_generar);

    }
}
