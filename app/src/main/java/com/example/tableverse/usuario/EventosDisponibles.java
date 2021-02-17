package com.example.tableverse.usuario;

import android.animation.ObjectAnimator;
import android.net.Uri;
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
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;

import com.example.tableverse.AppUtilities;
import com.example.tableverse.R;
import com.example.tableverse.UsuarioActividad;
import com.example.tableverse.adaptadores.AdaptadorEventos;
import com.example.tableverse.adaptadores.AdaptadorJuegos;
import com.example.tableverse.objetos.Evento;
import com.example.tableverse.objetos.Juego;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class EventosDisponibles extends Fragment {

    private Switch sw_eventos;
    private RecyclerView rv_eventos;
    private AdaptadorEventos adaptadorEventos;
    private StaggeredGridLayoutManager glm;
    private List<Evento> lista_eventos;
    private DatabaseReference ref;
    private StorageReference sto;
    private UsuarioActividad usuarioActividad;


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public EventosDisponibles() {
        // Required empty public constructor
    }

    public static EventosDisponibles newInstance(String param1, String param2) {
        EventosDisponibles fragment = new EventosDisponibles();
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
        return inflater.inflate(R.layout.fragment_eventos_disponibles, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sw_eventos = view.findViewById(R.id.sw_gratuitos);
        rv_eventos = view.findViewById(R.id.rv_eventos);
        final ImageView changeView = view.findViewById(R.id.iv_changeview_eventos);

        usuarioActividad = (UsuarioActividad)getActivity();

        lista_eventos = usuarioActividad.getLista_eventos();
        adaptadorEventos = usuarioActividad.getAdaptadorEventos();
        ref = usuarioActividad.getRef();
        sto = usuarioActividad.getSto();
        usuarioActividad.setVistaLineal(false);
        usuarioActividad.setVisibilitySearchView(0);
        cargarEventos();

        glm = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        adaptadorEventos = new AdaptadorEventos(lista_eventos, getContext(), usuarioActividad);


        rv_eventos.setAdapter(adaptadorEventos);
        rv_eventos.setLayoutManager(glm);

        sw_eventos.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                adaptadorEventos.filtroGratuito(b);
            }
        });

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

    private void setAdaptador(){
        usuarioActividad.adaptadorEventos = new AdaptadorEventos(lista_eventos, getContext(), usuarioActividad);

        if(usuarioActividad.isVistaLineal()){
            LinearLayoutManager llm = new LinearLayoutManager(getContext());
            rv_eventos.setAdapter(usuarioActividad.adaptadorEventos);
            rv_eventos.setLayoutManager(llm);
        }else {
            glm = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            rv_eventos.setAdapter(usuarioActividad.adaptadorEventos);
            rv_eventos.setLayoutManager(glm);
        }
        adaptadorEventos = usuarioActividad.adaptadorEventos;
    }

    private void changeRecyclerView(){
        setAdaptador();
        adaptadorEventos.filtroGratuito(sw_eventos.isChecked());

    }

    private void cargarEventos(){
        ref.child("tienda").child("eventos").orderByKey()
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lista_eventos.clear();
                AppUtilities utilities = new AppUtilities();

                for(DataSnapshot hijo: snapshot.getChildren()){
                    Evento pojo_evento = hijo.getValue(Evento.class);
                    boolean noHaPasado = utilities.esPosterior(pojo_evento.getFecha());
                    if(pojo_evento.getAforoMax() > pojo_evento.getOcupado() && noHaPasado){
                        pojo_evento.setId(hijo.getKey());
                        lista_eventos.add(pojo_evento);
                    }
                }
                adaptadorEventos.notifyDataSetChanged();

                for(final Evento evento: lista_eventos){
                    sto.child("tienda").child("eventos").child(evento.getId()).getDownloadUrl()
                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    evento.setUrlImagen(uri.toString());
                                    adaptadorEventos.notifyDataSetChanged();
                                }
                            });
                }
                ordenarEventos();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void ordenarEventos(){
        Collections.sort(lista_eventos, new Comparator<Evento>() {
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
                    valor = -1;
                }else if(fecha_evento2.before(fecha_evento1)){
                    valor = 1;
                }

                return valor;
            }
        });
        adaptadorEventos.notifyDataSetChanged();

    }

}