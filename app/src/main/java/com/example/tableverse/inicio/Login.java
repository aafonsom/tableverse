package com.example.tableverse.inicio;

import android.content.Intent;
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
import com.example.tableverse.UsuarioActividad;
import com.example.tableverse.objetos.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Login#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Login extends Fragment {

    private DatabaseReference ref;
    private StorageReference sto;
    private EditText et_nombre, et_pass;
    private Usuario usuario;


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public Login() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Login.
     */
    // TODO: Rename and change types and number of parameters
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

        LoginActividad loginActividad = (LoginActividad)getActivity();
        ref = loginActividad.getRef();


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login(view);
            }
        });
    }

    private void login(View v){
        String nombre = et_nombre.getText().toString().trim();
        final String pass = et_pass.getText().toString().trim();

        if(nombre.equals("") || pass.equals("")){
            Toast.makeText(getContext(), "Los campos no pueden estar vacíos", Toast.LENGTH_SHORT).show();
        }else{
            ref.child("tienda").child("clientes").orderByChild("nombre").equalTo(nombre)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.hasChildren()){
                                DataSnapshot dataSnapshot = snapshot.getChildren().iterator().next();
                                usuario = dataSnapshot.getValue(Usuario.class);
                                if(usuario.getContraseña().equals(pass)){
                                    usuario.setId(snapshot.getKey());

                                    loguearse();
                                }else{
                                    Toast.makeText(getContext(), "Contraseña incorrecta", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(getContext(), "No existe ningún usuario con ese nombre", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
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