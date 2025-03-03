package com.example.sportyhub.UserConfig;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportyhub.Adapters.AjustesAdapter;
import com.example.sportyhub.Modelos.AjusteItem;
import com.example.sportyhub.Modelos.Usuario;
import com.example.sportyhub.R;

import java.util.ArrayList;
import java.util.List;

public class Ajustes extends AppCompatActivity {
    private RecyclerView recyclerView;
    private AjustesAdapter adapter;
    private List<AjusteItem> ajustesList;

    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ajustes_activity);

        usuario = getIntent().getParcelableExtra("usuario", Usuario.class);

        recyclerView = findViewById(R.id.recyclerViewAjustes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ajustesList = new ArrayList<>();
        ajustesList.add(new AjusteItem(R.drawable.ic_profile, "Perfil"));
        ajustesList.add(new AjusteItem(R.drawable.payments, "Historial de Pagos"));
        ajustesList.add(new AjusteItem(R.drawable.info, "Acerca de"));

        adapter = new AjustesAdapter(ajustesList, this);
        recyclerView.setAdapter(adapter);
    }

    public void abrirActividad(String opcion) {
        Intent intent;
        switch (opcion) {
            case "Perfil":
                intent = new Intent(this, EditProfile.class);
                intent.putExtra("usuario", usuario);
                break;
            case "Historial de Pagos":
                intent = new Intent(this, HistorialPagosActivity.class);
                intent.putExtra("usuario", usuario);
                break;
            case "Acerca de":
                intent = new Intent(this, AcercaDe.class);
                break;
            default:
                return;
        }
        startActivity(intent);
    }

}
