package com.example.tableverse.admin;

import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tableverse.AdminActividad;
import com.example.tableverse.AppUtilities;
import com.example.tableverse.R;
import com.example.tableverse.dialog.DatePickerFragment;
import com.example.tableverse.objetos.Evento;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.Iterator;

import static android.app.Activity.RESULT_OK;

public class CrearEvento extends Fragment {
    private final int MODO_FAB = 3;
    private final int MODO_NAVVIEW = 2;
    private static final int SELECCIONAR_FOTO = 1;
    private EditText et_nombre, et_precio, et_fecha, et_aforo;
    private TextView error;
    private ImageView fotoEvento;
    private Uri fotoEventoUrl;
    private DatabaseReference ref;
    private StorageReference sto;

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

        return inflater.inflate(R.layout.fragment_crear_evento, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        et_nombre = view.findViewById(R.id.et_evento);
        et_aforo = view.findViewById(R.id.et_aforo);
        et_fecha = view.findViewById(R.id.et_fecha_evento);
        et_precio = view.findViewById(R.id.et_precio);
        fotoEvento = view.findViewById(R.id.iv_evento);
        error = view.findViewById(R.id.tv_error);
        Button addEvent = view.findViewById(R.id.b_add_event);
        fotoEventoUrl = null;

        AdminActividad adminActividad = (AdminActividad)getActivity();
        adminActividad.getToolbar().setNavigationIcon(R.drawable.arrow_back_white_24dp);
        adminActividad.modoFab(MODO_FAB);
        adminActividad.modoNavView(MODO_NAVVIEW);
        ref = adminActividad.getRef();
        sto = adminActividad.getSto();

        addEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                crearEvento(view);
            }
        });
        et_fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });
        fotoEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seleccionarFoto();
            }
        });

    }

    private void showDatePickerDialog() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                String selectedDate = year + "-" + (month+1) + "-" + day;
                et_fecha.setText(selectedDate);
            }
        });

        newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
    }

    public void crearEvento(View v){
        final String nombre, fecha, precio, aforo_maximo;
        error.setVisibility(View.GONE);

        nombre = et_nombre.getText().toString().trim();
        fecha = et_fecha.getText().toString().trim();
        precio = et_precio.getText().toString().trim();
        aforo_maximo = et_aforo.getText().toString().trim();

        if(esValido(nombre, fecha, precio, aforo_maximo)){

            ref.child("tienda").child("eventos").orderByChild("nombre").equalTo(nombre)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.hasChildren()){
                        Evento buscado = null;

                        Iterator<DataSnapshot> iter = snapshot.getChildren().iterator();

                        while (iter.hasNext() && buscado == null){
                            Evento evento = iter.next().getValue(Evento.class);
                            if(evento.getFecha().equals(fecha)){
                                buscado = evento;
                            }
                        }

                        if(buscado != null){
                            Toast.makeText(getContext(), "Ya existe un evento con este nombre y en esta fecha", Toast.LENGTH_SHORT).show();
                        }else{
                            nuevoEvento(nombre, fecha, precio, aforo_maximo);
                        }

                    }else{
                        nuevoEvento(nombre, fecha, precio, aforo_maximo);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }

    private void nuevoEvento(String nombre, String fecha, String precio, String aforo_maximo){
        Evento pojo_evento = new Evento(nombre, fecha, Double.parseDouble(precio),
                Integer.parseInt(aforo_maximo));
        String id = ref.child("tienda").child("eventos").push().getKey();
        ref.child("tienda").child("eventos").child(id).setValue(pojo_evento);
        if(fotoEventoUrl != null){
            sto.child("tienda").child("eventos").child(id).putFile(fotoEventoUrl);
        }
        AdminActividad adminActividad = (AdminActividad)getActivity();
        adminActividad.getNavController().navigate(R.id.listaEventosAdmin);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private boolean esValido(String nombre, String fecha, String precioString, String aforo_maximoStr){
        boolean validado = true;

        if(nombre.equals("")){
            et_nombre.setError("El nombre no puede estar vacío");
            validado = false;
        }

        AppUtilities utilities = new AppUtilities();

        if(!utilities.esPosterior(fecha)){
            error.setText("La fecha no puede ser anterior a hoy");
            error.setVisibility(View.VISIBLE);
            validado = false;
        }else{
            error.setVisibility(View.GONE);
        }

        if(precioString.equals("")){
            et_precio.setError("El precio no puede estar vacío, indica un 0 si quieres que sea gratuito");
            validado = false;
        }

        if(aforo_maximoStr.equals("")){
            et_aforo.setError("El aforo máximo no puede estar vacío");
            validado = false;
        }else{
            if(!utilities.esAforoValido(aforo_maximoStr)){
                et_aforo.setError("El aforo no puede ser 0 o inferior a 0");
                validado = false;
            }
        }

        return validado;
    }

    public void seleccionarFoto(){
        Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,SELECCIONAR_FOTO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK && requestCode==SELECCIONAR_FOTO){
            fotoEventoUrl=data.getData();
            fotoEvento.setImageURI(fotoEventoUrl);
            fotoEvento.setImageTintMode(null);
            Toast.makeText(getContext(), "Foto de perfil seleccionada con éxito", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getContext(), "Fallo al seleccionar la foto de perfil", Toast.LENGTH_SHORT).show();
        }
    }

}