package com.example.tableverse.usuario;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.example.tableverse.AppUtilities;
import com.example.tableverse.R;
import com.example.tableverse.UsuarioActividad;
import com.example.tableverse.adaptadores.AdaptadorEventos;
import com.example.tableverse.objetos.Evento;
import com.example.tableverse.objetos.Juego;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class EventosDisponibles extends Fragment {

    private Switch sw_eventos;
    private RecyclerView rv_eventos;
    private AdaptadorEventos adaptadorEventos;
    private LinearLayoutManager llm;
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

        usuarioActividad = (UsuarioActividad)getActivity();

        lista_eventos = usuarioActividad.getLista_eventos();
        adaptadorEventos = usuarioActividad.getAdaptadorEventos();
        ref = usuarioActividad.getRef();
        sto = usuarioActividad.getSto();
        cargarEventos();

        llm = new LinearLayoutManager(getContext());
        adaptadorEventos = new AdaptadorEventos(lista_eventos, getContext(), usuarioActividad);


        rv_eventos.setAdapter(adaptadorEventos);
        rv_eventos.setLayoutManager(llm);

        sw_eventos.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                adaptadorEventos.filtroGratuito(b);
            }
        });
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

                for(Evento evento: lista_eventos){
                    CargarImagen ci = new CargarImagen(evento);
                    ci.start();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private class CargarImagen extends Thread{
        Evento pojo_evento;

        public CargarImagen(Evento pojo_evento) {
            this.pojo_evento = pojo_evento;
        }

        @Override
        public void run(){
            sto.child("tienda").child("eventos").child(pojo_evento.getId()).getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            pojo_evento.setUrlImagen(uri.toString());
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adaptadorEventos.notifyDataSetChanged();
                                }
                            });
                        }
                    });
        }
    }

}