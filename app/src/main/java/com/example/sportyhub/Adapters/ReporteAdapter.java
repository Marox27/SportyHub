package com.example.sportyhub.Adapters;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sportyhub.Modelos.Reporte;
import com.example.sportyhub.R;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReporteAdapter extends RecyclerView.Adapter<ReporteAdapter.ReporteViewHolder> {
    private List<Reporte> reportes;
    private Context context;
    private OnItemClickListener mListener; // Declaración del mListener

    public ReporteAdapter(List<Reporte> reportes, Context context) {
        this.reportes = reportes;
        this.context = context;
    }

    @Override
    public ReporteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_reporte, parent, false);
        return new ReporteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReporteViewHolder holder, int position) {
        if (reportes == null || reportes.isEmpty()) {
            // Mostrar mensaje para lista vacía
            holder.textViewUsernameReportante.setText("No existen reportes");
            holder.textViewUsernameReportante.setTextSize(20);
            holder.textViewUsernameReportante.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            holder.textViewTipoReporte.setVisibility(View.GONE);
            holder.textViewDescripcionReporte.setVisibility(View.GONE);
            holder.textViewFechaCreacionReporte.setVisibility(View.GONE);
            holder.imageViewAvatarUsuarioReportante.setVisibility(View.GONE);
            holder.textViewEstadoReporte.setVisibility(View.GONE);
        } else {
            Reporte reporte = reportes.get(position);

            // Establecer el avatar del reportante
            Glide.with(holder.itemView.getContext())
                    .load(reporte.getUsuarioReportante().getPfp())
                    .placeholder(R.drawable.default_pfp) // Imagen por defecto mientras carga
                    .error(R.drawable.error_placeholder)       // Imagen por defecto si ocurre un error
                    .into(holder.imageViewAvatarUsuarioReportante);
            holder.imageViewAvatarUsuarioReportante.setScaleType(ImageView.ScaleType.FIT_CENTER); // Esto sería dinámico si tienes imagenes de perfil

            // Nombre del reportante
            holder.textViewUsernameReportante.setText("Reportante: " + reporte.getUsuarioReportante().getNickname());

            // Descripción del reporte
            holder.textViewDescripcionReporte.setText(reporte.getDescripcion());

            // Tipo de reporte (Usuario, Actividad, Equipo)
            if (reporte.getUsuarioReportado() != null) {
                holder.textViewTipoReporte.setText("Usuario Reportado: " + reporte.getUsuarioReportado().getNickname());
                holder.textViewTipoReporte.setTextColor(ContextCompat.getColor(context, R.color.red));
            } else if (reporte.getActividadReportada() != null) {
                holder.textViewTipoReporte.setText("Actividad Reportada: " + reporte.getActividadReportada().getTitulo());
                holder.textViewTipoReporte.setTextColor(ContextCompat.getColor(context, R.color.green));
            } else if (reporte.getEquipoReportado() != null) {
                holder.textViewTipoReporte.setText("Equipo Reportado: " + reporte.getEquipoReportado().getNombre());
                holder.textViewTipoReporte.setTextColor(ContextCompat.getColor(context, R.color.yellow));
            }

            // Estado del reporte (revisado o no)
            if (reporte.isRevisado()) {
                holder.textViewEstadoReporte.setText("Revisado");
                holder.textViewEstadoReporte.setBackgroundColor(ContextCompat.getColor(context, R.color.green));
            } else {
                holder.textViewEstadoReporte.setText("Pendiente");
                holder.textViewEstadoReporte.setBackgroundColor(ContextCompat.getColor(context, R.color.red));
            }

            LocalDateTime fechaHora = LocalDateTime.parse(reporte.getFechaCreacion());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String fechaReporte = fechaHora.format(formatter);


            // Fecha de creación del reporte
            holder.textViewFechaCreacionReporte.setText(fechaReporte);
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
        return (reportes == null || reportes.isEmpty()) ? 1 : reportes.size();
    }


    public class ReporteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageViewAvatarUsuarioReportante;
        TextView textViewUsernameReportante, textViewDescripcionReporte, textViewTipoReporte;
        TextView textViewEstadoReporte, textViewFechaCreacionReporte;

        public ReporteViewHolder(View itemView) {
            super(itemView);
            imageViewAvatarUsuarioReportante = itemView.findViewById(R.id.imageViewAvatarUsuarioReportante);
            textViewUsernameReportante = itemView.findViewById(R.id.textViewUsernameReportante);
            textViewDescripcionReporte = itemView.findViewById(R.id.textViewDescripcionReporte);
            textViewTipoReporte = itemView.findViewById(R.id.textViewTipoReporte);
            textViewEstadoReporte = itemView.findViewById(R.id.textViewEstadoReporte);
            textViewFechaCreacionReporte = itemView.findViewById(R.id.textViewFechaCreacionReporte);

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
}
