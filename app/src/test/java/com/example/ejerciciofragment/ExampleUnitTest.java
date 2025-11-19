package com.example.ejerciciofragment;


import com.example.ejerciciofragment.model.Estado;
import com.example.ejerciciofragment.model.Ticket;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class ExampleUnitTest {

        @Test
        public void resolucionDebeEmpezarVacia() {
            Ticket t = new Ticket(1, "Ana", "desc", "NO ESTA VACIO", Estado.NUEVO);

            assertEquals("", t.getResolucion());
        }


        // --------------------------------------------------------------
        // 1. Generar siguiente ID correctamente
        // --------------------------------------------------------------
        @Test
        public void generarSiguienteId_esCorrecto() {

            ArrayList<Ticket> lista = new ArrayList<>();
            lista.add(new Ticket(1, "Ana", "Prueba", "", Estado.NUEVO));
            lista.add(new Ticket(5, "Luis", "Prueba", "", Estado.NUEVO));
            lista.add(new Ticket(3, "Marta", "Prueba", "", Estado.NUEVO));

            int max = 0;
            for (Ticket t : lista) {
                if (t.getId() > max) max = t.getId();
            }

            int siguiente = max + 1;

            assertEquals(8, siguiente); // he cambiado de 6 que es el correcto a 8
        }


        // --------------------------------------------------------------
        // 2. Leer correctamente un ticket desde una línea de texto
        // --------------------------------------------------------------
        @Test
        public void leerTicketDesdeLinea() {

            String linea = "7;Ana Núñez;Fallo al iniciar;Reiniciar;ABIERTO";
            String[] p = linea.split(";");

            Ticket t = new Ticket(
                    Integer.parseInt(p[0]),
                    p[1],
                    p[2],
                    p[3],
                    Estado.valueOf(p[4])
            );

            assertEquals(7, t.getId());
            assertEquals("Ana Núñez", t.getNombreUsuario());
            assertEquals("Fallo al iniciar", t.getDescripcion());
            assertEquals("Reiniciar", t.getResolucion());
            assertEquals(Estado.ABIERTO, t.getEstado());
        }


        // --------------------------------------------------------------
        // 3. Cambiar estado → debe actualizarse correctamente
        // --------------------------------------------------------------
        @Test
        public void cambiarEstado_ticketActualizado() {

            Ticket t = new Ticket(1, "Ana", "Test", "", Estado.NUEVO);

            t.setEstado(Estado.RESUELTO);

            assertEquals(Estado.RESUELTO, t.getEstado());
        }


        // --------------------------------------------------------------
        // 4. Un ticket nuevo debe comenzar en estado NUEVO
        // --------------------------------------------------------------
        @Test
        public void ticketNuevoSiempreEsNuevo() {

            Ticket t = new Ticket(
                    99,
                    "Ana",
                    "Prueba",
                    "",
                    Estado.NUEVO
            );

            assertEquals(Estado.NUEVO, t.getEstado());
        }
    }

