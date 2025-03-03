package com.example.sportyhub.Adapters;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.sportyhub.Fragments.ActivityListFragment;
import com.example.sportyhub.Fragments.ActivityUserListFragment;
import com.example.sportyhub.Fragments.MainFragment;
import com.example.sportyhub.Fragments.TeamFragment;
import com.example.sportyhub.Modelos.Actividad;
import com.example.sportyhub.Modelos.Equipo;
import com.example.sportyhub.Modelos.Usuario;
import com.example.sportyhub.R;

import java.util.List;

public class MainAdapter extends FragmentStateAdapter {

    private static final int NUM_TABS = 4; // Número de pestañas
    private static final int[] TAB_ICONS = {R.drawable.activities,
            R.drawable.ic_home, R.drawable.teams, R.drawable.my_teams, R.drawable.ic_settings}; // Recursos de imágenes para pestañas
    private static final String[] TAB_TITLES = {
            "Actividades", "Mis Acts", "Equipos", "Mis Equipos"
    };
    private List<Actividad>actividades, actividades_usuario;
    private List<Equipo>equipos, equipos_usuario;
    private Usuario usuario;
    private double latitud, longitud, distancia;

    private ActivityListFragment activityListFragment;
    private ActivityUserListFragment activityUserListFragment;
    private TeamFragment teamFragment, userTeamsFragment;

    public MainAdapter(FragmentManager fragmentManager, Lifecycle lifecycle,
                       List<Actividad> actividades, List<Equipo>equipos, List<Actividad> actividades_usuario, Usuario usuario) {
        super(fragmentManager, lifecycle);
        this.actividades = actividades;
        this.equipos = equipos;
        this.actividades_usuario = actividades_usuario;
        this.usuario = usuario;
        this.latitud = 0;
        this.longitud = 0;
        this.distancia = 0;
    }

    public MainAdapter(FragmentManager fragmentManager, Lifecycle lifecycle,
                       List<Actividad> actividades, List<Equipo>equipos, List<Actividad> actividades_usuario,
                       List<Equipo> equipos_usuario, Usuario usuario, double latitud, double longitud) {
        super(fragmentManager, lifecycle);
        this.actividades = actividades;
        this.equipos = equipos;
        this.actividades_usuario = actividades_usuario;
        this.equipos_usuario = equipos_usuario;
        this.usuario = usuario;
        this.latitud = latitud;
        this.longitud = longitud;
        this.distancia = 50;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Dependiendo de la posición, crea y devuelve el fragmento correspondiente
        switch (position) {
            case 0: // Fragmento para la pestaña de actividades.
                if (latitud == 0 || distancia == 0){
                    if (actividades == null){
                        Log.e("API", "La lista de actividades en create fragment está vacía");
                    }
                    activityListFragment = new ActivityListFragment(actividades, usuario);
                    Log.d("DEBUG_FRG_ACTLIST_USER", usuario.toString());
                    return activityListFragment; // Fragmento para la pestaña 1 (Sin ubicación)
                }else {
                    activityListFragment = new ActivityListFragment(actividades, usuario, latitud, longitud, distancia);
                    return activityListFragment;
                }

            case 1: // Fragmento para la pestaña de las actividades del usuario.
                activityUserListFragment = new ActivityUserListFragment(actividades_usuario, usuario); // Fragmento para la pestaña 3 de actividades del usuario
                return activityUserListFragment; // Fragmento para la pestaña 2

            case 2: // Fragmento para la pestaña de equipo.
                teamFragment = new TeamFragment(equipos, usuario);
                return teamFragment;

            case 3: // Fragmento para la pestaña de equipos del usuario.
                userTeamsFragment = new TeamFragment(equipos_usuario, usuario);
                return userTeamsFragment;

            default:
                return null;
        }
    }

    // Devuelve la imagen correspodiente a la pestaña.
    public static int getTabIcon(int position) {
        return TAB_ICONS[position];
    }

    public static String getTabTitles(int position){
        return TAB_TITLES[position];
    }

    // Devuelve el número de pestañas
    @Override
    public int getItemCount() {
        return NUM_TABS;
    }

    public void updateData(List<Actividad> actividades, List<Equipo> equipos, List<Actividad> actividadesUsuario,
                           List<Equipo>equiposUsuario, Usuario usuario, double latitud, double longitud) {
        this.actividades = actividades;
        this.equipos = equipos;
        this.actividades_usuario = actividadesUsuario;
        this.usuario = usuario;
        this.latitud = latitud;
        this.longitud = longitud;
        Log.e("DEBUG", "Actividades cargadas: " + actividades.size());

        // Notificar a los fragmentos si están creados
        if (activityListFragment != null) {
            activityListFragment.updateData(actividades);
        }
        if (activityUserListFragment != null) {
            activityUserListFragment.updateData(actividadesUsuario);
        }
        if (teamFragment != null) {
            teamFragment.updateData(equipos);
        }
        if (userTeamsFragment != null){
            userTeamsFragment.updateData(equiposUsuario);
        }
        notifyDataSetChanged();
    }

}