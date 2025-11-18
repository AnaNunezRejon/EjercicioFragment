package com.example.ejerciciofragment.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
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
    private TicketAdapter adapter;

    private ArrayList<Ticket> listaTickets;
    private ArrayList<Ticket> listaOriginal;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        getContext().deleteFile("tickets.txt");
        controlador.copiarTicketsDesdeAssets(getContext());
        listaOriginal = controlador.leerTickets(getContext());

        listaTickets = new ArrayList<>(listaOriginal);

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
        tvCodigo.setText("Código: " + controlador.getCodigo());

        // CARGAR SPINNER
        ArrayAdapter<CharSequence> adapterSpinner = ArrayAdapter.createFromResource(
                getContext(),
                R.array.estados_filtro,
                android.R.layout.simple_spinner_item
        );
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFiltro.setAdapter(adapterSpinner);

        spFiltro.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {

                String seleccionado = spFiltro.getSelectedItem().toString();

                filtrarTickets(seleccionado);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });




        // LEER TICKETS DEL FICHERO
        listaTickets = controlador.leerTickets(getContext());

        // CONFIGURAR RECYCLER VIEW
        rvTickets.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TicketAdapter(listaTickets);
        rvTickets.setAdapter(adapter);

        // BOTÓN NUEVO TICKET
        btnNuevo.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.contenedor, new EditarFragment())
                    .addToBackStack(null)
                    .commit();
        });

        // BOTÓN CERRAR SESIÓN
        btnCerrar.setOnClickListener(v -> {
            controlador.borrarSesion(getContext());
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.contenedor, new LoginFragment())
                    .commit();
        });

        return vista;
    }

    private void filtrarTickets(String estado) {

        listaTickets.clear();

        if (estado.equals("Todos")) {
            listaTickets.addAll(listaOriginal);
        } else {

            for (int i = 0; i < listaOriginal.size(); i++) {
                Ticket t = listaOriginal.get(i);

                if (t.getEstado().toString().equals(estado)) {
                    listaTickets.add(t);
                }
            }
        }

        adapter.notifyDataSetChanged();
    }

}
