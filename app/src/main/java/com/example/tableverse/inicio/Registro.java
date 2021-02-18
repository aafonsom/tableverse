package com.example.tableverse.inicio;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;

import android.provider.Settings;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.tableverse.AppUtilities;
import com.example.tableverse.LoginActividad;
import com.example.tableverse.R;
import com.example.tableverse.objetos.Usuario;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Registro#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Registro extends Fragment {

    private static final int SELECCIONAR_FOTO = 1;
    private EditText et_nombre, et_pass, et_passrepit, et_email;
    private TextInputLayout til_pass;
    private ImageView fotoElegida;
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
        fotoElegida = view.findViewById(R.id.iv_foto_perfil);
        til_pass = view.findViewById(R.id.til_pass);

        LoginActividad loginActividad = (LoginActividad)getActivity();
        navController = loginActividad.getNavController();
        ref = loginActividad.getRef();
        sto = loginActividad.getSto();
        Button b = view.findViewById(R.id.b_registro);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registrarse(view);
            }
        });
        fotoElegida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seleccionarFotoPerfil(view);
            }
        });

        fotoUsuario_url = null;
    }

    public void registrarse(View v){
        final String nombre, pass, repit_pass, email;
        nombre = et_nombre.getText().toString().trim();
        pass = et_pass.getText().toString().trim();
        repit_pass = et_passrepit.getText().toString().trim();
        email = et_email.getText().toString().trim();
        boolean validacion = validar(nombre, pass, repit_pass, email);

        if(validacion){
            ref.child("tienda").child("clientes").orderByChild("nombre").equalTo(nombre)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.hasChildren()){
                        et_nombre.setError("Ese nombre de usuario ya existe");
                    }else{

                        Usuario nuevo = new Usuario(nombre, email, pass);
                        String id = ref.child("tienda").child("clientes").push().getKey();
                        ref.child("tienda").child("clientes").child(id).setValue(nuevo);
                        if(fotoUsuario_url != null){
                            sto.child("tienda").child("usuarios").child(id).putFile(fotoUsuario_url);
                        }

                        LoginActividad loginActividad = (LoginActividad)getActivity();
                        TabLayout.Tab tab = loginActividad.getTabLayout().getTabAt(0);
                        loginActividad.getTabLayout().selectTab(tab);
                        Toast.makeText(getContext(), "Te has registrado correctamente", Toast.LENGTH_SHORT).show();
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

        if(nombre.equals("")){
            et_nombre.setError("El nombre no puede estar vacío");
            validacion = false;
        }

        if(pass.equals("")){
            til_pass.setError("La contraseña no puede estar vacía");
            validacion = false;
        }else{
            AppUtilities utilities = new AppUtilities();
            if(!pass.equals(repit_pass)){
                til_pass.setError("Las contraseñas deben ser iguales");
                validacion = false;
            }else if(!utilities.isValidPass(pass)){
                validacion = false;
                til_pass.setError("Debe contener por lo menos un dígito y tener una longitud entre 5 y 15 caracteres");
            }
        }

        if(email.equals("")){
            et_email.setError("El email es obligatorio");
            validacion = false;
        }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            et_email.setError("No es un email válido");
            validacion = false;
        }

        return validacion;
    }


    public void seleccionarFotoPerfil(View v){
        Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,SELECCIONAR_FOTO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK && requestCode==SELECCIONAR_FOTO){
            fotoUsuario_url=data.getData();
            fotoElegida.setImageURI(fotoUsuario_url);
            Toast.makeText(getContext(), "Foto de perfil seleccionada con éxito", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getContext(), "Fallo al seleccionar la foto de perfil", Toast.LENGTH_SHORT).show();
        }
    }



}