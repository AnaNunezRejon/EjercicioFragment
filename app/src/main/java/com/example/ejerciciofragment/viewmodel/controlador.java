package com.example.ejerciciofragment.viewmodel;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.ejerciciofragment.model.Estado;
import com.example.ejerciciofragment.model.Ticket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

public class controlador {

    private static String nombre;
    private static String apellido;
    private static String codigo;

    // Guardar datos del usuario
    public static void setUsuario(String n, String a, String c) {
        nombre = n;
        apellido = a;
        codigo = c;
    }

    // Comprobar si hay usuario activo
    public static boolean haySesionActiva() {
        return nombre != null && !nombre.equals("");
    }

    // Obtener datos
    public static String getNombre() { return nombre; }
    public static String getApellido() { return apellido; }
    public static String getCodigo() { return codigo; }



    public static void guardarSesion(Context ctx, String nombre, String apellido, String codigo) {
        SharedPreferences prefs = ctx.getSharedPreferences("sesion", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("nombre", nombre);
        editor.putString("apellido", apellido);
        editor.putString("codigo", codigo);
        editor.apply();
    }

    public static void cargarSesion(Context ctx) {
        SharedPreferences prefs = ctx.getSharedPreferences("sesion", Context.MODE_PRIVATE);

        nombre = prefs.getString("nombre", "");
        apellido = prefs.getString("apellido", "");
        codigo = prefs.getString("codigo", "");
    }

    public static void borrarSesion(Context ctx) {
        SharedPreferences prefs = ctx.getSharedPreferences("sesion", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();

        nombre = "";
        apellido = "";
        codigo = "";
    }


    // Leer tickets desde fichero de texto
    public static ArrayList<Ticket> leerTickets(Context ctx) {
        ArrayList<Ticket> lista = new ArrayList<>();

        // 1. Localizamos la ruta del fichero en la memoria interna de la app
        File directorio = ctx.getFilesDir();
        File fichero = new File(directorio, "tickets.txt");

        // 2. Si el fichero NO existe, lo creamos con un ticket de ejemplo
        if (!fichero.exists()) {
            try {
                // FileWriter + BufferedWriter -> Ficheros de caracteres
                FileWriter fw = new FileWriter(fichero);
                BufferedWriter bw = new BufferedWriter(fw);

                // Formato: id;nombre;descripcion;resolucion;ESTADO
                bw.write("1;Usuario de prueba;Descripción de ejemplo;Resolución de ejemplo;NUEVO");
                bw.newLine();

                bw.close();
                fw.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 3. Leemos el fichero línea a línea con FileReader + BufferedReader
        try {
            FileReader fr = new FileReader(fichero);
            BufferedReader br = new BufferedReader(fr);

            String linea = br.readLine();

            while (linea != null) {
                String[] partes = linea.split(";");

                // Comprobamos que la línea tiene las 5 partes
                if (partes.length == 5) {
                    int id = Integer.parseInt(partes[0]);
                    String nombreTicket = partes[1];
                    String desc = partes[2];
                    String resol = partes[3];
                    Estado estado = Estado.valueOf(partes[4]);

                    Ticket t = new Ticket(id, nombreTicket, desc, resol, estado);
                    lista.add(t);
                }

                linea = br.readLine();
            }

            br.close();
            fr.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

}


