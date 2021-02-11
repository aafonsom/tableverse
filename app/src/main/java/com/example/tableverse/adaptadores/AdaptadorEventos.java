package com.example.tableverse.adaptadores;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tableverse.AdminActividad;
import com.example.tableverse.R;
import com.example.tableverse.UsuarioActividad;
import com.example.tableverse.dialog.DialogModEvento;
import com.example.tableverse.dialog.DialogModJuego;
import com.example.tableverse.objetos.Evento;
import com.example.tableverse.objetos.Juego;
import com.example.tableverse.objetos.Usuario;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class AdaptadorEventos extends RecyclerView.Adapter<AdaptadorEventos.Vh> {

    List<Evento> lista_eventos;
    List<Evento> lista_filtrada;
    Context context;
    Activity activity;
    public AdaptadorEventos(List<Evento> lista_eventos, Context context, Activity activity) {
        this.lista_eventos = lista_eventos;
        this.lista_filtrada = lista_eventos;
        this.context = context;
        this.activity = activity;
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
        Evento evento = lista_filtrada.get(position);

        holder.nombre.setText(evento.getNombre());
        holder.aforo.setText("Fecha: " + evento.getFecha());
        holder.precio.setText("Precio de la entrada:" + evento.getPrecio());
        Glide.with(context).load(evento.getUrlImagen())
                .placeholder(android.R.drawable.progress_indeterminate_horizontal)
                .error(R.drawable.person_morada).into(holder.foto);

        if(activity instanceof AdminActividad){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((AdminActividad)activity).setPosition(position);
                    DialogModEvento fragment = new DialogModEvento();
                    fragment.show(((AdminActividad)activity).getSupportFragmentManager(), "Modificar Datos");
                }
            });
        }else if(activity instanceof UsuarioActividad){
            UsuarioActividad usuarioActividad = ((UsuarioActividad)activity);
            if(usuarioActividad.getPos_ratio_elegido() == -1){
                holder.precio.setText("Precio de la entrada: " + evento.getPrecio()
                        + usuarioActividad.getDivisas()[usuarioActividad.getPos_ratio_elegido()+1]);
            }else{
                DecimalFormat df = new DecimalFormat("#.##");
                holder.precio.setText("Precio de la entrada: " + df.format(evento.getPrecio() *
                        usuarioActividad.getRatios()[usuarioActividad.getPos_ratio_elegido()])
                        + usuarioActividad.getDivisas()[usuarioActividad.getPos_ratio_elegido()+1]);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((UsuarioActividad)activity).setPosition(position);
                    NavController navController = ((UsuarioActividad)activity).getNavController();
                    navController.navigate(R.id.verEvento);
                }
            });

        }

    }

    @Override
    public int getItemCount() {
        return lista_filtrada.size();
    }

    public void filtroGratuito(boolean gratuito){
        lista_filtrada = new ArrayList<>(lista_eventos);
        List<Evento> lista = new ArrayList<>();

        if(!gratuito){
            lista = lista_eventos;
        }else{
            for(Evento evento: lista_eventos){
                if(evento.getPrecio() == 0){
                    lista.add(evento);
                }
            }
        }
        lista_filtrada.clear();
        lista_filtrada.addAll(lista);
        notifyDataSetChanged();
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
