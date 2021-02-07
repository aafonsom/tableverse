package com.example.tableverse.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tableverse.AdminActividad;
import com.example.tableverse.R;
import com.example.tableverse.dialog.DialogModEvento;
import com.example.tableverse.dialog.DialogModJuego;
import com.example.tableverse.objetos.Evento;

import java.util.List;

public class AdaptadorEventos extends RecyclerView.Adapter<AdaptadorEventos.Vh> {

    List<Evento> lista_eventos;
    Context context;
    AdminActividad adminActividad;
    public AdaptadorEventos(List<Evento> lista_eventos, Context context, AdminActividad adminActividad) {
        this.lista_eventos = lista_eventos;
        this.context = context;
        this.adminActividad = adminActividad;
    }

    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.template_juegos, parent, false);

        return new Vh(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Vh holder, final int position) {
        Evento evento = lista_eventos.get(position);

        holder.nombre.setText(evento.getNombre());
        holder.aforo.setText("Fecha: " + evento.getFecha());
        holder.precio.setText("Precio de la entrada:" + evento.getPrecio());
        Glide.with(context).load(evento.getUrlImagen())
                .placeholder(android.R.drawable.progress_indeterminate_horizontal)
                .error(android.R.drawable.stat_notify_error).into(holder.foto);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adminActividad.setPosition(position);
                DialogModEvento fragment = new DialogModEvento();
                fragment.show(adminActividad.getSupportFragmentManager(), "Modificar Datos");
            }
        });
    }

    @Override
    public int getItemCount() {
        return lista_eventos.size();
    }

    public class Vh extends RecyclerView.ViewHolder {
        private ImageView foto;
        private TextView nombre, precio, aforo;

        public Vh(@NonNull View itemView) {
            super(itemView);

            foto = itemView.findViewById(R.id.template_juego_imagen);
            nombre = itemView.findViewById(R.id.template_juego_titulo);
            precio = itemView.findViewById(R.id.template_precio);
            aforo = itemView.findViewById(R.id.template_categoria);
        }
    }
}
