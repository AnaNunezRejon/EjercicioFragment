# ESTRUCTURA
```plsql
app/
 ├── manifests/
 │     └── AndroidManifest.xml
 │
 ├── java/
 │     └── com.example.ejerciciofragment/
 │          ├── model/
 │          │     ├── Estado.java
 │          │     └── Ticket.java
 │          │
 │          ├── view/
 │          │     ├── EditarFragment.java
 │          │     ├── HomeFragment.java
 │          │     ├── LoginFragment.java
 │          │     ├── MainActivity.java
 │          │     └── TicketAdapter.java
 │          │
 │          └── viewmodel/
 │                └── Controlador.java
 │
 ├── res/
 │     ├── drawable/
 │     │     ├── borde_spinner.xml
 │     │     ├── ic_launcher_background.xml
 │     │     ├── ic_launcher_foreground.xml
 │     │     └── ticket_bg.xml
 │     │
 │     └── layout/
 │           ├── activity_main.xml
 │           ├── fragment_editar.xml
 │           ├── fragment_home.xml
 │           ├── fragment_login.xml
 │           └── item_ticket.xml
 │
 └── com.example.ejerciciofragment (androidTest + test)

```

# CONTROLLER
Controlador.java
```java
package com.example.ejerciciofragment.viewmodel;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.ejerciciofragment.model.Estado;
import com.example.ejerciciofragment.model.Ticket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;

public class controlador {

    // CAMPOS ESTÁTICOS PARA MANTENER EN MEMORIA LA SESIÓN DEL USUARIO
    private static String nombre;
    private static String apellido;
    private static String codigo;



    // MÉTODOS DE GESTIÓN DE SESIÓN (USUARIO LOGUEADO)

    // Guarda los datos en memoria (solo mientras la app está abierta)
    public static void setUsuario(String n, String a, String c) {
        nombre = n;
        apellido = a;
        codigo = c;
    }

    // Comprueba si hay un usuario activo
    public static boolean haySesionActiva() {
        return nombre != null && !nombre.equals("");
    }

    // Getters
    public static String getNombre() { return nombre; }
    public static String getApellido() { return apellido; }
    public static String getCodigo() { return codigo; }


    // GUARDAR SESIÓN EN SharedPreferences (permanece al cerrar app)
    public static void guardarSesion(Context ctx, String nombre, String apellido, String codigo) {

        SharedPreferences prefs = ctx.getSharedPreferences("sesion", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString("nombre", nombre);
        editor.putString("apellido", apellido);
        editor.putString("codigo", codigo);

        editor.apply();  // GUARDAR
    }


    // Cargar la sesión cuando se abre la app
    public static void cargarSesion(Context ctx) {

        SharedPreferences prefs = ctx.getSharedPreferences("sesion", Context.MODE_PRIVATE);

        nombre = prefs.getString("nombre", "");
        apellido = prefs.getString("apellido", "");
        codigo = prefs.getString("codigo", "");
    }


    // Borrar la sesión (logout)
    public static void borrarSesion(Context ctx) {

        SharedPreferences prefs = ctx.getSharedPreferences("sesion", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.clear(); // borrar todo
        editor.apply();

        // También limpiamos las variables en memoria
        nombre = "";
        apellido = "";
        codigo = "";
    }



    // COPIAR ARCHIVO tickets.txt DESDE /assets → /files (solo 1ª vez)
    public static void copiarTicketsDesdeAssets(Context ctx) {

        // Fichero de destino dentro de /data/data/.../files
        File destino = new File(ctx.getFilesDir(), "tickets.txt");

        // Si ya existe, NO lo copiamos (así no perdemos datos)
        if (destino.exists()) {
            return;
        }

        try {

            // Abrimos tickets.txt desde assets
            InputStream is = ctx.getAssets().open("tickets.txt");

            // Abrimos un FileOutputStream para escribir en /files
            FileOutputStream fos = new FileOutputStream(destino);

            // Copiar byte a byte
            int dato = is.read();
            while (dato != -1) {
                fos.write(dato);
                dato = is.read();
            }

            // Cerrar streams
            is.close();
            fos.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // LEER TODOS LOS TICKETS DESDE /files/tickets.txt
    public static ArrayList<Ticket> leerTickets(Context ctx) {

        ArrayList<Ticket> lista = new ArrayList<>();

        // Localizamos el fichero dentro de files/
        File fichero = new File(ctx.getFilesDir(), "tickets.txt");

        try {

            // Abrimos stream de lectura de caracteres
            FileReader fr = new FileReader(fichero);
            BufferedReader br = new BufferedReader(fr);

            // Leemos línea a línea
            String linea = br.readLine();

            while (linea != null) {

                // Cada ticket está separado por ";"
                String[] partes = linea.split(";");

                // Comprobación mínima del formato
                if (partes.length == 5) {

                    int id = Integer.parseInt(partes[0]);
                    String nombre = partes[1];
                    String desc = partes[2];
                    String resol = partes[3];
                    Estado estado = Estado.valueOf(partes[4]);

                    // Creamos el Ticket y lo añadimos a la lista
                    lista.add(new Ticket(id, nombre, desc, resol, estado));

                } else {
                    Log.e("ERROR FORMAT", "Línea incorrecta: " + linea);
                }

                // Pasamos a la siguiente línea
                linea = br.readLine();
            }

            br.close();
            fr.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }



    // GUARDAR TODOS LOS TICKETS DE VUELTA EN EL FICHERO - sobreescribe todo el metodo

    public static void guardarTickets(Context ctx, ArrayList<Ticket> lista) {

        File fichero = new File(ctx.getFilesDir(), "tickets.txt");

        try {

            // FileWriter en modo sobrescribir (false)
            FileWriter fw = new FileWriter(fichero, false);
            BufferedWriter bw = new BufferedWriter(fw);

            // Escribir cada ticket como una línea
            for (int i = 0; i < lista.size(); i++) {

                Ticket t = lista.get(i);

                bw.write(
                        t.getId() + ";" +
                                t.getNombreUsuario() + ";" +
                                t.getDescripcion() + ";" +
                                t.getResolucion() + ";" +
                                t.getEstado().toString()
                );

                bw.newLine();
            }

            bw.close();
            fw.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}


```
# MODEL
Estado.java
```java
package com.example.ejerciciofragment.model;

public enum Estado {
    NUEVO,
    ABIERTO,
    PENDIENTE,
    RESUELTO,
    CERRADO
}

```
Ticket.java
```java
package com.example.ejerciciofragment.model;

public class Ticket {

    private int id;
    private String nombreUsuario;
    private String descripcion;
    private String resolucion;
    private Estado estado;

    public Ticket(int id, String nombreUsuario, String descripcion, String resolucion, Estado estado) {
        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.descripcion = descripcion;
        this.resolucion = resolucion;
        this.estado = estado;
    }

    public int getId() {
        return id;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getResolucion() {
        return resolucion;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public void setResolucion(String resolucion) {
        this.resolucion = resolucion;
    }

    public void setId(int id) {
        this.id = id;
    }
}

```
# VIEW
EditarFragment.java
```java
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


```
HomeFragment.java
```java
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

```
LoginFragment.java
```java
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

```
MainActivity.java
```java
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

        controlador.cargarSesion(this);

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

```
TicketAdapter.java
```java
package com.example.ejerciciofragment.view;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ejerciciofragment.R;
import com.example.ejerciciofragment.model.Ticket;

import java.util.ArrayList;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketViewHolder> {

    private ArrayList<Ticket> lista;

    public TicketAdapter(ArrayList<Ticket> lista) {
        this.lista = lista;
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ticket, parent, false);

        return new TicketViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {
        Ticket t = lista.get(position);

        holder.tvTitulo.setText("Ticket #" + t.getId());
        holder.tvEstadoTexto.setText(t.getEstado().toString());
        holder.tvRemitente.setText("Remitente: " + t.getNombreUsuario());
        holder.tvDescripcion.setText(t.getDescripcion());

        holder.itemView.setOnClickListener(v -> {
            EditarFragment editarFragment = new EditarFragment();

            Bundle bundle = new Bundle();
            bundle.putInt("id", t.getId());
            editarFragment.setArguments(bundle);

            FragmentActivity activity = (FragmentActivity) v.getContext();

            activity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.contenedor, editarFragment)
                    .addToBackStack(null)
                    .commit();
        });

        int color = 0;

        switch (t.getEstado()) {
            case NUEVO:
                color = holder.itemView.getContext().getColor(R.color.estado_nuevo);
                break;
            case ABIERTO:
                color = holder.itemView.getContext().getColor(R.color.estado_abierto);
                break;
            case PENDIENTE:
                color = holder.itemView.getContext().getColor(R.color.estado_pendiente);
                break;
            case RESUELTO:
                color = holder.itemView.getContext().getColor(R.color.estado_resuelto);
                break;
            case CERRADO:
                color = holder.itemView.getContext().getColor(R.color.estado_cerrado);
                break;
        }

        GradientDrawable fondo = (GradientDrawable) holder.itemView.getBackground();
        fondo.setStroke(6, color);
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public class TicketViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitulo, tvRemitente, tvDescripcion, tvEstadoTexto;

        public TicketViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitulo = itemView.findViewById(R.id.tvTitulo);
            tvEstadoTexto = itemView.findViewById(R.id.tvEstadoTexto);
            tvRemitente = itemView.findViewById(R.id.tvRemitente);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcion);
        }
    }

    public void actualizarLista(ArrayList<Ticket> nuevaLista) {
        lista = nuevaLista;
        notifyDataSetChanged();
    }
}

```
# LAYOUT (XML)
activity_main.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/contenedor"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="#FAFAFA" />
```
fragment_editar.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<ScrollView xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="#F8F8F8"
    android:padding="16dp">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical">

        <!-- HEADER (igual que en home) -->
        <LinearLayout
            android:orientation="vertical"
            android:padding="20dp"
            android:background="#C1E9F0"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginBottom="20dp">

            <TextView
                android:id="@+id/tvSaludo"
                android:text="Hola, Usuario"
                android:textSize="22sp"
                android:textStyle="bold"
                android:textColor="#000000"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content" />

            <TextView
                android:id="@+id/tvCodigo"
                android:text="123456"
                android:textSize="15sp"
                android:textColor="#4A4A4A"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_marginTop="4dp" />
        </LinearLayout>

        <!-- TARJETA DE EDICIÓN -->
        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical"
            android:padding="16dp"
            android:background="#FFFFFF"
            android:elevation="3dp"
            android:layout_marginBottom="24dp">

            <!-- Ticket ID -->
            <TextView
                android:text="Ticket"
                android:textStyle="bold"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content" />

            <TextView
                android:id="@+id/tvTicketId"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:background="#E0E0E0"
                android:padding="10dp"
                android:text="Ticket #X"
                android:layout_marginTop="4dp" />

            <!-- Estado -->
            <TextView
                android:text="Estado"
                android:textStyle="bold"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_marginTop="12dp" />

            <Spinner
                android:id="@+id/spEstado"
                android:layout_width="match_parent"
                android:layout_height="45dp"
                android:background="#E0E0E0"
                android:paddingLeft="8dp"
                android:paddingRight="8dp"
                android:layout_marginTop="4dp" />

            <!-- Nombre / remitente -->
            <TextView
                android:text="Remitente"
                android:textStyle="bold"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_marginTop="12dp" />

            <TextView
                android:id="@+id/tvNombreUsuario"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:background="#E0E0E0"
                android:padding="10dp"
                android:text="Nombre Apellidos"
                android:layout_marginTop="4dp" />

            <!-- Descripción del problema -->
            <TextView
                android:text="Descripción del problema:"
                android:textStyle="bold"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_marginTop="16dp" />

            <EditText
                android:id="@+id/etDescripcion"
                android:layout_width="match_parent"
                android:layout_height="120dp"
                android:background="#D3D3D3"
                android:padding="10dp"
                android:gravity="top|start"
                android:inputType="textMultiLine"
                android:text=""
                android:layout_marginTop="4dp" />

            <!-- Resolución del problema -->
            <TextView
                android:text="Resolución del problema:"
                android:textStyle="bold"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_marginTop="16dp" />

            <EditText
                android:id="@+id/etResolucion"
                android:layout_width="match_parent"
                android:layout_height="120dp"
                android:background="#E0F7FA"
                android:padding="10dp"
                android:gravity="top|start"
                android:inputType="textMultiLine"
                android:text=""
                android:layout_marginTop="4dp" />

        </LinearLayout>

        <!-- BOTÓN GUARDAR -->
        <Button
            android:id="@+id/btnGuardar"
            android:layout_width="match_parent"
            android:layout_height="55dp"
            android:text="Guardar"
            android:textAllCaps="false"
            android:textSize="16sp"
            android:textColor="#000000"
            android:backgroundTint="#7BC2E0" />

    </LinearLayout>

</ScrollView>

```
fragment_home.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical"
    android:padding="16dp"
    android:background="#F8F8F8"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <!-- HEADER -->
    <LinearLayout
        android:orientation="vertical"
        android:padding="20dp"
        android:background="#C1E9F0"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginBottom="20dp"
        android:gravity="center_vertical">

        <TextView
            android:id="@+id/tvSaludo"
            android:text="Hola, Usuario"
            android:textSize="22sp"
            android:textStyle="bold"
            android:textColor="#000000"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"/>

        <TextView
            android:id="@+id/tvCodigo"
            android:text="123456"
            android:textSize="15sp"
            android:textColor="#4A4A4A"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"/>

    </LinearLayout>

    <!-- FILTRO -->
    <TextView
        android:text="Buscar por estado"
        android:textColor="#777777"
        android:paddingBottom="5dp"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"/>

    <Spinner
        android:id="@+id/spFiltro"
        android:background="@drawable/borde_spinner"
        android:padding="12dp"
        android:layout_width="match_parent"
        android:layout_height="45dp"
        android:layout_marginBottom="20dp"/>

    <!-- LISTA -->
    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/rvTickets"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"/>

    <!-- BOTONES ABAJO -->
    <LinearLayout
        android:orientation="horizontal"
        android:layout_marginTop="20dp"
        android:weightSum="2"
        android:layout_width="match_parent"
        android:layout_height="wrap_content">

        <Button
            android:id="@+id/btnNuevo"
            android:text="Nuevo ticket"
            android:backgroundTint="#A6EFD3"
            android:layout_weight="1"
            android:textColor="#000"
            android:layout_width="0dp"
            android:layout_height="50dp"/>

        <Button
            android:id="@+id/btnCerrar"
            android:text="Cerrar sesión"
            android:backgroundTint="#7BC2E0"
            android:layout_weight="1"
            android:textColor="#000"
            android:layout_width="0dp"
            android:layout_height="50dp"
            android:layout_marginStart="12dp"/>

    </LinearLayout>

