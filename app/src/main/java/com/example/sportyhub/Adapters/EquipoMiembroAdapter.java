package com.example.sportyhub.Adapters;



import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sportyhub.Modelos.EquipoMiembro;

import java.util.List;

import com.example.sportyhub.R;


public class EquipoMiembroAdapter extends RecyclerView.Adapter<EquipoMiembroAdapter.ParticipanteViewHolder> {

    private final List<EquipoMiembro> listaMiembros;

    public EquipoMiembroAdapter(List<EquipoMiembro> listaParticipantes) {
        this.listaMiembros = listaParticipantes;
    }

    @NonNull
    @Override
    public ParticipanteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_participante, parent, false);
        return new ParticipanteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParticipanteViewHolder holder, int position) {
        EquipoMiembro miembro = listaMiembros.get(position);
        holder.textViewNombre.setText(miembro.getUsuario().getNickname());

        // Cargar la imagen desde la URL usando Glide
        Glide.with(holder.itemView.getContext())
                .load(miembro.getUsuario().getPfp())
                .placeholder(R.drawable.default_pfp) // Imagen por defecto mientras carga
                .error(R.drawable.error_placeholder)       // Imagen por defecto si ocurre un error
                .into(holder.imageViewAvatar);
    }

    @Override
    public int getItemCount() {
        int count = (listaMiembros == null) ? 0 : listaMiembros.size();
        Log.e("RecyclerView", "Elementos en lista: " + count);
        return count;
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


