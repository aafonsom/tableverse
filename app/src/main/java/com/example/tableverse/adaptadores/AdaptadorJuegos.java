package com.example.tableverse.adaptadores;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tableverse.AdminActividad;
import com.example.tableverse.R;
import com.example.tableverse.UsuarioActividad;
import com.example.tableverse.dialog.DialogModJuego;
import com.example.tableverse.objetos.Juego;

import java.util.ArrayList;
import java.util.List;

public class AdaptadorJuegos extends RecyclerView.Adapter<AdaptadorJuegos.Vh> implements Filterable {

    private List<Juego> lista_juegos;
    private List<Juego> lista_filtrada;
    private Context context;
    private Activity activity;
    private String categoria = "Todas", filtro;
    private int min = 0, max = 0;

    //TODO: MEJORAR LOS FILTROS, QUE SE MANTENGAN SI HAY VARIOS APLICADOS


    public AdaptadorJuegos(List<Juego> lista_juegos, Context context, Activity activity) {
        this.lista_juegos = lista_juegos;
        lista_filtrada = lista_juegos;
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
        Juego juego = lista_filtrada.get(position);

        holder.titulo.setText(juego.getNombre());
        holder.categoria.setText("Categoria: " + juego.getCategoria());
        holder.precio.setText("Precio: " + juego.getPrecio());
        Glide.with(context).load(juego.getUrl_juego())
                .placeholder(android.R.drawable.progress_indeterminate_horizontal)
                .error(android.R.drawable.stat_notify_error).into(holder.foto);

        if(activity instanceof AdminActividad){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((AdminActividad)activity).setPosition(position);
                    DialogModJuego fragment = new DialogModJuego();
                    fragment.show(((AdminActividad)activity).getSupportFragmentManager(), "Modificar Datos");
                }
            });
        }else if(activity instanceof UsuarioActividad){

        }

    }

    @Override
    public int getItemCount() {
        return lista_filtrada.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {

            protected FilterResults performFiltering(CharSequence charSequence) {
                lista_filtrada = new ArrayList<>(lista_juegos);
                List<Juego> lista = new ArrayList<>();
                String filtro = charSequence.toString();

                if(filtro.isEmpty()){
                    if(categoria.equalsIgnoreCase("Todas") && min == 0 && max == 0){
                        lista = lista_juegos;
                    }else{
                        if(!categoria.equalsIgnoreCase("Todas") && (min != 0 || max != 0)){
                            for(Juego juego: lista_juegos){
                                if(juego.getCategoria().equalsIgnoreCase(categoria) &&
                                        juego.getPrecio() >= min && juego.getPrecio() <= max){
                                    lista.add(juego);
                                }
                            }
                        }else if(categoria.equalsIgnoreCase("Todas") && (min != 0 || max != 0)){
                            for(Juego juego: lista_juegos){
                                if(juego.getPrecio() >= min && juego.getPrecio() <= max){
                                    lista.add(juego);
                                }
                            }
                        }else if(!categoria.equalsIgnoreCase("Todas") && min == 0 && max == 0){
                            for(Juego juego: lista_juegos){
                                if(juego.getCategoria().equalsIgnoreCase(categoria)){
                                    lista.add(juego);
                                }
                            }
                        }
                    }


                }else{
                    for(Juego juego: lista_juegos){
                        if(juego.getNombre().toLowerCase().contains(filtro.trim().toLowerCase())
                            && categoria.equalsIgnoreCase("Todas") && max == 0 && min == 0){
                            lista.add(juego);
                        }else if(juego.getNombre().toLowerCase().contains(filtro.trim().toLowerCase())
                                && !categoria.equalsIgnoreCase("Todas") && max == 0 && min == 0){
                            lista.add(juego);
                        }else if(juego.getNombre().toLowerCase().contains(filtro.trim().toLowerCase())
                                && !categoria.equalsIgnoreCase("Todas") && (max != 0 || min != 0)
                                && juego.getPrecio() >= min && juego.getPrecio() <= max){
                            lista.add(juego);
                        }else if(juego.getNombre().toLowerCase().contains(filtro.trim().toLowerCase())
                                && categoria.equalsIgnoreCase("Todas") && (max != 0 || min != 0)
                                && juego.getPrecio() >= min && juego.getPrecio() <= max){
                            lista.add(juego);
                        }
                    }
                }
                FilterResults results = new FilterResults();
                results.values = lista;

                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                lista_filtrada.clear();
                lista_filtrada.addAll((List<Juego>) filterResults.values);
                notifyDataSetChanged();
            }

        };

    }

    public void filtrarCategoria(String filtro){
        categoria = filtro;
    }

    public void filtrarPorPrecio(int min, int max){
        this.min = min;
        this.max = max;
    }

    public class Vh extends RecyclerView.ViewHolder {
        public ImageView foto;
        public TextView titulo, categoria, precio;
        public Vh(@NonNull View itemView) {
            super(itemView);

            foto = itemView.findViewById(R.id.template_juego_imagen);
            titulo = itemView.findViewById(R.id.template_juego_titulo);
            categoria = itemView.findViewById(R.id.template_categoria);
            precio = itemView.findViewById(R.id.template_precio);
        }
    }
}
