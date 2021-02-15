package com.example.tableverse.usuario;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.tableverse.AdminActividad;
import com.example.tableverse.R;
import com.example.tableverse.UsuarioActividad;
import com.example.tableverse.adaptadores.AdaptadorJuegos;
import com.example.tableverse.objetos.Juego;
import com.example.tableverse.objetos.Usuario;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ListaJuegosUsuario extends Fragment {

    private List<Juego> lista_juegos;
    private AdaptadorJuegos adaptadorJuegos;
    private UsuarioActividad usuarioActividad;
    private DatabaseReference ref;
    private StorageReference sto;
    private SeekBar sb_min, sb_max;
    private TextView tv_min, tv_max;
    private RecyclerView rv_juegos;
    private StaggeredGridLayoutManager glm;
    private Spinner spi_categoria;
    private List<String> categorias;
    private ArrayAdapter<String> categoriaAdapter;
    private int max = 0, min = 0;


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public ListaJuegosUsuario() {
        // Required empty public constructor
    }

    public static ListaJuegosUsuario newInstance(String param1, String param2) {
        ListaJuegosUsuario fragment = new ListaJuegosUsuario();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_lista_juegos_usuario, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sb_min = view.findViewById(R.id.sb_min);
        sb_max = view.findViewById(R.id.sb_max);
        spi_categoria = view.findViewById(R.id.spi_categorias);
        tv_max = view.findViewById(R.id.tv_precio_max);
        tv_min = view.findViewById(R.id.tv_precio_min);
        rv_juegos = view.findViewById(R.id.rv_juegos);

        usuarioActividad = (UsuarioActividad) getActivity();
        ref = usuarioActividad.getRef();
        sto = usuarioActividad.getSto();
        adaptadorJuegos = usuarioActividad.getAdaptadorJuegos();
        lista_juegos = usuarioActividad.getLista_juegos();
        categorias = new ArrayList<>();

        cargarJuegos();
        glm = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        usuarioActividad.adaptadorJuegos = new AdaptadorJuegos(lista_juegos, getContext(), usuarioActividad);
        adaptadorJuegos = usuarioActividad.adaptadorJuegos;
        rv_juegos.setAdapter(adaptadorJuegos);
        rv_juegos.setLayoutManager(glm);
        categoriaAdapter = new ArrayAdapter<>(usuarioActividad.getApplicationContext(), android.R.layout.simple_spinner_item, categorias);
        categoriaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spi_categoria.setAdapter(categoriaAdapter);

        sb_min.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                min = i;
                tv_min.setText("Precio mínimo: " + Integer.toString(i));
                if(i > max ){
                    sb_max.setProgress(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                adaptadorJuegos.setMax(max);
                adaptadorJuegos.setMin(min);
                if(usuarioActividad.isQueryTextSi()){
                    adaptadorJuegos.getFilter().filter(usuarioActividad.getQueryText());
                }else{
                    adaptadorJuegos.getFilter().filter(usuarioActividad.getNewText());
                }
            }
        });

        sb_max.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                max = i;
                tv_max.setText("Precio máximo: " + Integer.toString(i));
                if(i < min){
                    sb_min.setProgress(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                adaptadorJuegos.setMax(max);
                adaptadorJuegos.setMin(min);
                adaptadorJuegos.getFilter().filter(usuarioActividad.getQueryText());

            }
        });

        spi_categoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                adaptadorJuegos.setCategoria(categorias.get(i));
                adaptadorJuegos.getFilter().filter(usuarioActividad.getQueryText());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void cargarJuegos(){
        ref.child("tienda").child("juegos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //TODO: METER TODAS LAS LLAMADAS A LA BASE DE DATOS EN UN HILO
                lista_juegos.clear();

                for(DataSnapshot hijo: snapshot.getChildren()){
                    Juego pojo_juego = hijo.getValue(Juego.class);
                    if(pojo_juego.isDisponible() && pojo_juego.getStock() > 0){
                        pojo_juego.setId(hijo.getKey());
                        lista_juegos.add(pojo_juego);
                    }
                    adaptadorJuegos.notifyDataSetChanged();
                }
                int precio_maximo_tienda = 0;
                for(final Juego pojo_juego: lista_juegos){
                    if(pojo_juego.getPrecio() > precio_maximo_tienda){
                        precio_maximo_tienda = (int)Math.ceil(pojo_juego.getPrecio());
                    }
                    sto.child("tienda").child("juegos").child(pojo_juego.getId()).getDownloadUrl()
                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    pojo_juego.setUrl_juego(uri.toString());
                                    adaptadorJuegos.notifyDataSetChanged();
                                }
                            });
                }

                setSeekBarMax(precio_maximo_tienda);
                añadirCategorias();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setSeekBarMax(int precioMax){
        sb_max.setMax(precioMax);
        sb_min.setMax(precioMax - 1);
        sb_max.setProgress(precioMax);
    }


    private void añadirCategorias(){
        if(!categorias.contains("Todas")){
            categorias.add("Todas");
        }
        for(Juego juego: lista_juegos){
            if(!categorias.contains(juego.getCategoria())){
                categorias.add(juego.getCategoria());
            }
        }
        categoriaAdapter.notifyDataSetChanged();
    }
}