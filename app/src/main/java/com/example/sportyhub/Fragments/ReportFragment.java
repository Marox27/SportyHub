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

import com.example.sportyhub.Adapters.ReporteAdapter;
import com.example.sportyhub.Modelos.Reporte;
import com.example.sportyhub.Modelos.Usuario;
import com.example.sportyhub.R;
import com.example.sportyhub.Reporte.ReporteDetails;

import java.util.List;

public class ReportFragment extends Fragment {
    private List<Reporte> reporteList;
    private Usuario usuarioAdministrador;
    private RecyclerView recyclerView;
    private ReporteAdapter reporteAdapter;


    public ReportFragment(List<Reporte> reporteList){
        this.reporteList = reporteList;
    }

    public ReportFragment(List<Reporte> reporteList, Usuario usuario) {
        this.reporteList = reporteList;
        this.usuarioAdministrador = usuario;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reportes, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewReportesReportante);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        reporteAdapter = new ReporteAdapter(reporteList, getActivity());

        reporteAdapter.setOnItemClickListener(position -> {
            // Acciones a realizar cuando se hace clic en una tarjeta específica
            // Obtener la actividad seleccionada según la posición
            Reporte reporteSeleccionado = reporteList.get(position);
            Toast.makeText(getContext(), reporteSeleccionado.getMotivo(), Toast.LENGTH_SHORT).show();

            // Crear un Intent para abrir una nueva actividad (reemplaza NuevaActividad.class con el nombre de tu actividad)
            Intent intent = new Intent(getActivity(), ReporteDetails.class);

            //Pasar datos a la nueva actividad, por ejemplo, el ID de la actividad seleccionada
            intent.putExtra("reporte", reporteSeleccionado);
            intent.putExtra("usuario", usuarioAdministrador);

            // Iniciar la nueva actividad
            startActivity(intent);
        });

        recyclerView.setAdapter(reporteAdapter);
        return view;
    }
    /*
    public void updateData(List<Reporte> reportes) {
        this.reporteList = reportes;
        if (reporteAdapter != null) {
            reporteAdapter.u(reportes);
            reporteAdapter.notifyDataSetChanged();
        }
        */
}