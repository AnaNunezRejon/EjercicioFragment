package com.example.ejerciciofragment.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ejerciciofragment.R;
import com.example.ejerciciofragment.model.Estado;
import com.example.ejerciciofragment.model.Ticket;
import com.example.ejerciciofragment.viewmodel.controlador;

import java.util.ArrayList;

public class EditarFragment extends Fragment {

    private TextView tvSaludo, tvCodigo;
    private TextView tvTicketId, tvNombreUsuario;
    private EditText etDescripcion, etResolucion;
    private Spinner spEstado;
    private Button btnGuardar;

    private boolean esNuevo;
    private Ticket ticketActual;
    private ArrayList<Ticket> listaTickets;
    private int nuevoIdGenerado = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View vista = inflater.inflate(R.layout.fragment_editar, container, false);

        // VISTAS
        tvSaludo = vista.findViewById(R.id.tvSaludo);
        tvCodigo = vista.findViewById(R.id.tvCodigo);
        tvTicketId = vista.findViewById(R.id.tvTicketId);
        tvNombreUsuario = vista.findViewById(R.id.tvNombreUsuario);
        etDescripcion = vista.findViewById(R.id.etDescripcion);
        etResolucion = vista.findViewById(R.id.etResolucion);
        spEstado = vista.findViewById(R.id.spEstado);
        btnGuardar = vista.findViewById(R.id.btnGuardar);

        // CABECERA
        tvSaludo.setText("Hola, " + controlador.getNombre() + " " + controlador.getApellido());
        tvCodigo.setText(controlador.getCodigo());

        // Cargar todos los tickets del fichero
        listaTickets = controlador.leerTickets(getContext());

        // Spinner de estados
        ArrayAdapter<CharSequence> adapterSpinner = ArrayAdapter.createFromResource(
                getContext(),
                R.array.estados,
                android.R.layout.simple_spinner_item
        );
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEstado.setAdapter(adapterSpinner);

        // ¿VENIMOS A EDITAR O A CREAR?
        Bundle args = getArguments();
        if (args != null && args.containsKey("id")) {
            // MODO EDITAR
            int id = args.getInt("id");
            ticketActual = buscarTicketPorId(id);
            esNuevo = false;
            configurarModoEditar();
        } else {
            // MODO NUEVO
            esNuevo = true;
            configurarModoNuevo();
        }

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardar();
            }
        });

        return vista;
    }


    private void configurarModoNuevo() {

        nuevoIdGenerado = calcularSiguienteId();

        tvTicketId.setText("Ticket #" + nuevoIdGenerado + " generado");

        String nombreCompleto = controlador.getNombre() + " " + controlador.getApellido();
        tvNombreUsuario.setText(nombreCompleto);

        // Descripción vacía y editable
        etDescripcion.setText("");
        etDescripcion.setEnabled(true);

        // Resolución vacía y NO editable
        etResolucion.setText("");
        etResolucion.setEnabled(false);

        // Estado NUEVO y no editable
        int posNuevo = 0; // en el array, NUEVO es el primer estado después de "Todos" o el primero, según tengas
        // si tu R.array.estados es SOLO estados (NUEVO, ABIERTO...), entonces NUEVO es índice 0
        spEstado.setSelection(0);
        spEstado.setEnabled(false);
    }


    private void configurarModoEditar() {
        if (ticketActual == null) return;

        tvTicketId.setText("Ticket #" + ticketActual.getId());
        tvNombreUsuario.setText(ticketActual.getNombreUsuario());

        // Descripción NO editable
        etDescripcion.setText(ticketActual.getDescripcion());
        etDescripcion.setEnabled(false);

        // Resolución editable
        etResolucion.setText(ticketActual.getResolucion());
        etResolucion.setEnabled(true);

        // Estado editable
        int pos = ticketActual.getEstado().ordinal();
        spEstado.setSelection(pos);
        spEstado.setEnabled(true);
    }


    private void guardar() {

        if (esNuevo) {
            // Crear ticket nuevo
            String nombreCompleto = controlador.getNombre() + " " + controlador.getApellido();
            String descripcion = etDescripcion.getText().toString();

            Ticket nuevo = new Ticket(
                    nuevoIdGenerado,
                    nombreCompleto,
                    descripcion,
                    "",
                    Estado.NUEVO
            );

            listaTickets.add(nuevo);

        } else {
            // Actualizar ticket existente: SOLO resolución y estado
            if (ticketActual == null) return;

            String nuevaResolucion = etResolucion.getText().toString();
            ticketActual.setResolucion(nuevaResolucion);

            String estadoString = spEstado.getSelectedItem().toString();
            Estado nuevoEstado = Estado.valueOf(estadoString);
            ticketActual.setEstado(nuevoEstado);

            // Reemplazar en la lista
            for (int i = 0; i < listaTickets.size(); i++) {
                if (listaTickets.get(i).getId() == ticketActual.getId()) {
                    listaTickets.set(i, ticketActual);
                }
            }
        }

        // Guardar todo en el fichero
        controlador.guardarTickets(getContext(), listaTickets);

        // Volver al Home
        getParentFragmentManager().popBackStack();
    }

    // ---------- AUXILIARES ----------

    private Ticket buscarTicketPorId(int id) {
        for (int i = 0; i < listaTickets.size(); i++) {
            Ticket t = listaTickets.get(i);
            if (t.getId() == id) {
                return t;
            }
        }
        return null;
    }

    private int calcularSiguienteId() {
        int max = 0;
        for (int i = 0; i < listaTickets.size(); i++) {
            int id = listaTickets.get(i).getId();
            if (id > max) max = id;
        }
        return max + 1;
    }
}
