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
import com.example.tableverse.dialog.DialogComprarJuego;
import com.example.tableverse.dialog.DialogModJuego;
import com.example.tableverse.objetos.Juego;
import com.example.tableverse.objetos.Usuario;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AdaptadorJuegos extends RecyclerView.Adapter<AdaptadorJuegos.Vh> implements Filterable {

    private List<Juego> lista_juegos;
    private List<Juego> lista_filtrada;
    private Context context;
    private Activity activity;
    private int min = 0, max = 0;
    private String categoria = "Todas";

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
        Glide.with(context).load(juego.getUrl_juego())
                .placeholder(android.R.drawable.progress_indeterminate_horizontal)
                .error(android.R.drawable.stat_notify_error).into(holder.foto);

        if(activity instanceof AdminActividad){
            holder.precio.setText("Precio: " + juego.getPrecio());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((AdminActividad)activity).setPosition(position);
                    DialogModJuego fragment = new DialogModJuego();
                    fragment.show(((AdminActividad)activity).getSupportFragmentManager(), "Modificar Datos");
                }
            });
        }else if(activity instanceof UsuarioActividad){
            UsuarioActividad usuarioActividad = ((UsuarioActividad)activity);
            if(usuarioActividad.getPos_ratio_elegido() == -1){
                holder.precio.setText("Precio: " + juego.getPrecio()
                        + usuarioActividad.getDivisas()[usuarioActividad.getPos_ratio_elegido()+1]);
            }else{
                DecimalFormat df = new DecimalFormat("#.##");
                holder.precio.setText("Precio: " + df.format(juego.getPrecio() *
                        usuarioActividad.getRatios()[usuarioActividad.getPos_ratio_elegido()])
                        + usuarioActividad.getDivisas()[usuarioActividad.getPos_ratio_elegido()+1]);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((UsuarioActividad)activity).setPosition(position);
                    DialogComprarJuego dialogComprarJuego = new DialogComprarJuego();
                    dialogComprarJuego.show(((UsuarioActividad)activity).getSupportFragmentManager(), "Información del juego");
                }
            });
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
                lista = filtrarCategoria();
                lista = filtrarPorPrecio(lista);
                if(!filtro.isEmpty()){
                    Iterator<Juego> iterator = lista.iterator();
                    while (iterator.hasNext()){
                        Juego juego = iterator.next();
                        if(!juego.getNombre().toLowerCase().contains(filtro.trim().toLowerCase())){
                            iterator.remove();
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

    public List<Juego> filtrarCategoria(){
        List<Juego> lista = new ArrayList<>();

        if(categoria.equals("Todas")){
            lista = new ArrayList<>(lista_juegos);
        }else{
            for(Juego juego: lista_juegos){
                if(juego.getCategoria().equalsIgnoreCase(categoria)){
                    lista.add(juego);
                }
            }
        }

        return lista;
    }

    public List<Juego> filtrarPorPrecio(List<Juego> prefiltrada){
        List<Juego> lista = new ArrayList<>();
        UsuarioActividad usuarioActividad = ((UsuarioActividad)activity);

        if(max == 0){
            lista = prefiltrada;
        }else{
            for(Juego juego: prefiltrada){
                double precio = juego.getPrecio() * usuarioActividad.getRatios()[usuarioActividad.getPos_ratio_elegido()];
                if(precio >= min && precio <= max){
                    lista.add(juego);
                }
            }
        }

        return lista;
    }


    public void setMax(int max){
        this.max = max;
    }

    public void setMin(int min){
        this.min = min;
    }

    public void setCategoria(String categoria){
        this.categoria = categoria;
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
