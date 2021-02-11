package com.example.tableverse.usuario;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.tableverse.R;
import com.example.tableverse.UsuarioActividad;
import com.example.tableverse.objetos.Usuario;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;


public class ConfiguracionUsuario extends Fragment {

    private DatabaseReference ref;
    private Spinner spi_divisas;
    private Switch sw_oscuro;
    private TextView tv_nombre, tv_email, tv_divisa;
    private ImageView iv_foto;
    private Usuario usuario;
    private UsuarioActividad usuarioActividad;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public ConfiguracionUsuario() {
        // Required empty public constructor
    }

    public static ConfiguracionUsuario newInstance(String param1, String param2) {
        ConfiguracionUsuario fragment = new ConfiguracionUsuario();
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
        return inflater.inflate(R.layout.fragment_configuracion_usuario, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        spi_divisas = view.findViewById(R.id.spi_divisas);
        sw_oscuro = view.findViewById(R.id.sw_modo_noche);
        tv_nombre = view.findViewById(R.id.tv_config_usuario);
        tv_email = view.findViewById(R.id.tv_email);
        tv_divisa = view.findViewById(R.id.tv_divisa);
        iv_foto = view.findViewById(R.id.iv_configuracion);

        usuarioActividad = (UsuarioActividad) getActivity();
        ref = usuarioActividad.getRef();
        usuario = usuarioActividad.getUsuario();
        editor = usuarioActividad.getEditor();
        sp = usuarioActividad.getSp();

        String[] tipo_divisa = {"Euros €", "Dólares $", "Libras Esterlinas £"};
        ArrayAdapter<String> spiAdaptador = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, tipo_divisa);
        spiAdaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spi_divisas.setAdapter(spiAdaptador);
        spi_divisas.setSelection(sp.getInt("pos",  -1) + 1);

        CargarImagen ci = new CargarImagen(usuarioActividad.getSto());
        ci.start();
        setView();

        spi_divisas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                tv_divisa.setText(Character.toString(usuarioActividad.getDivisas()[i]));
                usuarioActividad.setPos_ratio_elegido(i - 1);
                editor.putInt("pos", i-1);
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setView(){
        tv_nombre.setText(usuario.getNombre());
        tv_email.setText(usuario.getCorreo());
        tv_divisa.setText("€");

        Glide.with(this).load(usuario.getUrl_imagen())
                .placeholder(android.R.drawable.progress_indeterminate_horizontal)
                .error(R.drawable.person_morada).into(iv_foto);
    }


    private class CargarImagen extends Thread{
        private StorageReference sto;

        public CargarImagen(StorageReference sto) {
            this.sto = sto;

        }

        @Override
        public void run(){
            sto.child("tienda").child("usuarios").child(usuario.getId()).getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        usuario.setUrl_imagen(uri.toString());
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setView();
                            }
                        });
                    }
                });
        }
    }
}
