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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListaJuegosAdmin#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListaJuegosAdmin extends Fragment {

    private AdminActividad adminActividad;
    private final int MODO_FAB = 1;
    private RecyclerView rv_juegos;


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
    }
}