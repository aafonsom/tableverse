package com.example.tableverse.inicio;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.tableverse.AppUtilities;
import com.example.tableverse.LoginActividad;
import com.example.tableverse.R;
import com.example.tableverse.UsuarioActividad;
import com.example.tableverse.objetos.Usuario;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Login#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Login extends Fragment {

    private DatabaseReference ref;
    private StorageReference sto;
    private EditText et_nombre, et_pass;
    private TextInputLayout til_pass;
    private Usuario usuario;
    private SharedPreferences sp;


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public Login() {
        // Required empty public constructor
    }


    public static Login newInstance(String param1, String param2) {
        Login fragment = new Login();
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
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button login = view.findViewById(R.id.b_login);

        et_nombre = view.findViewById(R.id.et_usuario);
        et_pass = view.findViewById(R.id.et_password);
        til_pass = view.findViewById(R.id.til_pass);

        sp = getContext().getSharedPreferences("LOGIN", MODE_PRIVATE);

        LoginActividad loginActividad = (LoginActividad)getActivity();
        ref = loginActividad.getRef();

    /*    String id = sp.getString("id", "");
        if(!id.equals("")){
            cargarUsuario(id);
        }*/


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login(view);
            }
        });
    }

    private void cargarUsuario(String id){
        ref.child("tienda").child("clientes").orderByKey()
                .equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChildren()){
                    DataSnapshot dataSnapshot = snapshot.getChildren().iterator().next();
                    usuario = dataSnapshot.getValue(Usuario.class);
                    usuario.setId(snapshot.getKey());
                    loguearse();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void login(View v){
        String nombre = et_nombre.getText().toString().trim();
        final String pass = et_pass.getText().toString().trim();

        if(validar(nombre, pass)){
            ref.child("tienda").child("clientes").orderByChild("nombre").equalTo(nombre)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChildren()){
                            DataSnapshot dataSnapshot = snapshot.getChildren().iterator().next();
                            usuario = dataSnapshot.getValue(Usuario.class);
                            if(usuario.getContraseña().equals(pass)){
                                usuario.setId(dataSnapshot.getKey());
                                guardarShared();
                                loguearse();
                            }else{
                                til_pass.setError("La contraseña es incorrecta");
                            }
                        }else{
                            et_nombre.setError("No existe este usuario");
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        }
    }

    private boolean validar(String nombre, String pass){
        boolean validar = true;

        if(nombre.equals("")){
            validar = false;
            et_nombre.setError("El nombre no puede estar vacío");
        }

        if(pass.equals("")){
            validar = false;
            til_pass.setError("La contraseña no puede estar vacía");
        }


        return validar;
    }

    private void guardarShared(){
        SharedPreferences.Editor edit = sp.edit();
        edit.putString("id", usuario.getId());
        edit.commit();
    }

    private void loguearse(){
        Intent intent;
        if(usuario.getTipo().equals("admin")){
            intent = new Intent(getContext(), AdminActividad.class);
        }else{
            intent = new Intent(getContext(), UsuarioActividad.class);
        }
        startActivity(intent);
    }



}