</LinearLayout>

```
fragment_login.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical"
    android:gravity="center_horizontal"
    android:paddingLeft="40dp"
    android:paddingRight="40dp"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="#C8F2F5">

    <!-- ESPACIADO SUPERIOR -->
    <View
        android:layout_width="match_parent"
        android:layout_height="120dp"/>

    <!-- TÍTULO LOGIN -->
    <TextView
        android:id="@+id/tvTitulo"
        android:text="Login"
        android:textSize="24sp"
        android:textColor="#000000"
        android:textStyle="bold"
        android:gravity="center"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginBottom="40dp" />

    <!-- NOMBRE -->
    <EditText
        android:id="@+id/etNombre"
        android:hint="Nombre"
        android:background="@android:color/transparent"
        android:textColor="#000"
        android:textColorHint="#888"
        android:padding="0dp"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"/>

    <View
        android:layout_width="match_parent"
        android:layout_height="2dp"
        android:background="#000000"
        android:layout_marginBottom="24dp"/>

    <!-- APELLIDOS -->
    <EditText
        android:id="@+id/etApellido"
        android:hint="Apellidos"
        android:background="@android:color/transparent"
        android:textColor="#000"
        android:textColorHint="#888"
        android:padding="0dp"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"/>

    <View
        android:layout_width="match_parent"
        android:layout_height="2dp"
        android:background="#000000"
        android:layout_marginBottom="24dp"/>

    <!-- CÓDIGO -->
    <EditText
        android:id="@+id/etCodigo"
        android:hint="Código del trabajador"
        android:background="@android:color/transparent"
        android:textColor="#000"
        android:textColorHint="#888"
        android:padding="0dp"
        android:inputType="number"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"/>

    <View
        android:layout_width="match_parent"
        android:layout_height="2dp"
        android:background="#000000"
        android:layout_marginBottom="40dp"/>

    <!-- BOTÓN ENTRAR -->


    <Button
        android:id="@+id/btnEntrar"
        android:text="Entrar"
        android:textSize="16sp"
        android:textStyle="bold"
        android:textColor="#FFFFFF"
        android:backgroundTint="#4FD3E5"
        android:layout_width="match_parent"
        android:layout_height="50dp"
        android:layout_marginTop="10dp"
        android:layout_marginBottom="10dp"></Button>

</LinearLayout>

```
item_ticket.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical"
    android:padding="14dp"
    android:layout_margin="10dp"
    android:background="@drawable/ticket_bg"
    android:layout_width="match_parent"
    android:layout_height="wrap_content">

    <!-- FILA SUPERIOR: TITULO + ESTADO -->
    <LinearLayout
        android:orientation="horizontal"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:gravity="center_vertical">

        <TextView
            android:id="@+id/tvTitulo"
            android:text="Ticket #1"
            android:textStyle="bold"
            android:textSize="16sp"
            android:textColor="#000"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1" />

        <TextView
            android:id="@+id/tvEstadoTexto"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="NUEVO"
            android:textStyle="italic"
            android:textColor="#444"/>
    </LinearLayout>

    <!-- REMITENTE -->
    <TextView
        android:id="@+id/tvRemitente"
        android:text="Remitente: Marta Pérez"
        android:layout_marginTop="4dp"
        android:textColor="#333"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content" />

    <!-- DESCRIPCIÓN -->
    <TextView
        android:id="@+id/tvDescripcion"
        android:text="Error: NullPointerException..."
        android:textColor="#555"
        android:layout_marginTop="2dp"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content" />

</LinearLayout>

```
# DRAWABLE
borde_spinner.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android">
    <solid android:color="#FFFFFF"/>
    <corners android:radius="12dp"/>
    <padding android:left="8dp" android:top="8dp" android:right="8dp" android:bottom="8dp"/>
    <stroke android:width="1dp" android:color="#DDDDDD"/>
</shape>

```
ic_launcher_background.xml
ic_launcher_foreground.xml
ticket_bg.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android">
    <solid android:color="@color/gris_suave"/>
    <corners android:radius="14dp"/>
    <padding android:left="8dp" android:top="8dp"
        android:right="8dp" android:bottom="8dp"/>

    <stroke android:width="4dp" android:color="@color/gris_suave"/>
</shape>

```

```
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.example.ejerciciofragment">

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:supportsRtl="true"
        android:theme="@style/Theme.AppCompat.Light.NoActionBar">

        <activity
            android:name=".view.MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

    </application>

</manifest>
```
