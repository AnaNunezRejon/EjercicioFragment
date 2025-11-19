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


    private ArrayList<Ticket> lista;// Lista de tickets que se mostrará en el RecyclerView

    // Constructor que recibe la lista
    public TicketAdapter(ArrayList<Ticket> lista) {
        this.lista = lista;
    }


    // CREA LA VISTA DE CADA ITEM (item_ticket.xml)
    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // Infla (crea) la vista del layout item_ticket
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ticket, parent, false);

        return new TicketViewHolder(vista);
    }


    // RELLENA CADA ITEM CON LA INFORMACIÓN DEL TICKET
    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {

        // Obtenemos el ticket correspondiente a esa posición
        Ticket t = lista.get(position);

        // Rellenamos los campos de la tarjeta
        holder.tvTitulo.setText("Ticket #" + t.getId());
        holder.tvRemitente.setText("Remitente: " + t.getNombreUsuario());
        holder.tvDescripcion.setText(t.getDescripcion());

        // CUANDO SE PULSA EL ITEM → ABRE EditarFragment
        holder.itemView.setOnClickListener(v -> {

            // Creamos el fragmento de edición
            EditarFragment editarFragment = new EditarFragment();

            // Pasamos el ID del ticket como argumento
            Bundle bundle = new Bundle();
            bundle.putInt("id", t.getId());
            editarFragment.setArguments(bundle);

            // Abrimos el fragmento usando el contenedor principal
            FragmentActivity activity = (FragmentActivity) v.getContext();

            activity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.contenedor, editarFragment)
                    .addToBackStack(null)  // para poder volver atrás
                    .commit();
        });


        // CAMBIAR EL BORDE SEGÚN EL ESTADO DEL TICKET
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

        // Aplicamos el borde de color al fondo del item
        GradientDrawable fondo = (GradientDrawable) holder.itemView.getBackground();
        fondo.setStroke(6, color); // Grosor 6px, color depende del estado
    }


    // Nº de elementos que tiene la lista
    @Override
    public int getItemCount() {
        return lista.size();
    }


    // CLASE QUE REPRESENTA CADA TARJETA (item_ticket)
    public class TicketViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitulo, tvRemitente, tvDescripcion;

        public TicketViewHolder(@NonNull View itemView) {
            super(itemView);

            // Conectamos las vistas del XML con variables Java
            tvTitulo = itemView.findViewById(R.id.tvTitulo);
            tvRemitente = itemView.findViewById(R.id.tvRemitente);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcion);
        }
    }


    // MeTODO PARA ACTUALIZAR LA LISTA (lo usa HomeFragment en onResume)
    public void actualizarLista(ArrayList<Ticket> nuevaLista) {
        lista = nuevaLista;
        notifyDataSetChanged(); // refresca la pantalla
    }
}
