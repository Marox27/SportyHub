package com.example.sportyhub.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportyhub.Adapters.EquipoAdapter;
import com.example.sportyhub.Equipo.EquipoDetails;
import com.example.sportyhub.Modelos.Equipo;
import com.example.sportyhub.Modelos.Usuario;
import com.example.sportyhub.R;

import java.util.ArrayList;
import java.util.List;

public class TeamFragment extends Fragment {

    private List<Equipo> listaEquipos = new ArrayList<>();
    private RecyclerView recyclerView;
    private EquipoAdapter equipoAdapter;

    private Usuario usuario;

    // Constructor vacío necesario para evitar el error de NoSuchMethodException
    public TeamFragment() {
        // Constructor vacío requerido
    }

    public TeamFragment(List<Equipo> listaEquipos, Usuario usuario){
        this.listaEquipos = listaEquipos;
        this.usuario = usuario;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = usuario.isAdmin() ? inflater.inflate(R.layout.fragment_equipo_admin, container, false)
                : inflater.inflate(R.layout.fragment_equipo_list, container, false);

        recyclerView = rootView.findViewById(R.id.recyclerViewEquiposLider);
        // Configurar el RecyclerView con un LinearLayoutManager
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Inicializar el adaptador y asignarlo al RecyclerView
        equipoAdapter = new EquipoAdapter(listaEquipos, getActivity());

        equipoAdapter.setOnItemClickListener(new EquipoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // Acciones a realizar cuando se hace clic en una tarjeta específica
                // Obtener la actividad seleccionada según la posición
                Equipo equipoSeleccionado = listaEquipos.get(position);
                Toast.makeText(getContext(), equipoSeleccionado.getNombre(), Toast.LENGTH_SHORT).show();

                // Crear un Intent para abrir una nueva actividad (reemplaza NuevaActividad.class con el nombre de tu actividad)
                Intent intent = new Intent(getActivity(), EquipoDetails.class);

                // Pasar datos a la nueva actividad, por ejemplo, el ID de la actividad seleccionada
                //intent.putExtra("equipo_id", equipoSeleccionado.getId_equipo());
                intent.putExtra("Equipo", equipoSeleccionado);
                intent.putExtra("usuario", usuario);

                // Iniciar la nueva actividad
                startActivity(intent);
            }
        });


        recyclerView.setAdapter(equipoAdapter);



        return rootView;
    }

    public void updateData(List<Equipo> equipos) {
        this.listaEquipos = equipos;
        if (equipoAdapter != null) {
            equipoAdapter.updateData(equipos);
            equipoAdapter.notifyDataSetChanged();
        }
    }

}
