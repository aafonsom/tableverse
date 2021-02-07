package com.example.tableverse.admin;

import android.content.Context;
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
import android.widget.LinearLayout;

import com.example.tableverse.AdminActividad;
import com.example.tableverse.R;
import com.example.tableverse.adaptadores.AdaptadorEventos;
import com.example.tableverse.objetos.Evento;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListaEventosAdmin#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListaEventosAdmin extends Fragment {
    private AdminActividad adminActividad;
    private final int MODO_FAB = 2;
    private final int MODO_NAVVIEW = 1;
    private RecyclerView rv_eventos;
    private AdaptadorEventos adaptador;
    private LinearLayoutManager linearLayoutManager;
    private DatabaseReference ref;
    private StorageReference sto;
    private List<Evento> lista_eventos;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public ListaEventosAdmin() {
        // Required empty public constructor
    }

    public static ListaEventosAdmin newInstance(String param1, String param2) {
        ListaEventosAdmin fragment = new ListaEventosAdmin();
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
        return inflater.inflate(R.layout.fragment_lista_eventos_admin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rv_eventos = view.findViewById(R.id.rv_eventos);

        adminActividad = (AdminActividad)getActivity();
        lista_eventos = adminActividad.getLista_eventos();
        adminActividad.modoFab(MODO_FAB);
        adminActividad.modoNavView(MODO_NAVVIEW);
        ref = adminActividad.getRef();
        sto = adminActividad.getSto();

        cargarEventos();
        linearLayoutManager = new LinearLayoutManager(getContext());
        adaptador = new AdaptadorEventos(lista_eventos, getContext(), adminActividad);
        rv_eventos.setAdapter(adaptador);
        rv_eventos.setLayoutManager(linearLayoutManager);
    }

    private void cargarEventos(){
        ref.child("tienda").child("eventos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lista_eventos.clear();
                for (DataSnapshot datasnapshot : snapshot.getChildren()) {
                    Evento pojo_evento = datasnapshot.getValue(Evento.class);
                    pojo_evento.setId(datasnapshot.getKey());

                    lista_eventos.add(pojo_evento);
                }
                //Mejorar, hacerlo con asynctask
                for(final Evento pojo_evento: lista_eventos){
                    sto.child("tienda").child("eventos").child(pojo_evento.getId())
                            .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            pojo_evento.setUrlImagen(uri.toString());
                            adaptador.notifyDataSetChanged();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            adaptador.notifyDataSetChanged();
                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}