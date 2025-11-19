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


    private TextView tvSaludo, tvCodigo;// TextViews de la cabecera (saludo y código de trabajador)

    private TextView tvTicketId, tvNombreUsuario;// TextViews del propio ticket (ID y nombre de usuario / remitente)

    private EditText etDescripcion, etResolucion;// Campos de texto para la descripción del problema y la resolución

    private Spinner spEstado;// Spinner para seleccionar el estado del ticket

    private Button btnGuardar;// Botón para guardar los cambios o el nuevo ticket

    private boolean esNuevo;// Indica si el fragment está creando un ticket nuevo (true) o editando uno existente (false)

    private Ticket ticketActual;// Referencia al ticket que se está editando (en modo edición)

    private ArrayList<Ticket> listaTickets;// Lista completa de tickets leída del fichero

    private int nuevoIdGenerado = -1;// ID que se generará para el nuevo ticket (en modo nuevo)

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // 1. Inflamos el layout XML que define la interfaz de este fragment
        View vista = inflater.inflate(R.layout.fragment_editar, container, false);

        // 2. Vinculamos las vistas del xml con las variables Java
        tvSaludo = vista.findViewById(R.id.tvSaludo);
        tvCodigo = vista.findViewById(R.id.tvCodigo);
        tvTicketId = vista.findViewById(R.id.tvTicketId);
        tvNombreUsuario = vista.findViewById(R.id.tvNombreUsuario);
        etDescripcion = vista.findViewById(R.id.etDescripcion);
        etResolucion = vista.findViewById(R.id.etResolucion);
        spEstado = vista.findViewById(R.id.spEstado);
        btnGuardar = vista.findViewById(R.id.btnGuardar);

        // 3. Rellenamos la cabecera con el nombre y código del usuario que ha iniciado sesión
        tvSaludo.setText("Hola, " + controlador.getNombre() + " " + controlador.getApellido());
        tvCodigo.setText(controlador.getCodigo());

        // 4. Leemos todos los tickets que hay guardados en el fichero "tickets.txt"
        //    Esto nos sirve tanto para editar uno existente como para añadir uno nuevo al final.
        listaTickets = controlador.leerTickets(getContext());

        // 5. Configuramos el Spinner de estados usando el array R.array.estados del XML
        ArrayAdapter<CharSequence> adapterSpinner = ArrayAdapter.createFromResource(
                getContext(),
                R.array.estados, // ["NUEVO", "ABIERTO", "PENDIENTE", "RESUELTO", "CERRADO"]
                android.R.layout.simple_spinner_item
        );
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEstado.setAdapter(adapterSpinner);

        // 6. Comprobamos si este fragment ha recibido argumentos (Bundle)
        //    Si trae un "id", significa que venimos de pulsar en un ticket de la lista (MODO EDITAR)
        //    Si NO trae nada, significa que venimos del botón "Nuevo ticket" (MODO NUEVO)
        Bundle args = getArguments();
        if (args != null && args.containsKey("id")) {
            // --- MODO EDITAR ---
            esNuevo = false;  // estamos editando, no creando
            int id = args.getInt("id");  // recogemos el ID que vino en el Bundle
            ticketActual = buscarTicketPorId(id); // buscamos ese ticket en la lista
            configurarModoEditar();               // configuramos la pantalla para edición
        } else {
            // --- MODO NUEVO ---
            esNuevo = true;         // estamos creando un ticket nuevo
            configurarModoNuevo();  // configuramos la pantalla para creación
        }

        // 7. Programamos el botón GUARDAR para que llame al método guardar()
        btnGuardar.setOnClickListener(v -> guardar());

        // 8. Devolvemos la vista ya configurada
        return vista;
    }


    // --------------------------------------------------
    // -------------------- MODO NUEVO -------------------
    // --------------------------------------------------

    // Este metodo se ejecuta cuando el fragment se usa para CREAR un ticket nuevo
    private void configurarModoNuevo() {

        // 1. Calculamos el siguiente ID disponible buscando el máximo y sumando 1
        nuevoIdGenerado = calcularSiguienteId();

        // 2. Mostramos el ID generado en el TextView correspondiente
        tvTicketId.setText("Ticket #" + nuevoIdGenerado + " generado");

        // 3. Construimos el nombre completo a partir de los datos del controlador (login)
        String nombreCompleto = controlador.getNombre() + " " + controlador.getApellido();
        tvNombreUsuario.setText(nombreCompleto);

        // 4. La descripción del problema se empieza vacía y SÍ se puede escribir
        etDescripcion.setText("");
        etDescripcion.setEnabled(true);

        // 5. La resolución empieza vacía y NO es editable (porque todavía no se ha resuelto)
        etResolucion.setText("");
        etResolucion.setEnabled(false);

        // 6. El estado para un ticket nuevo SIEMPRE es NUEVO (posición 0 del array)
        spEstado.setSelection(0);
        // En modo nuevo no permitimos cambiar el estado desde el formulario
        spEstado.setEnabled(false);
    }


    // --------------------------------------------------
    // ------------------- MODO EDITAR -------------------
    // --------------------------------------------------

    // Este metodo se ejecuta cuando el fragment se usa para EDITAR un ticket existente
    private void configurarModoEditar() {

        // Si por alguna razón el ticket no se encontró, salimos
        if (ticketActual == null) return;

        // 1. Mostramos el ID real del ticket que estamos editando
        tvTicketId.setText("Ticket #" + ticketActual.getId());

        // 2. Mostramos el nombre del usuario remitente del ticket
        tvNombreUsuario.setText(ticketActual.getNombreUsuario());

        // 3. Mostramos la descripción original escrita por el usuario
        //    y la bloqueamos para que NO se pueda modificar (requisito B que nos dijiste)
        etDescripcion.setText(ticketActual.getDescripcion());
        etDescripcion.setEnabled(false);

        // 4. Mostramos la resolución actual (si tiene) y la dejamos editable
        etResolucion.setText(ticketActual.getResolucion());
        etResolucion.setEnabled(true);

        // 5. Seleccionamos en el Spinner el estado actual del ticket
        //    ordinal() devuelve la posición del enum (NUEVO=0, ABIERTO=1, etc.)
        int pos = ticketActual.getEstado().ordinal();
        spEstado.setSelection(pos);

        // 6. En modo edición sí se permite cambiar el estado
        spEstado.setEnabled(true);
    }


    // --------------------------------------------------
    // -------------------- GUARDAR ----------------------
    // --------------------------------------------------

    // Este metodo se ejecuta cuando el usuario pulsa el botón "Guardar"
    // y tiene en cuenta si estamos en modo NUEVO o modo EDITAR
    private void guardar() {

        if (esNuevo) {
            // --------- MODO NUEVO ---------

            // Creamos un nuevo objeto Ticket a partir de los datos introducidos
            Ticket nuevo = new Ticket(
                    nuevoIdGenerado,                                             // ID generado automáticamente
                    controlador.getNombre() + " " + controlador.getApellido(),   // remitente = usuario del login
                    etDescripcion.getText().toString(),                          // descripción escrita en el campo
                    "",                                                          // resolución vacía al crear
                    Estado.NUEVO                                                 // estado siempre NUEVO
            );

            // Añadimos el nuevo ticket a la lista
            listaTickets.add(nuevo);

        } else {
            // --------- MODO EDITAR ---------

            // Si no hay ticket actual, no hacemos nada
            if (ticketActual == null) return;

            // 1. Actualizamos la resolución con el texto que haya escrito el usuario
            ticketActual.setResolucion(etResolucion.getText().toString());

            // 2. Obtenemos el estado seleccionado en el Spinner (como String)
            String estadoString = spEstado.getSelectedItem().toString();
            // Lo convertimos a valor del enum Estado
            Estado nuevoEstado = Estado.valueOf(estadoString);
            // Guardamos el nuevo estado en el ticket
            ticketActual.setEstado(nuevoEstado);

            // 3. Recorremos la lista de tickets para reemplazar el ticket antiguo
            //    por la versión actualizada (ticketActual)
            for (int i = 0; i < listaTickets.size(); i++) {
                if (listaTickets.get(i).getId() == ticketActual.getId()) {
                    listaTickets.set(i, ticketActual);
                    break; // una vez encontrado y reemplazado, salimos del bucle
                }
            }
        }

        // 4. Guardamos la lista completa de tickets (actualizada) en el fichero "tickets.txt"
        controlador.guardarTickets(getContext(), listaTickets);

        // 5. Volvemos al fragment anterior (normalmente, el HomeFragment)
        getParentFragmentManager().popBackStack();
    }


    // --------------------------------------------------
    // ----------------- AUXILIARES ----------------------
    // --------------------------------------------------

    // Busca y devuelve el ticket cuyo ID coincida con el que le pasamos
    // Si no encuentra ninguno, devuelve null
    private Ticket buscarTicketPorId(int id) {
        for (int i = 0; i < listaTickets.size(); i++) {
            Ticket t = listaTickets.get(i);
            if (t.getId() == id) return t;
        }
        return null;
    }

    // Calcula el siguiente ID disponible:
    // busca el ID más alto de la lista y le suma 1
    private int calcularSiguienteId() {
        int max = 0;
        for (int i = 0; i < listaTickets.size(); i++) {
            int id = listaTickets.get(i).getId();
            if (id > max) max = id;
        }
        return max + 1;
    }
}

