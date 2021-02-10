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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.tableverse.AdminActividad;
import com.example.tableverse.R;
import com.example.tableverse.adaptadores.AdaptadorPedidos;
import com.example.tableverse.objetos.ReservaJuego;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class ListaPedidos extends Fragment {
    private AdminActividad adminActividad;
    private final int MODO_FAB = 3;
    private List<ReservaJuego> lista_pedidos;
    private AdaptadorPedidos adaptadorPedidos;
    private LinearLayoutManager llm;
    private DatabaseReference ref;
    private StorageReference sto;
    private RecyclerView rv_pedidos;
    private Spinner spi_tipo;
    private final String[] tipo = {"En preparación", "Preparados"};

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    public ListaPedidos() {
        // Required empty public constructor
    }


    public static ListaPedidos newInstance(String param1, String param2) {
        ListaPedidos fragment = new ListaPedidos();
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

        return inflater.inflate(R.layout.fragment_lista_pedidos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rv_pedidos = view.findViewById(R.id.rv_pedidos);
        spi_tipo = view.findViewById(R.id.spi_tipo_pedido);

        adminActividad = (AdminActividad)getActivity();
        adminActividad.modoFab(MODO_FAB);
        lista_pedidos = adminActividad.getLista_pedidos();
        ref = adminActividad.getRef();
        sto = adminActividad.getSto();

        cargarPedidos();

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, tipo);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spi_tipo.setAdapter(spinnerAdapter);

        adaptadorPedidos = new AdaptadorPedidos(lista_pedidos, getContext());
        llm = new LinearLayoutManager(getContext());
        rv_pedidos.setAdapter(adaptadorPedidos);
        rv_pedidos.setLayoutManager(llm);

        spi_tipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                adaptadorPedidos.filtroTipo(tipo[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void cargarPedidos(){
        ref.child("tienda").child("reservas_juegos").orderByKey().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lista_pedidos.clear();
                for(DataSnapshot hijo: snapshot.getChildren()){
                    ReservaJuego pojo_reserva = hijo.getValue(ReservaJuego.class);
                    pojo_reserva.setId(hijo.getKey());
                    lista_pedidos.add(pojo_reserva);
                }


                adaptadorPedidos.notifyDataSetChanged();

                for(ReservaJuego reserva: lista_pedidos) {
                    CargarImagen ci = new CargarImagen(reserva);
                    ci.start();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private class CargarImagen extends Thread{
        private ReservaJuego pojo_reserva;

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
        }

    }

}