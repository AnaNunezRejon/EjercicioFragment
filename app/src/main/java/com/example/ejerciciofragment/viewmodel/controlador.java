package com.example.ejerciciofragment.viewmodel;

import android.content.Context;

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
}
