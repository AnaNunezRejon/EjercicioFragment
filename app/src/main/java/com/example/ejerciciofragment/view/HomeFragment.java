package com.example.ejerciciofragment.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ejerciciofragment.R;
import com.example.ejerciciofragment.model.Ticket;
import com.example.ejerciciofragment.viewmodel.controlador;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private TextView tvSaludo, tvCodigo;
    private Spinner spFiltro;
    private Button btnNuevo, btnCerrar;

    private RecyclerView rvTickets;

    //private TicketAdapter adapter;
    private ArrayList<Ticket> listaTickets;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View vista = inflater.inflate(R.layout.fragment_home, container, false);

        // ELEMENTOS DE LA VISTA
        tvSaludo = vista.findViewById(R.id.tvSaludo);
        tvCodigo = vista.findViewById(R.id.tvCodigo);
        spFiltro = vista.findViewById(R.id.spFiltro);
        btnNuevo = vista.findViewById(R.id.btnNuevo);
        btnCerrar = vista.findViewById(R.id.btnCerrar);
        rvTickets = vista.findViewById(R.id.rvTickets);

        // MOSTRAR NOMBRE Y CODIGO DEL USUARIO
        tvSaludo.setText("Hola, " + controlador.getNombre() + " " + controlador.getApellido());
        tvCodigo.setText("Código de trabajador: " + controlador.getCodigo());

        // CARGAR SPINNER
        ArrayAdapter<CharSequence> adapterSpinner = ArrayAdapter.createFromResource(
                getContext(),
                R.array.estados_filtro,
                android.R.layout.simple_spinner_item
        );
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFiltro.setAdapter(adapterSpinner);

        // LEER TICKETS DEL FICHERO
        listaTickets = controlador.leerTickets(getContext());

        // CONFIGURAR RECYCLER VIEW
        // rvTickets.setLayoutManager(new LinearLayoutManager(getContext()));
        // adapter = new TicketAdapter(listaTickets);
        // rvTickets.setAdapter(adapter);

        // BOTÓN NUEVO TICKET
        btnNuevo.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.contenedor, new EditarFragment())
                    .addToBackStack(null)
                    .commit();
        });

        // BOTÓN CERRAR SESIÓN
        btnCerrar.setOnClickListener(v -> {
            controlador.borrarSesion(getContext()); // SharedPreferences
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.contenedor, new LoginFragment())
                    .commit();
        });

        return vista;
    }
}
