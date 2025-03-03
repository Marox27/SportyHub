package com.example.sportyhub.Adapters;


import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sportyhub.Modelos.Participante;

import java.util.List;

import com.example.sportyhub.Modelos.Usuario;
import com.example.sportyhub.R;
import com.example.sportyhub.Reporte.CrearReporte;


public class ParticipantesAdapter extends RecyclerView.Adapter<ParticipantesAdapter.ParticipanteViewHolder> {

    private List<Participante> listaParticipantes;
    private Usuario creador;

    public ParticipantesAdapter(List<Participante> listaParticipantes) {
        this.listaParticipantes = listaParticipantes;
    }

    public ParticipantesAdapter(List<Participante> listaParticipantes, Usuario creador) {
        this.listaParticipantes = listaParticipantes;
        this.creador = creador;
    }

    @NonNull
    @Override
    public ParticipanteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_participante, parent, false);
        return new ParticipanteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParticipanteViewHolder holder, int position) {
        Participante participante = listaParticipantes.get(position);
        Log.e("ParticipanteADAPTER", participante.getNickname() + participante.getPfp() + participante.getId());

        holder.textViewNombre.setText(participante.getUsuario().getIdUsuario() == creador.getIdUsuario().intValue() ?
                participante.getUsuario().getNickname() + " ⭐(CREADOR)" : participante.getUsuario().getNickname());

        // Cargar la imagen desde la URL usando Glide
        Glide.with(holder.itemView.getContext())
                .load(participante.getUsuario().getPfp())
                .placeholder(R.drawable.default_pfp) // Imagen por defecto mientras carga
                .error(R.drawable.error_placeholder)       // Imagen por defecto si ocurre un error
                .into(holder.imageViewAvatar);

        // Agregar un evento de clic al ítem
        holder.itemView.setOnClickListener(v -> {
            // Obtener el participante en la posición actual
            Participante participanteSeleccionado = listaParticipantes.get(holder.getBindingAdapterPosition());

            // Crear y mostrar el PopupMenu
            PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
            popupMenu.inflate(R.menu.participante_menu); // Aquí debes crear el archivo XML del menú
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    int idAccion = item.getItemId();

                    if (idAccion == R.id.action_reportar_usuario){
                        // Acciones para la opción 1
                        Intent intent = new Intent(v.getContext(), CrearReporte.class);
                        intent.putExtra("usuario", creador);
                        intent.putExtra("usuario_reportado", participanteSeleccionado.getUsuario());

                        v.getContext().startActivity(intent);

                        return true;
                    }else {
                        return false;
                    }
                }
            });
            popupMenu.show(); // Mostrar el menú
        });
    }

    @Override
    public int getItemCount() {
        return listaParticipantes.size();
    }

    public static class ParticipanteViewHolder extends RecyclerView.ViewHolder {
        TextView textViewNombre;
        ImageView imageViewAvatar;

        public ParticipanteViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNombre = itemView.findViewById(R.id.textViewNombreParticipante);
            imageViewAvatar = itemView.findViewById(R.id.imageViewAvatarParticipante);
        }
    }
}
