package com.example.tableverse.usuario;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.solver.widgets.Snapshot;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.List;


public class EventosApuntado extends Fragment {

    private List<Evento> lista_evento;
    private Usuario usuario;
    private DatabaseReference ref;
    private StorageReference sto;
    private RecyclerView rv_evento;
    private AdaptadorEventos adaptadorEventos;
    private LinearLayoutManager llm;
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

        usuarioActividad = (UsuarioActividad)getActivity();
        lista_evento = usuarioActividad.getLista_eventos();
        adaptadorEventos = usuarioActividad.getAdaptadorEventos();
        ref = usuarioActividad.getRef();
        sto = usuarioActividad.getSto();
        usuario = usuarioActividad.getUsuario();

        cargarEventos();

        llm = new LinearLayoutManager(getContext());
        adaptadorEventos = new AdaptadorEventos(lista_evento, getContext(), usuarioActividad);
        rv_evento.setAdapter(adaptadorEventos);
        rv_evento.setLayoutManager(llm);
    }

    private void cargarEventos(){
        ref.child("tienda").child("reservas_eventos").orderByKey()
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lista_evento.clear();
                boolean apuntadoAlgunEvento = false;
                for(DataSnapshot hijo: snapshot.getChildren()){
                    ReservaEvento reserva = hijo.getValue(ReservaEvento.class);

                    if(reserva.getId_cliente().equals(usuario.getId())){
                        apuntadoAlgunEvento = true;
                        ref.child("tienda").child("eventos").child(reserva.getId_evento())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    Evento pojo_evento = snapshot.getValue(Evento.class);
                                    pojo_evento.setId(snapshot.getKey());
                                    lista_evento.add(pojo_evento);
                                    adaptadorEventos.notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                    }
                }

                if(!apuntadoAlgunEvento){
                    Toast.makeText(getContext(), "No estás apuntado a ningún evento", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}