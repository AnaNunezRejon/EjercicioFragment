package com.example.ejerciciofragment.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.ejerciciofragment.R;

public class EditarFragment extends Fragment {

    private Button btnGuardar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_editar, container, false);

        btnGuardar = vista.findViewById(R.id.btnGuardar);

        // Al pulsar guardar, vuelve al fragment anterior
        btnGuardar.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });

        return vista;
    }
}
