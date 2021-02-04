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
import android.widget.Toast;

import com.example.tableverse.AdminActividad;
import com.example.tableverse.LoginActividad;
import com.example.tableverse.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CrearJuego#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CrearJuego extends Fragment {
    private final int MODO_FAB = 3;
    //Si no doy más uso, hacerlo variable local
    private AdminActividad adminActividad;
    private EditText et_nombre, et_categoria, et_precio, et_stock;


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public CrearJuego() {
        // Required empty public constructor
    }

    public static CrearJuego newInstance(String param1, String param2) {
        CrearJuego fragment = new CrearJuego();
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
        return inflater.inflate(R.layout.fragment_crear_juego, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        et_nombre = view.findViewById(R.id.et_nombre_juego);
        et_categoria = view.findViewById(R.id.et_categoria_juego);
        et_precio = view.findViewById(R.id.et_precio_juego);
        et_stock = view.findViewById(R.id.et_stock);
        Button b = view.findViewById(R.id.b_addjuego);

        adminActividad = (AdminActividad)getActivity();
        adminActividad.modoFab(MODO_FAB);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                crearJuego();
            }
        });

    }

    private void crearJuego(){
        String nombre, categoria;
        double precio;
        int stock;

        nombre = et_nombre.getText().toString().trim();
        categoria = et_nombre.getText().toString().trim();
        try{
            precio = Double.parseDouble(et_precio.toString().trim());
            stock = Integer.parseInt(et_stock.toString().trim());
            boolean validacion = validar(nombre, categoria, precio, stock);
            if(validacion){

            }else{

            }
        }catch(NumberFormatException nfe){
            Toast.makeText(getContext(), "Ni el precio ni el stock pueden estar vacíos", Toast.LENGTH_SHORT).show();
        }

    }

    private boolean validar(String nombre, String categoria, double precio, int stock){
        boolean validado = true;

        if(nombre.equals("") || categoria.equals("") || precio == 0.0 || stock == 0){
            validado = false;
        }

        return validado;
    }


}