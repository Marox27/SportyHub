package com.example.sportyhub.Adapters;

import static android.view.View.TEXT_ALIGNMENT_CENTER;

import android.content.Context;
import android.media.tv.TvInputManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportyhub.Modelos.Notificacion;
import com.example.sportyhub.Modelos.Usuario;
import com.example.sportyhub.R;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
    private List<Notificacion> notificaciones;
    private Context context;
    private OnItemClickListener listener;

    public NotificationAdapter(List<Notificacion> notificaciones, Context context) {
        this.notificaciones = notificaciones;
        this.context = context;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notificacion, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        TextView tvTitulo = holder.titulo;
        TextView tvMensaje = holder.mensaje;
        TextView tvFecha = holder.fecha;
        TextView tvAutor = holder.autor;

        if (notificaciones == null || notificaciones.isEmpty()){
            tvTitulo.setText("No hay notificaciones pendientes :)");
            tvTitulo.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tvMensaje.setVisibility(View.GONE);
            tvFecha.setVisibility(View.GONE);
            tvAutor.setVisibility(View.GONE);
        }else{
            Notificacion notificacion = notificaciones.get(position);
            holder.titulo.setText(notificacion.getTitulo());
            holder.mensaje.setText(notificacion.getMensaje());
            // Convertir la cadena a LocalDateTime
            LocalDateTime dateTime = LocalDateTime.parse(notificacion.getFechaCreacion());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String fecha_formateada = dateTime.format(formatter);
            holder.fecha.setText(fecha_formateada);

            Usuario emisor = notificacion.getEmisor();
            if (emisor.isAdmin()) {
                tvAutor.setText("De: SportyHub");
            }else{
                tvAutor.setText("De: " + notificacion.getEmisor().getNickname());
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(NotificationAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return (notificaciones == null || notificaciones.isEmpty()) ? 1 : notificaciones.size();
    }

    public class NotificationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView titulo, mensaje, fecha, autor;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.tvTitulo);
            mensaje = itemView.findViewById(R.id.tvMensaje);
            fecha = itemView.findViewById(R.id.tvFecha);
            autor = itemView.findViewById(R.id.tvEmisor);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // Obtener la posición de la tarjeta que se hizo clic
            int position = getBindingAdapterPosition();
            // Verificar si hay un listener y llamarlo
            if (listener != null && position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position);
            }
        }

    }
}

