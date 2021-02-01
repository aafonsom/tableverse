package com.example.tableverse.inicio;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tableverse.LoginActividad;
import com.example.tableverse.R;
import com.example.tableverse.objetos.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Registro#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Registro extends Fragment {

    private EditText et_nombre, et_pass, et_passrepit, et_email;
    private DatabaseReference ref;
    private StorageReference sto;
    private Uri fotoUsuario_url;
    private NavController navController;


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public Registro() {
        // Required empty public constructor
    }

    public static Registro newInstance(String param1, String param2) {
        Registro fragment = new Registro();
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

        return inflater.inflate(R.layout.fragment_registro, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        et_nombre = view.findViewById(R.id.et_nombre_usuario);
        et_pass = view.findViewById(R.id.et_registro_pass);
        et_passrepit = view.findViewById(R.id.et_registro_repitpass);
        et_email = view.findViewById(R.id.et_email);
        LoginActividad loginActividad = (LoginActividad)getActivity();
        navController = loginActividad.navController;
        ref = loginActividad.ref;
        sto = loginActividad.sto;
        Button b = view.findViewById(R.id.b_registro);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registrarse(view);
            }
        });
    }

    public void registrarse(View v){
        final String nombre, pass, repit_pass, email;
        nombre = et_nombre.getText().toString().trim();
        pass = et_pass.getText().toString().trim();
        repit_pass = et_pass.getText().toString().trim();
        email = et_email.getText().toString().trim();
        boolean validacion = validar(nombre, pass, repit_pass, email);

        if(validacion){
            ref.child("tienda").child("clientes").equalTo(nombre)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.hasChildren()){
                        Toast.makeText(getContext(), "Este nombre de usuario ya existe", Toast.LENGTH_SHORT).show();
                    }else{

                        Usuario nuevo = new Usuario(nombre, email, pass);
                        String id = ref.child("tienda").child("clientes").push().getKey();
                        ref.child("tienda").child("clientes").child(id).setValue(nuevo);
                        if(!fotoUsuario_url.equals("")){
                            sto.child("tienda").child("usuarios").child(id).putFile(fotoUsuario_url);
                        }

                        navController.navigate(R.id.login);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }else{
            Toast.makeText(getContext(), "No se han validado los datos", Toast.LENGTH_SHORT).show();
        }


    }

    public boolean validar(String nombre, String pass, String repit_pass, String email){
        boolean validacion = true;
        //mejorar valicacion
        if(nombre.equals("") || !pass.equals(repit_pass) || email.equals("")){
            validacion = false;
        }

        return validacion;
    }




}