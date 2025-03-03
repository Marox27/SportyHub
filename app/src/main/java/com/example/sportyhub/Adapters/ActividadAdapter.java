package com.example.sportyhub.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportyhub.Modelos.Actividad;
import com.example.sportyhub.Modelos.Equipo;
import com.example.sportyhub.Modelos.Etiqueta;
import com.example.sportyhub.Modelos.Usuario;
import com.example.sportyhub.R;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

public class ActividadAdapter extends RecyclerView.Adapter<ActividadAdapter.ActividadViewHolder> {

    private List<Actividad> listaActividades;
    private Context context;

    private OnItemClickListener mListener; // Declaración del mListener

    public ActividadAdapter(List<Actividad> listaActividades, Context context) {
        this.listaActividades = listaActividades;
        this.context = context;
    }

    @NonNull
    @Override
    public ActividadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_card, parent, false);
        return new ActividadViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActividadViewHolder holder, int position) {
        if (listaActividades == null || listaActividades.isEmpty()) {
            // Mostrar mensaje para lista vacía
            holder.textViewTitulo.setText("No hay actividades disponibles");
            holder.textViewTitulo.setPadding(16,16,16,16);
            holder.imageViewActividad.setVisibility(View.GONE);
            holder.textViewDeporteActividad.setVisibility(View.GONE);
            holder.textViewFecha.setVisibility(View.GONE);
            holder.textViewHora.setVisibility(View.GONE);
            holder.textViewEtiqueta1.setVisibility(View.GONE);
            holder.textViewEtiqueta2.setVisibility(View.GONE);
            holder.textViewEtiqueta3.setVisibility(View.GONE);
            holder.textViewEtiqueta4.setVisibility(View.GONE);
            holder.textViewPrecio.setVisibility(View.GONE);
            holder.textViewCoste.setVisibility(View.GONE);

        } else {
            // Configurar el diseño de la tarjeta normalmente
            Actividad actividad = listaActividades.get(position);

            holder.textViewTitulo.setText("Titulo:" + actividad.getTitulo());

            String fecha = actividad.getFecha();
            // Convertir la cadena a LocalDateTime
            LocalDate date = LocalDate.parse(fecha);

            // Formatear al estilo deseado
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            fecha = date.format(formatter);
            String hora = actividad.getHora();
            double precio = actividad.getPrecio();

            holder.textViewFecha.setText("Inicio: " + fecha);
            holder.textViewHora.setText("Hora: " + hora);
            holder.textViewDeporteActividad.setText(actividad.getDeporteName());

            if (precio == 0){
                holder.textViewPrecio.setText("GRATIS");
                holder.textViewPrecio.setBackgroundColor(ContextCompat.getColor(context, R.color.green));
            }else {
                double precioPorParticipante = precio/actividad.getParticipantesNecesarios();
                holder.textViewPrecio.setText(String.format("%.2f", precioPorParticipante) + "€");
                holder.textViewPrecio.setBackgroundColor(ContextCompat.getColor(context, R.color.mainOrange));
            }

            Set<Etiqueta> etiquetas = actividad.getEtiquetas();

            if (etiquetas != null || !etiquetas.isEmpty()){
                establecerEtiquetas(etiquetas, holder);
            }else{
                holder.textViewPrecio.setGravity(Gravity.START);
            }

            holder.imageViewActividad.setScaleType(ImageView.ScaleType.FIT_CENTER);
            // Asignar la imagen según el deporte
            switch (actividad.getDeporte()) {
                case 1:
                    holder.imageViewActividad.setImageResource(R.drawable.fut7);
                    break;
                case 2:
                    holder.imageViewActividad.setImageResource(R.drawable.futbol);
                    break;
                case 3:
                    holder.imageViewActividad.setImageResource(R.drawable.futsal);
                    break;
                case 4:
                    holder.imageViewActividad.setImageResource(R.drawable.tenis);
                    break;
                case 5:
                    holder.imageViewActividad.setImageResource(R.drawable.padel);
                    break;
                case 6:
                    holder.imageViewActividad.setImageResource(R.drawable.baloncesto);
                    break;
                case 7:
                    holder.imageViewActividad.setImageResource(R.drawable.beisbol);
                    break;
                default:
                    holder.imageViewActividad.setImageResource(R.drawable.activities);
                    break;
            }
        }
    }

    private void establecerEtiquetas(Set<Etiqueta> etiquetas, ActividadViewHolder holder){
        // Convertir el Set en un array correctamente
        Etiqueta[] arrayEtiquetas = etiquetas.toArray(new Etiqueta[0]);

        // Hacemos no visibles todos los TextViews por defecto
        holder.textViewEtiqueta1.setVisibility(View.GONE);
        holder.textViewEtiqueta2.setVisibility(View.GONE);
        holder.textViewEtiqueta3.setVisibility(View.GONE);
        holder.textViewEtiqueta4.setVisibility(View.GONE);

        // Asignar valores según la cantidad de etiquetas
        if (arrayEtiquetas.length > 0) {
            holder.textViewEtiqueta1.setText(arrayEtiquetas[0].getNombre());
            holder.textViewEtiqueta1.setVisibility(View.VISIBLE);
        }
        if (arrayEtiquetas.length > 1) {
            holder.textViewEtiqueta2.setText(arrayEtiquetas[1].getNombre());
            holder.textViewEtiqueta2.setVisibility(View.VISIBLE);
        }
        if (arrayEtiquetas.length > 2) {
            holder.textViewEtiqueta3.setText(arrayEtiquetas[2].getNombre());
            holder.textViewEtiqueta3.setVisibility(View.VISIBLE);
        }
        if (arrayEtiquetas.length > 3) {
            holder.textViewEtiqueta4.setText(arrayEtiquetas[3].getNombre());
            holder.textViewEtiqueta4.setVisibility(View.VISIBLE);
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
        return (listaActividades == null || listaActividades.isEmpty()) ? 1 : listaActividades.size();
    }



    public class ActividadViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {
        ImageView imageViewActividad;
        TextView textViewTitulo, textViewDeporteActividad, textViewFecha, textViewHora,
                textViewEtiqueta1, textViewEtiqueta2, textViewEtiqueta3, textViewEtiqueta4,
                textViewPrecio, textViewCoste;

        public ActividadViewHolder (@NonNull View itemView) {
            super(itemView);
            imageViewActividad = itemView.findViewById(R.id.imageViewActividad);
            textViewTitulo = itemView.findViewById(R.id.textViewTituloActividad);
            textViewDeporteActividad = itemView.findViewById(R.id.textViewDeporteActividad);
            textViewFecha = itemView.findViewById(R.id.textViewFechaActividad);
            textViewHora = itemView.findViewById(R.id.textViewHoraActividad);
            textViewEtiqueta1 = itemView.findViewById(R.id.textViewEtiqueta1);
            textViewEtiqueta2 = itemView.findViewById(R.id.textViewEtiqueta2);
            textViewEtiqueta3 = itemView.findViewById(R.id.textViewEtiqueta3);
            textViewEtiqueta4 = itemView.findViewById(R.id.textViewEtiqueta4);
            textViewPrecio = itemView.findViewById(R.id.textViewPrecio);
            textViewCoste = itemView.findViewById(R.id.textViewCoste);

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

    public void updateActividades(List<Actividad> nuevasActividades) {
        this.listaActividades.clear();
        this.listaActividades.addAll(nuevasActividades);
        notifyDataSetChanged();
    }

    public void updateData(List<Actividad> actividades) {
        this.listaActividades = actividades;
        notifyDataSetChanged();
    }
}