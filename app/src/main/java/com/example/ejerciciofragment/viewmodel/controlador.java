package com.example.ejerciciofragment.viewmodel;

import android.content.Context;
import android.content.SharedPreferences;

public class controlador {

    private static String nombre;
    private static String apellido;
    private static String codigo;

    // Guardar datos en memoria y SharedPreferences
    public static void guardarUsuario(Context ctx, String n, String a, String c) {
        nombre = n;
        apellido = a;
        codigo = c;

        SharedPreferences prefs = ctx.getSharedPreferences("usuario", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("nombre", n);
        editor.putString("apellido", a);
        editor.putString("codigo", c);
        editor.apply();
    }

    // Cargar los datos del usuario si existen
    public static boolean cargarUsuario(Context ctx) {
        SharedPreferences prefs = ctx.getSharedPreferences("usuario", Context.MODE_PRIVATE);
        nombre = prefs.getString("nombre", "");
        apellido = prefs.getString("apellido", "");
        codigo = prefs.getString("codigo", "");

        return !nombre.equals("");
    }

    // Borrar sesión
    public static void cerrarSesion(Context ctx) {
        SharedPreferences prefs = ctx.getSharedPreferences("usuario", Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }

    // Getters
    public static String getNombre() { return nombre; }
    public static String getApellido() { return apellido; }
    public static String getCodigo() { return codigo; }
}
