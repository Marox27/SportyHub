package com.example.sportyhub.Fragments;

import android.content.Context;
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.sportyhub.Actividad.ActivityDetails;
import com.example.sportyhub.Adapters.ActividadAdapter;
import com.example.sportyhub.Modelos.Actividad;
import com.example.sportyhub.Modelos.Usuario;
import com.example.sportyhub.R;

import java.util.List;

public class ActivityUserListFragment extends Fragment {

    private List<Actividad> listaActividades;
    private RecyclerView recyclerView;
    private ActividadAdapter actividadAdapter;
    private Usuario usuario;

    private OnActivityInteractionListener listener;

    public ActivityUserListFragment() {
    }
    public ActivityUserListFragment(List<Actividad> listaActividades, Usuario usuario) {
        this.listaActividades = listaActividades;
        this.usuario = usuario;
    }

    // Necesario para poder implementar el refresh de actividades
    public interface OnActivityInteractionListener {
        void recargarActividadesUsuario();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnActivityInteractionListener) {
            listener = (OnActivityInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnActivityInteractionListener");
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_activity_user_list, container, false);

        // Asignamos los elementos de las interfaces.
        recyclerView = rootView.findViewById(R.id.recyclerViewActividadesParticipante);

        // Configurar el RecyclerView con un LinearLayoutManager
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Inicializar el adaptador y asignarlo al RecyclerView
        actividadAdapter = new ActividadAdapter(listaActividades, getActivity());

        actividadAdapter.setOnItemClickListener(new ActividadAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // Acciones a realizar cuando se hace clic en una tarjeta específica
                // Obtener la actividad seleccionada según la posición
                Actividad actividadSeleccionada = listaActividades.get(position);
                Toast.makeText(getContext(), actividadSeleccionada.getTitulo(), Toast.LENGTH_SHORT).show();

                // Crear un Intent para abrir una nueva actividad (reemplaza NuevaActividad.class con el nombre de tu actividad)
                Intent intent = new Intent(getActivity(), ActivityDetails.class);

                // Pasar datos a la nueva actividad, por ejemplo, el ID de la actividad seleccionada
                intent.putExtra("actividad_seleccionada", actividadSeleccionada);
                intent.putExtra("usuario_actual", usuario);

                // Iniciar la nueva actividad
                startActivity(intent);
            }
        });

        SwipeRefreshLayout swipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Aquí realizamos el refresh de actividades
                if (listener != null) {
                    listener.recargarActividadesUsuario();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        recyclerView.setAdapter(actividadAdapter);

        return rootView;
    }

    public void updateData(List<Actividad> actividades) {
        this.listaActividades = actividades;
        if (actividadAdapter != null) {
            actividadAdapter.updateData(actividades);
            actividadAdapter.notifyDataSetChanged();
        }
    }

}
