package com.example.tableverse.admin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tableverse.AdminActividad;
import com.example.tableverse.R;
import com.example.tableverse.objetos.Evento;

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
    private List<Evento> lista_eventos = new ArrayList<>();

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
        adminActividad = (AdminActividad)getActivity();
        adminActividad.modoFab(MODO_FAB);
        adminActividad.modoNavView(MODO_NAVVIEW);
    }

}