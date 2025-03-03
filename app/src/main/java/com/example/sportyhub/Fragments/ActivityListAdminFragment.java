package com.example.sportyhub.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportyhub.Adapters.ActividadAdapter;
import com.example.sportyhub.Modelos.Actividad;
import com.example.sportyhub.Modelos.Usuario;
import com.example.sportyhub.R;
import com.example.sportyhub.Actividad.ActivityDetails;

import java.util.ArrayList;
import java.util.List;

public class ActivityListAdminFragment extends Fragment {
    private List<Actividad> listaActividades = new ArrayList<>();
    private Usuario usuario;
    private RecyclerView recyclerView;
    private ActividadAdapter actividadAdapter;

    // Constructor vacío necesario para evitar el error de NoSuchMethodException
    public ActivityListAdminFragment() {
        // Constructor vacío requerido
    }

    public ActivityListAdminFragment(List<Actividad> listaActividades, Usuario usuario) {
        this.listaActividades = listaActividades;
        this.usuario = usuario;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_actividades_admin, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewActividades);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        actividadAdapter = new ActividadAdapter(listaActividades, getContext());

        actividadAdapter.setOnItemClickListener(new ActividadAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // Obtener la actividad seleccionada
                Actividad actividadSeleccionada = listaActividades.get(position);
                Toast.makeText(getContext(), actividadSeleccionada.getTitulo(), Toast.LENGTH_SHORT).show();

                // Abrir la pantalla de detalles de la actividad
                // PENDIENTE ACTIVITYADMIN
                Intent intent = new Intent(getActivity(), ActivityDetails.class);
                intent.putExtra("actividad", actividadSeleccionada);
                intent.putExtra("usuario", usuario);

                startActivity(intent);
            }
        });

        recyclerView.setAdapter(actividadAdapter);
        return view;
    }

    public void updateData(List<Actividad> actividades) {
        this.listaActividades = actividades;
        if (actividadAdapter != null) {
            actividadAdapter.updateActividades(actividades);
            actividadAdapter.notify();
        }
    }
}

