package com.example.tableverse.usuario;

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

import com.example.tableverse.R;
import com.example.tableverse.UsuarioActividad;
import com.example.tableverse.adaptadores.AdaptadorPedidos;
import com.example.tableverse.adaptadores.AdaptadorPedidosUsuario;
import com.example.tableverse.objetos.ReservaJuego;
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


public class HistorialPedidos extends Fragment {
    private RecyclerView rv_historial;
    private AdaptadorPedidosUsuario adaptadorPedidos;
    private List<ReservaJuego> lista_reservas = new ArrayList<>();
    private LinearLayoutManager llm;
    private DatabaseReference ref;
    private StorageReference sto;
    private Usuario usuario;
    private UsuarioActividad usuarioActividad;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public HistorialPedidos() {
        // Required empty public constructor
    }


    public static HistorialPedidos newInstance(String param1, String param2) {
        HistorialPedidos fragment = new HistorialPedidos();
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
        return inflater.inflate(R.layout.fragment_historial_pedidos, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rv_historial = view.findViewById(R.id.rv_historial);

        usuarioActividad = (UsuarioActividad)getActivity();
        ref = usuarioActividad.getRef();
        sto = usuarioActividad.getSto();
        usuarioActividad.setVisibilitySearchView(0);
        usuario = usuarioActividad.getUsuario();
        cargarDatos();
        adaptadorPedidos = new AdaptadorPedidosUsuario(lista_reservas, getContext(), usuarioActividad);
        llm = new LinearLayoutManager(getContext());
        rv_historial.setAdapter(adaptadorPedidos);
        rv_historial.setLayoutManager(llm);

    }


    private void cargarDatos(){
        ref.child("tienda").child("reservas_juegos").orderByChild("id_cliente")
                .equalTo(usuario.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lista_reservas.clear();
                for (DataSnapshot hijo : snapshot.getChildren()) {
                    ReservaJuego reserva = hijo.getValue(ReservaJuego.class);
                    reserva.setId(hijo.getKey());
                    lista_reservas.add(reserva);
                }

                adaptadorPedidos.notifyDataSetChanged();

                for(final ReservaJuego res: lista_reservas){
                    sto.child("tienda").child("juegos").child(res.getId_juego()).getDownloadUrl()
                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    res.setUrl_juego(uri.toString());
                                    adaptadorPedidos.notifyDataSetChanged();
                                }
                            });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /*private class CargarImagen extends Thread{
        ReservaJuego pojo_reserva;
        public CargarImagen(ReservaJuego pojo_reserva) {
            this.pojo_reserva = pojo_reserva;
        }

        @Override
        public void run(){
            sto.child("tienda").child("juegos").child(pojo_reserva.getId_juego()).getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        pojo_reserva.setUrl_juego(uri.toString());
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adaptadorPedidos.notifyDataSetChanged();
                            }
                        });
                    }
                });

        }*/
/*

    }
*/


}