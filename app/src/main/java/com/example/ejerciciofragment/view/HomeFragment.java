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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ejerciciofragment.R;
import com.example.ejerciciofragment.model.Ticket;
import com.example.ejerciciofragment.viewmodel.controlador;

import java.util.ArrayList;

public class HomeFragment extends Fragment {


    private TextView tvSaludo, tvCodigo;// Elementos del encabezado con los datos del usuario

    private Spinner spFiltro;// Spinner para elegir el estado a filtrar

    private Button btnNuevo, btnCerrar;// Botones de abajo (nuevo ticket y cerrar sesión)

    private RecyclerView rvTickets; // Lista y adaptador del RecyclerView
    private TicketAdapter adapter;

    private ArrayList<Ticket> listaTickets;// Listas de tickets (una original y una filtrada)
    private ArrayList<Ticket> listaOriginal;// Listas de tickets (una original y una filtrada)


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // 1. PRIMERA LECTURA DEL FICHERO DE TICKETS
        // Leer tickets desde /files/tickets.txt (ya copiado desde assets la 1ª vez)
        listaOriginal = controlador.leerTickets(getContext());

        // Copiamos la lista original para tener lista filtrable
        listaTickets = new ArrayList<>(listaOriginal);

        // 2. INFLAR LA VISTA DEL HOME
        View vista = inflater.inflate(R.layout.fragment_home, container, false);

        // 3. ENLAZAR VISTAS DEL XML CON VARIABLES JAVA
        tvSaludo = vista.findViewById(R.id.tvSaludo);
        tvCodigo = vista.findViewById(R.id.tvCodigo);
        spFiltro = vista.findViewById(R.id.spFiltro);
        btnNuevo = vista.findViewById(R.id.btnNuevo);
        btnCerrar = vista.findViewById(R.id.btnCerrar);
        rvTickets = vista.findViewById(R.id.rvTickets);

        // 4. RELLENAR CABECERA CON LOS DATOS DEL USUARIO
        tvSaludo.setText("Hola, " + controlador.getNombre() + " " + controlador.getApellido());
        tvCodigo.setText("Código: " + controlador.getCodigo());

        // 5. CONFIGURAR EL SPINNER PARA FILTRAR LOS TICKETS
        ArrayAdapter<CharSequence> adapterSpinner = ArrayAdapter.createFromResource(
                getContext(),
                R.array.estados_filtro,   // ["Todos", "NUEVO", "ABIERTO"...]
                android.R.layout.simple_spinner_item
        );
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFiltro.setAdapter(adapterSpinner);

        // Listener del spinner → cada vez que se cambia, se filtran los tickets
        spFiltro.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                String seleccionado = spFiltro.getSelectedItem().toString();
                filtrarTickets(seleccionado); // llamamos al método que filtra
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        // 6. CONFIGURAR EL RECYCLER VIEW CON LOS TICKETS
        rvTickets.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TicketAdapter(listaTickets);
        rvTickets.setAdapter(adapter);

        // 7. BOTÓN "NUEVO TICKET" → abre EditarFragment en modo nuevo
        btnNuevo.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.contenedor, new EditarFragment())
                    .addToBackStack(null)
                    .commit();
        });

        // 8. BOTÓN "CERRAR SESIÓN"
        btnCerrar.setOnClickListener(v -> {
            controlador.borrarSesion(getContext()); // borra SharedPreferences

            getParentFragmentManager().beginTransaction()
                    .replace(R.id.contenedor, new LoginFragment())
                    .commit();
        });

        return vista;
    }


    // MeTODO PARA FILTRAR LOS TICKETS POR SU ESTADO

    private void filtrarTickets(String estado) {

        // Vaciar la lista mostrada actualmente
        listaTickets.clear();

        if (estado.equals("Todos")) {
            // Si el usuario elige "Todos" → mostramos todos los tickets
            listaTickets.addAll(listaOriginal);
        } else {
            // Si elige un estado → filtramos solo los que coincidan
            for (int i = 0; i < listaOriginal.size(); i++) {
                Ticket t = listaOriginal.get(i);

                if (t.getEstado().toString().equals(estado)) {
                    listaTickets.add(t);
                }
            }
        }

        // Avisamos al adaptador de que los datos han cambiado
        adapter.notifyDataSetChanged();
    }

    // onResume() SE EJECUTA CADA VEZ QUE VOLVEMOS DESDE EditarFragment
    @Override
    public void onResume() {
        super.onResume();

        // Volvemos a leer del fichero por si se ha creado o modificado un ticket
        listaOriginal = controlador.leerTickets(getContext());

        // Copiamos los datos a la lista que ve el usuario
        listaTickets = new ArrayList<>(listaOriginal);

        // Actualizamos el RecyclerView
        adapter.actualizarLista(listaTickets);
    }
}
