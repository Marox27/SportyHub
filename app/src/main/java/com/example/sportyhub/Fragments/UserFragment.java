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

import com.example.sportyhub.Adapters.EquipoAdapter;
import com.example.sportyhub.Adapters.UsuarioAdapter;
import com.example.sportyhub.Equipo.EquipoDetails;
import com.example.sportyhub.Modelos.Equipo;
import com.example.sportyhub.Modelos.Usuario;
import com.example.sportyhub.R;
import com.example.sportyhub.Usuario.UsuarioDetails;

import java.util.ArrayList;
import java.util.List;

public class UserFragment extends Fragment {
    private List<Usuario> listaUsuarios = new ArrayList<>();
    private RecyclerView recyclerView;
    private UsuarioAdapter usuarioAdapter;
    private Usuario usuarioAdmin;

    private int id_usuario;

    // Constructor vacío necesario para evitar el error de NoSuchMethodException
    public UserFragment() {
        // Constructor vacío requerido
    }
    public UserFragment(List<Usuario> listaUsuarios, Usuario usuario) {
        this.listaUsuarios = listaUsuarios;
        this.usuarioAdmin = usuario;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_usuarios, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewUsuarios);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        usuarioAdapter = new UsuarioAdapter(listaUsuarios, getContext());

        usuarioAdapter.setOnItemClickListener(new UsuarioAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // Acciones a realizar cuando se hace clic en una tarjeta específica
                // Obtener la actividad seleccionada según la posición
                Usuario usuarioSeleccionado = listaUsuarios.get(position);
                Toast.makeText(getContext(), usuarioSeleccionado.getNickname(), Toast.LENGTH_SHORT).show();

                // Crear un Intent para abrir una nueva actividad (reemplaza NuevaActividad.class con el nombre de tu actividad)
                Intent intent = new Intent(getActivity(), UsuarioDetails.class);

                //Pasar datos a la nueva actividad, por ejemplo, el ID de la actividad seleccionada
                intent.putExtra("usuario", usuarioSeleccionado);
                intent.putExtra("usuario_admin", usuarioAdmin);

                // Iniciar la nueva actividad
                startActivity(intent);
            }
        });

        recyclerView.setAdapter(usuarioAdapter);
        return view;
    }

    public void updateData(List<Usuario> usuarios) {
        this.listaUsuarios = usuarios;
        if (usuarioAdapter != null) {
            usuarioAdapter.actualizarLista(usuarios);
            usuarioAdapter.notifyDataSetChanged();
        }
    }



}

