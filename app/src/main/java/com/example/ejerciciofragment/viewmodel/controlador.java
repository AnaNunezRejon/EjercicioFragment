package com.example.ejerciciofragment.viewmodel;

import android.content.Context;

import com.example.ejerciciofragment.model.Estado;
import com.example.ejerciciofragment.model.Ticket;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
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

    // Cerrar sesión
    public static void cerrarSesion() {
        nombre = "";
        apellido = "";
        codigo = "";
    }

    public static ArrayList<Ticket> leerTickets(Context ctx){
        ArrayList<Ticket> lista = new ArrayList<>();
        try{

            File file=new File(ctx.getFilesDir()), "tickets."
            InputStream is = ctx.getAssets().open("tickets.txt");
            BufferedReader br = new BufferedReader( new InputStreamReader(is));
            String linea;
            while((linea = br.readLine()) != null){
                String[] partes = linea.split(";");
                int id = Integer.parseInt(partes[0]);
                String nombre = partes[1];
                String desc = partes[2];
                String resol = partes[3];
                Estado estado = Estado.valueOf(partes[4]);


                lista.add(new Ticket(id,nombre,desc, resol, estado));
            }
            br.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return lista;
    }


}


