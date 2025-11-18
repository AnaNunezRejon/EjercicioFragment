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


    //Crear archivo inicial en /files si no existe
    public static void copiarTicketsDesdeAssets(Context ctx) {

        File destino = new File(ctx.getFilesDir(), "tickets.txt");

        if (destino.exists()) {

            return;
        }

        try {
            InputStream is = ctx.getAssets().open("tickets.txt");
            FileOutputStream fos = new FileOutputStream(destino);

            int dato = is.read();
            while (dato != -1) {
                fos.write(dato);
                dato = is.read();
            }

            is.close();
            fos.close();



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Ticket> leerTickets(Context ctx) {

        ArrayList<Ticket> lista = new ArrayList<>();
        File fichero = new File(ctx.getFilesDir(), "tickets.txt");

        try {
            FileReader fr = new FileReader(fichero);
            BufferedReader br = new BufferedReader(fr);

            String linea = br.readLine();

            while (linea != null) {


                String[] partes = linea.split(";");

                if (partes.length == 5) {
                    int id = Integer.parseInt(partes[0]);
                    String nombre = partes[1];
                    String desc = partes[2];
                    String resol = partes[3];
                    Estado estado = Estado.valueOf(partes[4]);

                    lista.add(new Ticket(id, nombre, desc, resol, estado));
                } else {
                    Log.e("ERROR", "Formato de línea incorrecto: " + linea);
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


    public static Ticket getTicketPorId(ArrayList<Ticket> lista, int id) {

            for (int i = 0; i < lista.size(); i++) {
                Ticket t = lista.get(i);
                if(t.getId()== id){
                    return t;
                }

            }
        return null;
    }

    public static void guardarTickets(Context ctx, ArrayList<Ticket> lista) {

        File fichero = new File(ctx.getFilesDir(), "tickets.txt");

        try {
            FileWriter fw = new FileWriter(fichero, false); // sobrescribir todo
            BufferedWriter bw = new BufferedWriter(fw);

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


