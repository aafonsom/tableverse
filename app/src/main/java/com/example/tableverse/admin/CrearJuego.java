package com.example.tableverse.admin;

import android.content.Intent;
import android.net.Uri;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.tableverse.AdminActividad;
import com.example.tableverse.R;
import com.example.tableverse.objetos.Juego;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CrearJuego#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CrearJuego extends Fragment {
    private final int MODO_FAB = 3;
    private final int MODO_NAVVIEW = 2;
    private static final int SELECCIONAR_FOTO = 1;

    private ImageView fotoJuego;
    private TextView error;
    private EditText et_nombre, et_categoria, et_precio, et_stock;
    private DatabaseReference ref;
    private StorageReference sto;
    private Uri fotoJuegoUrl;


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
        et_stock = view.findViewById(R.id.et_fecha);
        fotoJuego = view.findViewById(R.id.iv_foto_evento);
        error = view.findViewById(R.id.tv_error);

        Button añadir = view.findViewById(R.id.b_addjuego);

        AdminActividad adminActividad = (AdminActividad)getActivity();
        adminActividad.getToolbar().setNavigationIcon(R.drawable.arrow_back_white_24dp);
        adminActividad.modoFab(MODO_FAB);
        adminActividad.modoNavView(MODO_NAVVIEW);
        ref = adminActividad.getRef();
        sto = adminActividad.getSto();

        fotoJuegoUrl = null;
        añadir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                crearJuego();
            }
        });
        fotoJuego.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seleccionarFoto(view);
            }
        });

    }

    private void crearJuego(){
        final String nombre, categoria;
        final String precio;
        final String stock;

        nombre = et_nombre.getText().toString().trim();
        categoria = et_categoria.getText().toString().trim();
        precio = et_precio.getText().toString().trim();
        stock = et_stock.getText().toString().trim();


        boolean validacion = validar(nombre, categoria, precio, stock);
        if(validacion){
            ref.child("tienda").child("juegos").orderByChild("nombre").equalTo(nombre)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChildren()){
                            et_nombre.setError("Ya existe un juego con ese nombre");
                        }else{
                            Juego pojo_juego = new Juego(nombre, categoria, Double.parseDouble(precio), Integer.parseInt(stock));
                            String id = ref.child("tienda").child("juegos").push().getKey();
                            ref.child("tienda").child("juegos").child(id).setValue(pojo_juego);
                            sto.child("tienda").child("juegos").child(id).putFile(fotoJuegoUrl);
                            AdminActividad adminActividad = (AdminActividad)getActivity();
                            adminActividad.getNavController().navigate(R.id.listaJuegosAdmin);

                            try {
                                Thread.sleep(900);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        }else{
            Toast.makeText(getContext(), "Hay campos no validados", Toast.LENGTH_SHORT).show();
        }

    }

    private boolean validar(String nombre, String categoria, String precio, String stock){
        boolean validado = true;

        if(nombre.equals("")){
            et_nombre.setError("El nombre no puede estar vacío");
            validado = false;
        }

        if(categoria.isEmpty()){
            et_categoria.setError("La categoría no puede estar vacía");
            validado = false;
        }

        if(precio.isEmpty()){
            et_precio.setError("El precio no puede estar vacío");
            validado = false;
        }else{
            double pre = Double.parseDouble(precio);
            if(pre == 0){
                et_precio.setError("El precio no puede ser 0");
                validado = false;
            }
        }

        if(stock.isEmpty()){
            et_stock.setError("El stock no puede estar vacío");
            validado = false;
        }

        if(fotoJuegoUrl == null){
            error.setText("Es obligatorio seleccionar una foto");
            error.setVisibility(View.VISIBLE);
            validado = false;
        }

        return validado;
    }

    public void seleccionarFoto(View v){
        Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,SELECCIONAR_FOTO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK && requestCode==SELECCIONAR_FOTO){
            fotoJuegoUrl=data.getData();
            fotoJuego.setImageURI(fotoJuegoUrl);
            fotoJuego.setImageTintMode(null);
            Toast.makeText(getContext(), "Foto de perfil seleccionada con éxito", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getContext(), "Fallo al seleccionar la foto de perfil", Toast.LENGTH_SHORT).show();
        }
    }



}