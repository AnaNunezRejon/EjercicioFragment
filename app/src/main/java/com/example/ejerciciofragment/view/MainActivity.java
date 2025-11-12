package com.example.ejerciciofragment.view;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.ejerciciofragment.R;
import com.example.ejerciciofragment.viewmodel.controlador;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (controlador.haySesionActiva()) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.contenedor, new HomeFragment())
                    .commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.contenedor, new LoginFragment())
                    .commit();
        }

    }
}
