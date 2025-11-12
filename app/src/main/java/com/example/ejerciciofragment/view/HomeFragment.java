package com.example.ejerciciofragment.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.ejerciciofragment.R;
import com.example.ejerciciofragment.viewmodel.controlador;

public class HomeFragment extends Fragment {

    private TextView tvSaludo, tvCodigo;
    private Spinner spFiltro;
    private Button btnNuevo, btnCerrar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_home, container, false);

        tvSaludo = vista.findViewById(R.id.tvSaludo);
        tvCodigo = vista.findViewById(R.id.tvCodigo);
        spFiltro = vista.findViewById(R.id.spFiltro);
        btnNuevo = vista.findViewById(R.id.btnNuevo);
        btnCerrar = vista.findViewById(R.id.btnCerrar);

        // Mostrar los datos del usuario guardados
        tvSaludo.setText("Hola, " + controlador.getNombre() + " " + controlador.getApellido());
        tvCodigo.setText("Código de trabajador: " + controlador.getCodigo());

        // Llenar el spinner con opciones
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.estados_filtro,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFiltro.setAdapter(adapter);

        // Botón para crear nuevo ticket
        btnNuevo.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.contenedor, new EditarFragment())
                    .addToBackStack(null)
                    .commit();
        });

        // Botón cerrar sesión
        btnCerrar.setOnClickListener(view -> {
            controlador.cerrarSesion();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.contenedor, new LoginFragment())
                    .commit();
        });

        return vista;
    }
}
