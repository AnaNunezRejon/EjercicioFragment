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
