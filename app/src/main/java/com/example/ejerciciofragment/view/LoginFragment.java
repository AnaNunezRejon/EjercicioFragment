package com.example.ejerciciofragment.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.example.ejerciciofragment.R;
import com.example.ejerciciofragment.viewmodel.controlador;

public class LoginFragment extends Fragment {

    private EditText etNombre, etApellido, etCodigo;
    private Button btnEntrar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_login, container, false);

        etNombre = vista.findViewById(R.id.etNombre);
        etApellido = vista.findViewById(R.id.etApellido);
        etCodigo = vista.findViewById(R.id.etCodigo);
        btnEntrar = vista.findViewById(R.id.btnEntrar);

        btnEntrar.setOnClickListener(view -> {
            String nombre = etNombre.getText().toString();
            String apellido = etApellido.getText().toString();
            String codigo = etCodigo.getText().toString();

            controlador.setUsuario(nombre, apellido, codigo);
            controlador.guardarSesion(getContext(), nombre, apellido, codigo);

            getParentFragmentManager().beginTransaction()
                    .replace(R.id.contenedor, new HomeFragment())
                    .commit();
        });

        return vista;
    }
}
