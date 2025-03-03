package com.example.sportyhub.Usuario;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sportyhub.Actividad.ActivityDetails;
import com.example.sportyhub.Adapters.ActividadAdapter;
import com.example.sportyhub.Adapters.EquipoAdapter;
import com.example.sportyhub.Adapters.ReporteAdapter;
import com.example.sportyhub.Adapters.PagoAdapter;
import com.example.sportyhub.Api.ApiClient;
import com.example.sportyhub.Api.ApiService;
import com.example.sportyhub.Equipo.EquipoDetails;
import com.example.sportyhub.Modelos.Actividad;
import com.example.sportyhub.Modelos.Equipo;
import com.example.sportyhub.Modelos.Pago;
import com.example.sportyhub.Modelos.Reporte;
import com.example.sportyhub.Modelos.Usuario;
import com.example.sportyhub.R;
import com.example.sportyhub.Reporte.ReporteDetails;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsuarioDetails extends AppCompatActivity {
    ImageView ivUserPfp;

    TextView tvUserNickname, tvUserNombre, tvUserApellidos, tvUserCorreo, tvUserCiudad,
            tvUserFecha, tvUserAdmin, tvUserActivo;

    TextView desplegableActividades, desplegableActividadesCreadas, desplegableActividadesParticipadas;
    TextView desplegableEquipos, desplegableEquiposLider, desplegableEquiposMiembro;
    TextView desplegableReportes, desplegableReportesReportante, desplegableReportesReportado;
    TextView desplegablePagos;

    RecyclerView recyclerViewActividadesCreadas, recyclerViewActividadesParticipante;
    RecyclerView recyclerViewEquiposLider, recyclerViewEquiposMiembro;
    RecyclerView recyclerViewReportesReportante, recyclerViewReportesReportado;
    RecyclerView recyclerViewPagos;

    Button buttonBanear;
    Usuario usuario = null, usuarioAdmin = null;
    List<Actividad> listaActividadesCreadas, listaActividadesParticipadas;
    List<Reporte> listaReporteRealizados, listaReportesRecibidos;
    List<Equipo> listaEquiposUsuario, listaEquiposCreados, listaEquiposMiembro;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.main_screen);
        setContentView(R.layout.usuario_detalles);

        // Obtenemos la información que se ha pasado por el fragment.
        usuario = getIntent().getParcelableExtra("usuario", Usuario.class);
        usuarioAdmin = getIntent().getParcelableExtra("usuario_admin", Usuario.class);

        ivUserPfp = findViewById(R.id.ivUserPfp);
        tvUserNickname = findViewById(R.id.textViewUserNickname);
        tvUserNombre = findViewById(R.id.textViewNombreUsuarioPlaceholder);
        tvUserApellidos = findViewById(R.id.textViewApellidosUsuarioPlaceholder);
        tvUserCorreo = findViewById(R.id.textViewCorreoUsuarioPlaceholder);
        tvUserCiudad = findViewById(R.id.textViewCiudadUsuarioPlaceholder);
        tvUserFecha = findViewById(R.id.textViewNacimientoUsuarioPlaceholder);
        tvUserAdmin = findViewById(R.id.textViewAdminUsuarioPlaceholder);
        tvUserActivo = findViewById(R.id.textViewActivoUsuarioPlaceholder);

        // Asignamos los TextView que actuaran como desplegables
        // Desplegables de actividades
        desplegableActividades = findViewById(R.id.desplegable_actividades_usuario);
        desplegableActividadesCreadas = findViewById(R.id.desplegable_actividades_usuario_creadas);
        desplegableActividadesParticipadas = findViewById(R.id.desplegable_actividades_usuario_participadas);

        // Desplegables de Equipos
        desplegableEquipos = findViewById(R.id.desplegable_equipos_usuario);
        desplegableEquiposLider = findViewById(R.id.desplegable_equipos_usuario_lider);
        desplegableEquiposMiembro = findViewById(R.id.desplegable_equipos_usuario_miembro);

        // Desplegables de reportes
        desplegableReportes = findViewById(R.id.desplegable_reportes_usuario);
        desplegableReportesReportante = findViewById(R.id.desplegable_reportes_usuario_reportante);
        desplegableReportesReportado = findViewById(R.id.desplegable_reportes_usuario_reportado);

        // Desplegable de Pagos
        desplegablePagos = findViewById(R.id.desplegable_pagos_usuario);


        // Obtenemos y asignamos los recyclerViews del layout
        // RecyclerViews de actividades
        recyclerViewActividadesCreadas = findViewById(R.id.recyclerViewActividadesCreadas);
        recyclerViewActividadesParticipante = findViewById(R.id.recyclerViewActividadesParticipante);

        // RecyclerViews de equipos
        recyclerViewEquiposLider = findViewById(R.id.recyclerViewEquiposLider);
        recyclerViewEquiposMiembro = findViewById(R.id.recyclerViewEquiposMiembro);

        // RecyclerViews de reportes
        recyclerViewReportesReportante = findViewById(R.id.recyclerViewReportesReportante);
        recyclerViewReportesReportado = findViewById(R.id.recyclerViewReportesReportado);

        // RecyclerView de Pagos
        recyclerViewPagos = findViewById(R.id.recyclerViewPagos);

        // Botones
        buttonBanear = findViewById(R.id.btnUnirse);

        // Obtenemos el id del usuario a partir del Objeto Usuario pasado por el intent
        int id_usuario = usuario.getIdUsuario().intValue();

        // Cargamos los datos a los diferentes recyclerViews
        cargarListaActvidadesUsuario(id_usuario);
        cargarListasDeEquipos(id_usuario);
        cargarListaReportesUsuario(id_usuario);
        cargarListaPagosUsuario(id_usuario);

        // Asignamos los datos del usuario seleccionado
        tvUserNickname.setText(usuario.getNickname());
        tvUserNombre.setText(usuario.getNombre());
        tvUserApellidos.setText(usuario.getApellidos());
        tvUserCorreo.setText(usuario.getEmail());
        tvUserCiudad.setText(usuario.getCiudad());
        tvUserFecha.setText(usuario.getFecha_nacimiento());
        tvUserAdmin.setText(usuario.isAdmin()? "Sí": "No");
        tvUserActivo.setText(usuario.isBaneado()? "Sí": "No");

        // Cargar la imagen desde la URL usando Glide
        Glide.with(this)
                .load(usuario.getPfp())
                .placeholder(R.drawable.default_pfp) // Imagen por defecto mientras carga
                .error(R.drawable.error_placeholder)       // Imagen por defecto si ocurre un error
                .into(ivUserPfp);

        ivUserPfp.setScaleType(ImageView.ScaleType.CENTER_CROP);


        desplegableActividades.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (desplegableActividadesCreadas.getVisibility() == View.GONE){
                    desplegableActividadesCreadas.setVisibility(View.VISIBLE);
                    desplegableActividadesParticipadas.setVisibility(View.VISIBLE);
                }else {
                    desplegableActividadesCreadas.setVisibility(View.GONE);
                    desplegableActividadesParticipadas.setVisibility(View.GONE);
                    recyclerViewActividadesCreadas.setVisibility(View.GONE);
                    recyclerViewActividadesParticipante.setVisibility(View.GONE);
                }
            }
        });

        desplegableActividadesCreadas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (recyclerViewActividadesCreadas.getVisibility() == View.GONE){
                    recyclerViewActividadesCreadas.setVisibility(View.VISIBLE);
                }else {
                    recyclerViewActividadesCreadas.setVisibility(View.GONE);
                }
            }
        });

        desplegableActividadesParticipadas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (recyclerViewActividadesParticipante.getVisibility() == View.GONE){
                    recyclerViewActividadesParticipante.setVisibility(View.VISIBLE);
                }else {
                    recyclerViewActividadesParticipante.setVisibility(View.GONE);
                }
            }
        });

        desplegableEquipos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (desplegableEquiposLider.getVisibility() == View.GONE){
                    desplegableEquiposLider.setVisibility(View.VISIBLE);
                    desplegableEquiposMiembro.setVisibility(View.VISIBLE);
                }else {
                    desplegableEquiposLider.setVisibility(View.GONE);
                    desplegableEquiposMiembro.setVisibility(View.GONE);
                    recyclerViewEquiposLider.setVisibility(View.GONE);
                    recyclerViewEquiposMiembro.setVisibility(View.GONE);
                }
            }
        });

        desplegableEquiposLider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (recyclerViewEquiposLider.getVisibility() == View.GONE){
                    recyclerViewEquiposLider.setVisibility(View.VISIBLE);
                }else {
                    recyclerViewEquiposLider.setVisibility(View.GONE);
                }
            }
        });

        desplegableEquiposMiembro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (recyclerViewEquiposMiembro.getVisibility() == View.GONE){
                    recyclerViewEquiposMiembro.setVisibility(View.VISIBLE);
                }else {
                    recyclerViewEquiposMiembro.setVisibility(View.GONE);
                }
            }
        });

        desplegableReportes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (desplegableReportesReportante.getVisibility() == View.GONE){
                    desplegableReportesReportante.setVisibility(View.VISIBLE);
                    desplegableReportesReportado.setVisibility(View.VISIBLE);
                }else {
                    desplegableReportesReportante.setVisibility(View.GONE);
                    desplegableReportesReportado.setVisibility(View.GONE);
                    recyclerViewReportesReportante.setVisibility(View.GONE);
                    recyclerViewReportesReportado.setVisibility(View.GONE);
                }
            }
        });

        desplegableReportesReportante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (recyclerViewReportesReportante.getVisibility() == View.GONE){
                    recyclerViewReportesReportante.setVisibility(View.VISIBLE);
                }else {
                    recyclerViewReportesReportante.setVisibility(View.GONE);
                }
            }
        });

        desplegableReportesReportado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (recyclerViewReportesReportante.getVisibility() == View.GONE){
                    recyclerViewReportesReportado.setVisibility(View.VISIBLE);
                }else {
                    recyclerViewReportesReportado.setVisibility(View.GONE);
                }
            }
        });


        desplegablePagos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (recyclerViewPagos.getVisibility() == View.GONE){
                    recyclerViewPagos.setVisibility(View.VISIBLE);
                }else {
                    recyclerViewPagos.setVisibility(View.GONE);
                }
            }
        });


        buttonBanear.setText(usuario.isBaneado() ? "Quitar baneo" : "Banear al usuario");
        buttonBanear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle(usuario.isBaneado() ? "Quitar baneo" : "Banear usuario")
                        .setMessage(usuario.isBaneado() ? "¿Seguro que quieres quitar el baneo al usuario?"
                                : "¿Seguro que deseas banear al usuario?")
                        .setPositiveButton("Sí", (dialog, which) -> {
                            if (usuario.isBaneado()) {
                                quitarBaneoUsuario(id_usuario);
                            } else{
                                banearUsuario(id_usuario);
                            }
                        })
                        .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss()) // Cierra el diálogo si elige "No"
                                    .show();
            }
        });


    }

    private void quitarBaneoUsuario(int idUsuario) {
        ApiService apiService = ApiClient.getApiService(this);
        Call<Boolean> call = apiService.desbanearUsuario(idUsuario);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boolean exito = response.body();
                    Toast.makeText(getApplicationContext(), exito ? "Se ha eliminado el baneo." :
                            "No se ha podido quitar el baneo.", Toast.LENGTH_LONG).show();
                    buttonBanear.setText("Banear al usuario");
                }
            }
            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getApplicationContext(), "Error al quitar el baneao al usuario.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void banearUsuario(int idUsuario) {
        ApiService apiService = ApiClient.getApiService(this);
        Call<Boolean> call = apiService.banearUsuario(idUsuario);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boolean exito = response.body();
                    Toast.makeText(getApplicationContext(), exito ? "Se ha baneado al usuario." :
                            "No se ha podido banear al usuario.", Toast.LENGTH_LONG).show();
                    buttonBanear.setText("Quitar baneo");
                }
            }
            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getApplicationContext(), "Error al quitar el baneao al usuario.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarListaActvidadesUsuario(int idUsuario) {
        ApiService apiService = ApiClient.getApiService(this);

        apiService.getActividadesUsuario(idUsuario).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<Actividad>> call, Response<List<Actividad>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.e("API","Miembros del equipo cargados.");
                    List<Actividad> listaActividadesUsuario = response.body();
                    List<Actividad> listaActividadesCreadas = new ArrayList<>();
                    List<Actividad> listaActividadesParticipadas = new ArrayList<>();

                    for (Actividad actividad: listaActividadesUsuario) {
                        if (actividad.getCreador() == idUsuario){
                            listaActividadesCreadas.add(actividad);
                        }else {
                            listaActividadesParticipadas.add(actividad);
                        }
                    }

                    ActividadAdapter adapterParticipadas = new ActividadAdapter(listaActividadesParticipadas,getApplicationContext());
                    recyclerViewActividadesParticipante.setAdapter(adapterParticipadas);
                    recyclerViewActividadesParticipante.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    recyclerViewActividadesParticipante.setVisibility(View.VISIBLE);

                    adapterParticipadas.setOnItemClickListener(new ActividadAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            Actividad actividadSeleccionada = listaActividadesUsuario.get(position);
                            // Llamar a la nueva Activity, pasando la información de la actividad
                            Intent intent = new Intent(getApplicationContext(), ActivityDetails.class);
                            intent.putExtra("usuario_actual", usuarioAdmin);
                            intent.putExtra("actividad_seleccionada", actividadSeleccionada);  // Pasa el objeto Actividad (asegúrate de que sea Parcelable o Serializable)
                            startActivity(intent);
                        }
                    });


                    ActividadAdapter adapterCreadas = new ActividadAdapter(listaActividadesCreadas,getApplicationContext());
                    recyclerViewActividadesCreadas.setAdapter(adapterCreadas);
                    recyclerViewActividadesCreadas.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    recyclerViewActividadesCreadas.setVisibility(View.VISIBLE);

                    adapterCreadas.setOnItemClickListener(new ActividadAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            Actividad actividadSeleccionada = listaActividadesCreadas.get(position);
                            // Llamar a la nueva Activity, pasando la información de la actividad
                            Intent intent = new Intent(getApplicationContext(), ActivityDetails.class);
                            intent.putExtra("usuario_actual", usuarioAdmin);
                            intent.putExtra("actividad_seleccionada", actividadSeleccionada);  // Pasa el objeto Actividad (asegúrate de que sea Parcelable o Serializable)
                            startActivity(intent);
                        }
                    });

                } else {
                    Toast.makeText(getApplicationContext(), "Error al cargar los miembros.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Actividad>> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getApplicationContext(), "Error al cargar los miembros.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarListasDeEquipos(int idUsuario) {
        ApiService apiService = ApiClient.getApiService(this);

        apiService.getEquiposUsuario(idUsuario).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<Equipo>> call, Response<List<Equipo>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.e("API","Miembros del equipo cargados.");
                    listaEquiposUsuario = response.body();

                    listaEquiposCreados = new ArrayList<>();
                    listaEquiposMiembro = new ArrayList<>();

                    for (Equipo equipo: listaEquiposUsuario) {
                        if (equipo.getCreador() == idUsuario){
                            listaEquiposCreados.add(equipo);
                        }else {
                            listaEquiposMiembro.add(equipo);
                        }
                    }

                    EquipoAdapter adapterCreados = new EquipoAdapter(listaEquiposCreados,getApplicationContext());
                    recyclerViewEquiposLider.setAdapter(adapterCreados);
                    recyclerViewEquiposLider.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    recyclerViewEquiposLider.setVisibility(View.VISIBLE);
                    adapterCreados.setOnItemClickListener(new EquipoAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            Equipo equipoSeleccionado = listaEquiposCreados.get(position);
                            // Llamar a la nueva Activity, pasando la información de la actividad
                            Intent intent = new Intent(getApplicationContext(), EquipoDetails.class);
                            intent.putExtra("usuario", usuarioAdmin);
                            intent.putExtra("Equipo", equipoSeleccionado);  // Pasa el objeto Actividad (asegúrate de que sea Parcelable o Serializable)
                            startActivity(intent);
                        }
                    });

                    EquipoAdapter adapterMiembro = new EquipoAdapter(listaEquiposMiembro,getApplicationContext());
                    recyclerViewEquiposMiembro.setAdapter(adapterMiembro);
                    recyclerViewEquiposMiembro.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    recyclerViewEquiposMiembro.setVisibility(View.VISIBLE);

                    adapterMiembro.setOnItemClickListener(new EquipoAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            Equipo equipoSeleccionado = listaEquiposMiembro.get(position);
                            // Llamar a la nueva Activity, pasando la información de la actividad
                            Intent intent = new Intent(getApplicationContext(), EquipoDetails.class);
                            intent.putExtra("usuario", usuarioAdmin);
                            intent.putExtra("Equipo", equipoSeleccionado);  // Pasa el objeto
                            startActivity(intent);
                        }
                    });

                } else {
                        Log.d("APP_DEBUG", "ERROR AL CARGAR LOS EQUIPOS");
                }
            }

            @Override
            public void onFailure(Call<List<Equipo>> call, Throwable t) {
                Log.d("APP_DEBUG", "ERROR AL CARGAR LOS MIEMBROS", t);
            }
        });
    }

    // Carga los reportes realizados y recibidos por parte del usuario
    private void cargarListaReportesUsuario(int idUsuario) {
        ApiService apiService = ApiClient.getApiService(this);

        apiService.obtenerReportesUsuario(idUsuario).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<Reporte>> call, Response<List<Reporte>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.e("API","Miembros del equipo cargados.");
                    List<Reporte> listaReportesUsuario = response.body();
                    List<Reporte> listaReportesReportante = new ArrayList<>();
                    List<Reporte> listaReportesReportado = new ArrayList<>();

                    for (Reporte reporte: listaReportesUsuario) {
                        int idUsuarioReportado;
                        if (reporte.getUsuarioReportado() == null){
                            idUsuarioReportado = 0;
                        }else{
                             idUsuarioReportado = reporte.getUsuarioReportado().getIdUsuario().intValue();
                        }
                        int idUsuarioReportante = reporte.getUsuarioReportante().getIdUsuario().intValue();

                        if (idUsuarioReportado == idUsuario){
                            listaReportesReportado.add(reporte);
                        } else if (idUsuarioReportante == idUsuario) {
                            listaReportesReportante.add(reporte);
                        }
                    }

                    ReporteAdapter adapterReportado = new ReporteAdapter(listaReportesReportado, getApplicationContext());
                    recyclerViewReportesReportado.setAdapter(adapterReportado);
                    recyclerViewReportesReportado.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    recyclerViewReportesReportado.setVisibility(View.VISIBLE);

                    adapterReportado.setOnItemClickListener(new ReporteAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            Reporte reporteSeleccionado = listaReportesReportado.get(position);
                            // Llamar a la nueva Activity, pasando la información de la actividad
                            Intent intent = new Intent(getApplicationContext(), ReporteDetails.class);
                            intent.putExtra("usuario_admin", usuarioAdmin);
                            intent.putExtra("reporte", reporteSeleccionado);  // Pasa el objeto Actividad (asegúrate de que sea Parcelable o Serializable)
                            startActivity(intent);
                        }
                    });

                    ReporteAdapter adapterReportante = new ReporteAdapter(listaReportesReportante, getApplicationContext());
                    recyclerViewReportesReportante.setAdapter(adapterReportante);
                    recyclerViewReportesReportante.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    recyclerViewReportesReportante.setVisibility(View.VISIBLE);
                    adapterReportante.setOnItemClickListener(new ReporteAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            Reporte reporteSeleccionado = listaReportesReportante.get(position);
                            // Llamar a la nueva Activity, pasando la información de la actividad
                            Intent intent = new Intent(getApplicationContext(), ReporteDetails.class);
                            intent.putExtra("usuario_admin", usuarioAdmin);
                            intent.putExtra("reporte", reporteSeleccionado);  // Pasa el objeto Actividad (asegúrate de que sea Parcelable o Serializable)
                            startActivity(intent);
                        }
                    });

                } else {
                    Toast.makeText(getApplicationContext(), "Error al cargar los miembros.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Reporte>> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getApplicationContext(), "Error al cargar los miembros.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarListaPagosUsuario(int idUsuario) {
        ApiService apiService = ApiClient.getApiService(this);

        apiService.obtenerPagosDeUsuario(idUsuario).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<Pago>> call, Response<List<Pago>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.e("API","Miembros del equipo cargados.");
                    List<Pago> listaPagosUsuario = response.body();

                    PagoAdapter adapter = new PagoAdapter(listaPagosUsuario,getApplicationContext());
                    recyclerViewPagos.setAdapter(adapter);
                    recyclerViewPagos.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    recyclerViewPagos.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(getApplicationContext(), "Error al cargar los miembros.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Pago>> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getApplicationContext(), "Error al cargar los miembros.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}

