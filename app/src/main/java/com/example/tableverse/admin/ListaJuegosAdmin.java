package com.example.tableverse.admin;

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

import com.example.tableverse.AdminActividad;
import com.example.tableverse.R;
import com.example.tableverse.adaptadores.AdaptadorJuegos;
import com.example.tableverse.objetos.Juego;
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
 * Use the {@link ListaJuegosAdmin#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListaJuegosAdmin extends Fragment {

    private AdminActividad adminActividad;
    private final int MODO_FAB = 1;
    private DatabaseReference ref;
    private StorageReference sto;
    private RecyclerView rv_juegos;
    private AdaptadorJuegos adaptadorJuegos;
    private LinearLayoutManager linearLayoutManager;
    private List<Juego> lista_juegos = new ArrayList<>();


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public ListaJuegosAdmin() {
        // Required empty public constructor
    }

    public static ListaJuegosAdmin newInstance(String param1, String param2) {
        ListaJuegosAdmin fragment = new ListaJuegosAdmin();
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
        return inflater.inflate(R.layout.fragment_lista_juegos_admin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rv_juegos = view.findViewById(R.id.rv_juegos_admin);
        adminActividad = (AdminActividad)getActivity();
        adminActividad.modoFab(MODO_FAB);

        ref = adminActividad.getRef();
        sto = adminActividad.getSto();

        cargarJuegos();
        adaptadorJuegos = new AdaptadorJuegos(lista_juegos, getContext());
        linearLayoutManager = new LinearLayoutManager(getContext());
        rv_juegos.setAdapter(adaptadorJuegos);
        rv_juegos.setLayoutManager(linearLayoutManager);

    }

    private void cargarJuegos(){
        ref.child("tienda").child("juegos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lista_juegos.clear();

                for (DataSnapshot hijo : snapshot.getChildren()) {
                    Juego pojo_juego = hijo.getValue(Juego.class);
                    pojo_juego.setId(hijo.getKey());
                    lista_juegos.add(pojo_juego);
                }

                for (final Juego juego : lista_juegos) {
                    sto.child("tienda").child("juegos").child(juego.getId())
                            .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            juego.setUrl_juego(uri.toString());
                            adaptadorJuegos.notifyDataSetChanged();
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