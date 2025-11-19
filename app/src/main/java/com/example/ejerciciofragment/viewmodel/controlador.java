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

