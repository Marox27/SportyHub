package com.example.sportyhub.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sportyhub.Modelos.Equipo;
import com.example.sportyhub.R;

import java.util.List;

public class EquipoAdapter extends RecyclerView.Adapter<EquipoAdapter.EquipoViewHolder> {

    private List<Equipo> listaEquipos;
    private Context context;

    private OnItemClickListener mListener; // Declaración del mListener

    public EquipoAdapter(List<Equipo> listaEquipos, Context context) {
        this.listaEquipos = listaEquipos;
        this.context = context;
    }

    @NonNull
    @Override
    public EquipoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.equipo_card, parent, false);
        return new EquipoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EquipoViewHolder holder, int position) {
        if(listaEquipos == null || listaEquipos.isEmpty()){
            holder.textViewEquipoNombre.setText("No tienes equipos disponibles");
            holder.textViewEquipoNombre.setPadding(16,16,16,16);
            holder.textViewMiembros.setVisibility(View.GONE);
            holder.textViewProvincia.setVisibility(View.GONE);
            holder.textViewDeporte.setVisibility(View.GONE);
            holder.imageViewEquipo.setVisibility(View.GONE);
        }else {

            Equipo equipo = listaEquipos.get(position);

            // Asignar datos a los elementos de la tarjeta
            holder.textViewEquipoNombre.setText(equipo.getNombre());
            holder.textViewDeporte.setText("Deporte: " + equipo.getDeporteName());
            holder.textViewProvincia.setText("Provincia: " + equipo.getProvincia());
            holder.textViewMiembros.setText("Miembros: " + equipo.getMiembros());

            // Cargar la imagen desde la URL usando Glide
            Glide.with(holder.itemView.getContext())
                    .load(equipo.getImagen())
                    .placeholder(R.drawable.default_pfp) // Imagen por defecto mientras carga
                    .error(R.drawable.error_placeholder)       // Imagen por defecto si ocurre un error
                    .into(holder.imageViewEquipo);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    // Método para asignar el listener desde fuera del adaptador
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public int getItemCount() {
        return (listaEquipos == null || listaEquipos.isEmpty()) ? 1: listaEquipos.size();
    }
    public class EquipoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {
        ImageView imageViewEquipo;
        TextView textViewEquipoNombre, textViewDeporte, textViewProvincia, textViewMiembros;

        public EquipoViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewEquipo = itemView.findViewById(R.id.imageViewEquipo);
            textViewEquipoNombre = itemView.findViewById(R.id.textViewEquipoNombre);
            textViewDeporte = itemView.findViewById(R.id.textViewDeporte);
            textViewProvincia = itemView.findViewById(R.id.textViewProvincia);
            textViewMiembros = itemView.findViewById(R.id.textViewMiembros);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // Obtener la posición de la tarjeta que se hizo clic
            int position = getBindingAdapterPosition();
            // Verificar si hay un listener y llamarlo
            if (mListener != null && position != RecyclerView.NO_POSITION) {
                mListener.onItemClick(position);
            }
        }
    }

    public void updateData(List<Equipo> equipos) {
        this.listaEquipos = equipos;
        notifyDataSetChanged();
    }

}