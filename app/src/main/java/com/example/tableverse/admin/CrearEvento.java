package com.example.tableverse.admin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.tableverse.AdminActividad;
import com.example.tableverse.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CrearEvento#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CrearEvento extends Fragment {
    private final int MODO_FAB = 3;
    private final int MODO_NAVVIEW = 2;
    private static final int SELECCIONAR_FOTO = 1;
    private EditText et_nombre, et_precio, et_fecha, et_aforo;
    private ImageView iv_foto;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public CrearEvento() {
        // Required empty public constructor
    }

    public static CrearEvento newInstance(String param1, String param2) {
        CrearEvento fragment = new CrearEvento();
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
        return inflater.inflate(R.layout.fragment_crear_evento, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        et_nombre = view.findViewById(R.id.et_evento);
        et_aforo = view.findViewById(R.id.et_aforo);
        et_fecha = view.findViewById(R.id.et_fecha_evento);
        et_precio = view.findViewById(R.id.et_precio);
        Button addEvent = view.findViewById(R.id.b_add_event);

        AdminActividad adminActividad = (AdminActividad)getActivity();
        adminActividad.modoFab(MODO_FAB);
        adminActividad.modoNavView(MODO_NAVVIEW);

        addEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                crearEvento(view);
            }
        });
    }

    public void crearEvento(View v){
        String nombre, fecha;
        double precio;
        int aforo_maximo;

        nombre = et_nombre.getText().toString().trim();
        fecha = et_fecha.getText().toString().trim();
        precio = Double.parseDouble(et_precio.getText().toString().trim());
        aforo_maximo = Integer.parseInt(et_aforo.getText().toString().trim());
        
    }


}