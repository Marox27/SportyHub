package com.example.sportyhub.Fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.sportyhub.Login;
import com.example.sportyhub.Modelos.Usuario;
import com.example.sportyhub.R;
import com.example.sportyhub.RegistroPaso3;
import com.example.sportyhub.Reporte.CrearAviso;

public class AdminOtherFragment extends Fragment {
    Usuario usuario_admin;

    public AdminOtherFragment(Usuario usuario) {
        this.usuario_admin = usuario;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_other_layout, container, false);

        CardView cardOpcion1 = view.findViewById(R.id.card_opcion1);
        CardView cardOpcion2 = view.findViewById(R.id.card_opcion2);
        CardView cardOpcion3 = view.findViewById(R.id.card_opcion3);

        cardOpcion1.setOnClickListener(v -> {
           Intent intent = new Intent(getContext(), RegistroPaso3.class);
           intent.putExtra("admin", true);
           intent.putExtra("usuario", usuario_admin);
           startActivity(intent);
        });

        cardOpcion2.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), CrearAviso.class);
            intent.putExtra("usuario_admin", usuario_admin);
            startActivity(intent);
        });

        cardOpcion3.setOnClickListener(v -> {
            cerrarSesion();
        });

        return view;
    }

    // Se borran las credenciales guardadas del usuario y lo devuelve a la actividad de Login
    private void cerrarSesion() {
        SharedPreferences preferences = getContext().getSharedPreferences("Credenciales", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear(); // Elimina todos los datos
        editor.apply();

        // Vuelve a la pantalla de inicio de sesión
        Intent intent = new Intent(getContext(), Login.class);
        startActivity(intent);
        getActivity().finish();
    }

}

