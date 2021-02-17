package com.example.tableverse.usuario;

import android.animation.ObjectAnimator;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.tableverse.R;
import com.example.tableverse.UsuarioActividad;
import com.example.tableverse.adaptadores.AdaptadorEventos;
import com.example.tableverse.objetos.Evento;
import com.example.tableverse.objetos.ReservaEvento;
import com.example.tableverse.objetos.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


public class EventosApuntado extends Fragment {

    private List<Evento> lista_evento;
    private Usuario usuario;
    private DatabaseReference ref;
    private StorageReference sto;
    private RecyclerView rv_evento;
    private AdaptadorEventos adaptadorEventos;
    private StaggeredGridLayoutManager glm;
    private UsuarioActividad usuarioActividad;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public EventosApuntado() {

    }

    public static EventosApuntado newInstance(String param1, String param2) {
        EventosApuntado fragment = new EventosApuntado();
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
        return inflater.inflate(R.layout.fragment_eventos_apuntado, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rv_evento = view.findViewById(R.id.rv_eventos_apuntado);
        final ImageView changeView = view.findViewById(R.id.iv_changeview_eventos2);


        usuarioActividad = (UsuarioActividad)getActivity();
        lista_evento = usuarioActividad.getLista_eventos();
        adaptadorEventos = usuarioActividad.getAdaptadorEventos();
        usuarioActividad.setVistaLineal(false);
        ref = usuarioActividad.getRef();
        sto = usuarioActividad.getSto();
        usuario = usuarioActividad.getUsuario();
        usuarioActividad.setVisibilitySearchView(0);
        cargarEventos();

        glm = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        adaptadorEventos = new AdaptadorEventos(lista_evento, getContext(), usuarioActividad);
        rv_evento.setAdapter(adaptadorEventos);
        rv_evento.setLayoutManager(glm);

        changeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(usuarioActividad.isVistaLineal()){
                    usuarioActividad.setVistaLineal(false);
                    ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(changeView, "alpha",0, 1);
                    objectAnimator.setDuration(1500);
                    objectAnimator.setStartDelay(0);
                    objectAnimator.start();
                    changeView.setImageResource(R.drawable.format_list_bulleted_24px);
                }else{
                    usuarioActividad.setVistaLineal(true);
                    ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(changeView, "alpha",0, 1);
                    objectAnimator.setDuration(1500);
                    objectAnimator.setStartDelay(0);
                    objectAnimator.start();
                    changeView.setImageResource(R.drawable.ic_dashboard_black_24dp);

                }
                changeRecyclerView();
            }
        });
    }

    private void changeRecyclerView(){
        usuarioActividad.adaptadorEventos = new AdaptadorEventos(lista_evento, getContext(), usuarioActividad);

        if(usuarioActividad.isVistaLineal()){
            LinearLayoutManager llm = new LinearLayoutManager(getContext());
            rv_evento.setAdapter(usuarioActividad.adaptadorEventos);
            rv_evento.setLayoutManager(llm);
        }else {
            glm = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            rv_evento.setAdapter(usuarioActividad.adaptadorEventos);
            rv_evento.setLayoutManager(glm);
        }
        adaptadorEventos = usuarioActividad.adaptadorEventos;
    }


    private void cargarEventos(){
        ref.child("tienda").child("reservas_eventos").orderByKey()
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lista_evento.clear();

                for(DataSnapshot hijo: snapshot.getChildren()){
                    ReservaEvento reserva = hijo.getValue(ReservaEvento.class);

                    if(reserva.getId_cliente().equals(usuario.getId())){

                        ref.child("tienda").child("eventos").child(reserva.getId_evento())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(!snapshot.hasChildren()){
                                        Toast.makeText(getContext(), "No estás apuntado a ningún evento", Toast.LENGTH_SHORT).show();
                                    }
                                    Evento pojo_evento = snapshot.getValue(Evento.class);
                                    pojo_evento.setId(snapshot.getKey());
                                    lista_evento.add(pojo_evento);
                                    adaptadorEventos.notifyDataSetChanged();
                                    ordenarEventos();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void ordenarEventos(){
        Collections.sort(lista_evento, new Comparator<Evento>() {
            @Override
            public int compare(Evento evento, Evento t1) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Calendar fecha_evento1 = Calendar.getInstance();
                Calendar fecha_evento2 = Calendar.getInstance();
                try {
                    Date fecha = sdf.parse(evento.getFecha());
                    fecha_evento1.setTime(fecha);
                    fecha = sdf.parse(t1.getFecha());
                    fecha_evento2.setTime(fecha);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                int valor = 0;
                if(fecha_evento1.before(fecha_evento2)){
                    valor = 1;
                }else if(fecha_evento2.before(fecha_evento1)){
                    valor = -1;
                }

                return valor;
            }
        });
        adaptadorEventos.notifyDataSetChanged();

    }
}