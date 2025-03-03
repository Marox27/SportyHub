package com.example.sportyhub.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sportyhub.Modelos.Equipo;
import com.example.sportyhub.Modelos.Pago;
import com.example.sportyhub.R;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PagoAdapter extends RecyclerView.Adapter<PagoAdapter.PagoViewHolder> {

    private List<Pago> listaPagos;
    private Context context;

    private PagoAdapter.OnItemClickListener mListener; // Declaración del mListener

    public PagoAdapter(List<Pago> listaPagos, Context context) {
        this.listaPagos = listaPagos;
        this.context = context;
    }

    @NonNull
    @Override
    public PagoAdapter.PagoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pago, parent, false);
        return new PagoAdapter.PagoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PagoAdapter.PagoViewHolder holder, int position) {
        if (listaPagos == null || listaPagos.isEmpty()) {
            holder.textViewUsernameUsuario.setText("No hay pagos disponibles");
            holder.textViewActividad.setVisibility(View.GONE);
            holder.textViewFechaPago.setVisibility(View.GONE);
            holder.imageViewAvatarUsuario.setVisibility(View.GONE);
            holder.textViewCantidad.setVisibility(View.GONE);
            holder.textViewEstadoPago.setVisibility(View.GONE);
            holder.textViewDescripcion.setVisibility(View.GONE);
        } else {

            Pago pago = listaPagos.get(position);

            // Asignar datos a los elementos de la tarjeta
            holder.textViewUsernameUsuario.setText(pago.getUsuario().getNickname());
            holder.textViewActividad.setText("Actividad: " + pago.getActividad().getTitulo());
            holder.textViewCantidad.setText(pago.getCantidad() + "€");
            holder.textViewFechaPago.setText(String.valueOf(formatFechaPago(pago.getFechaPago())));
            holder.textViewDescripcion.setText("Observaciones: " + pago.getObservaciones());

            if (pago.isReembolsado()) {
                holder.textViewEstadoPago.setText("Reembolsado");
                holder.textViewEstadoPago.setBackgroundColor(ContextCompat.getColor(context, R.color.red));
            } else if (pago.isLiberado()) {
                holder.textViewEstadoPago.setText("Liberado");
                holder.textViewEstadoPago.setBackgroundColor(ContextCompat.getColor(context, R.color.green));
            } else {
                holder.textViewEstadoPago.setText("Pendiente");
                holder.textViewEstadoPago.setBackgroundColor(ContextCompat.getColor(context, R.color.yellow));
            }

            // Cargar la imagen desde la URL usando Glide
            Glide.with(holder.itemView.getContext())
                    .load(pago.getUsuario().getPfp())
                    .placeholder(R.drawable.default_pfp) // Imagen por defecto mientras carga
                    .error(R.drawable.error_placeholder)       // Imagen por defecto si ocurre un error
                    .into(holder.imageViewAvatarUsuario);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    // Método para asignar el listener desde fuera del adaptador
    public void setOnItemClickListener(PagoAdapter.OnItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public int getItemCount() {
        return (listaPagos == null || listaPagos.isEmpty()) ? 1 : listaPagos.size();
    }

    public class PagoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageViewAvatarUsuario;
        TextView textViewUsernameUsuario, textViewActividad, textViewCantidad, textViewFechaPago,
                textViewEstadoPago, textViewDescripcion;

        public PagoViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewAvatarUsuario = itemView.findViewById(R.id.imageViewAvatarUsuario);
            textViewUsernameUsuario = itemView.findViewById(R.id.textViewUsernameUsuario);
            textViewActividad = itemView.findViewById(R.id.textViewActividad);
            textViewCantidad = itemView.findViewById(R.id.textViewCantidad);
            textViewFechaPago = itemView.findViewById(R.id.textViewFechaPago);
            textViewEstadoPago = itemView.findViewById(R.id.textViewEstadoPago);
            textViewDescripcion = itemView.findViewById(R.id.textViewDescripcion);

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

    public void updateData(List<Pago> equipos) {
        this.listaPagos = equipos;
        notifyDataSetChanged();
    }

    public String formatFechaPago(String fechaPago) {
        if (fechaPago == null || fechaPago.isEmpty()) {
            return "Fecha no disponible";
        }

        try {
            // Convertir la cadena a LocalDateTime
            LocalDateTime dateTime = LocalDateTime.parse(fechaPago);

            // Formatear al estilo deseado
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            return dateTime.format(formatter);
        } catch (Exception e) {
            e.printStackTrace();
            return "Formato incorrecto";
        }
    }

}
