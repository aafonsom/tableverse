package com.example.tableverse.dialog;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tableverse.AdminActividad;
import com.example.tableverse.AppUtilities;
import com.example.tableverse.R;
import com.example.tableverse.objetos.Evento;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

public class DialogModEvento extends DialogFragment {
    private AdminActividad adminActividad;
    private Evento evento;
    private EditText et_nombre, et_precio, et_fecha, et_aforo;
    private TextView error;
    private ImageView iv_foto;
    private Button modificar;
    private Uri foto_url;
    private StorageReference sto;
    private DatabaseReference ref;

    public DialogModEvento(){

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return crearDialogModJuego();
    }

    private AlertDialog crearDialogModJuego() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_dialog_mod_evento, null);
        builder.setView(v);

        et_nombre = v.findViewById(R.id.et_nombre);
        et_aforo = v.findViewById(R.id.et_aforo);
        et_precio = v.findViewById(R.id.et_precio);
        et_fecha = v.findViewById(R.id.et_fecha);
        iv_foto = v.findViewById(R.id.iv_foto_evento);
        modificar = v.findViewById(R.id.b_modificar);
        error = v.findViewById(R.id.tv_error);

        sto = adminActividad.getSto();
        ref = adminActividad.getRef();

        int pos = adminActividad.getPosition();
        evento = adminActividad.getLista_eventos().get(pos);
        foto_url = null;

        setView();

        modificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modificarOnClick();
            }
        });
        et_fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });
        return builder.create();

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof AdminActividad){
            this.adminActividad = (AdminActividad) context;
        }
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

    private void setView(){
        et_nombre.setText(evento.getNombre());
        et_aforo.setText(Integer.toString(evento.getAforoMax()));
        et_precio.setText(Double.toString(evento.getPrecio()));
        et_fecha.setText(evento.getFecha());

        sto.child("tienda").child("juegos").child(evento.getId())
                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getContext()).load(evento.getUrlImagen())
                        .placeholder(android.R.drawable.progress_indeterminate_horizontal)
                        .error(android.R.drawable.stat_notify_error).into(iv_foto);
            }
        });

    }

    private void modificarOnClick(){
        final String nombre, fecha, precio, aforo;

        nombre = et_nombre.getText().toString().trim();
        fecha = et_fecha.getText().toString().trim();
        precio = et_precio.getText().toString().trim();
        aforo =et_aforo.getText().toString().trim();

        if(validar(nombre, fecha, precio, aforo)){
            if(nombre.equals(evento.getNombre())){
                realizarModificaciones(nombre, fecha, precio, aforo);
            }else{
                ref.child("tienda").child("eventos").orderByChild("nombre").equalTo(nombre)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.hasChildren()){
                                    Toast.makeText(adminActividad, "Ya existe un juego con ese nombre", Toast.LENGTH_SHORT).show();
                                }else{
                                    realizarModificaciones(nombre, fecha, precio, aforo);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }
        }
    }

    private void realizarModificaciones(String nombre, String fecha, String precio, String aforo) {
        evento.setNombre(nombre);
        evento.setFecha(fecha);
        evento.setPrecio(Double.parseDouble(precio));
        evento.setAforoMax(Integer.parseInt(aforo));

        ref.child("tienda").child("eventos").child(evento.getId()).setValue(evento);
        if(foto_url != null){
            sto.child("tienda").child("eventos").child(evento.getId()).putFile(foto_url);
        }
        Toast.makeText(getContext(), "Datos modificados con éxito", Toast.LENGTH_SHORT).show();

        dismiss();

    }

    private boolean hayCambios(String nombre, String fecha, double precio, int aforo){
        boolean cambios = true;

        if(nombre.equals(evento.getNombre()) && fecha.equals(evento.getFecha())
                && precio == evento.getPrecio() && aforo == evento.getAforoMax()){
            Toast.makeText(getContext(), "No se ha realizado ningun cambio", Toast.LENGTH_SHORT).show();
            cambios = false;
        }

        return cambios;
    }

    private boolean validar(String nombre, String fecha, String precio, String aforo){
        boolean esValido = true;
        double precioDouble = -1;
        int aforoInt;

        try{
            precioDouble = Double.parseDouble(precio);
        }catch (NumberFormatException nfe){
            esValido = false;
            et_precio.setError("El precio no pueden estar vacíos. ");
        }

        try{
            aforoInt = Integer.parseInt(aforo);
            esValido = hayCambios(nombre, fecha, precioDouble, aforoInt);
        }catch (NumberFormatException nfe){
            esValido = false;
            et_aforo.setError("El aforo no puede estar vacío");
        }


        if(esValido){
            if(nombre.equals("")){
                et_nombre.setError("El nombre no puede estar vacío");
                esValido = false;
            }

            AppUtilities utilities = new AppUtilities();

            if(!utilities.esPosterior(fecha)){
                error.setText("La fecha no puede ser anterior a hoy");
                error.setVisibility(View.VISIBLE);
                esValido = false;
            }else{
                error.setVisibility(View.GONE);
            }

            if(precio.equals("")){
                et_precio.setError("El precio no puede estar vacío, indica un 0 si quieres que sea gratuito");
                esValido = false;
            }

            if(aforo.equals("")){
                et_aforo.setError("El aforo máximo no puede estar vacío");
                esValido = false;
            }else{
                if(!utilities.esAforoValido(aforo)){
                    et_aforo.setError("El aforo no puede ser 0 o inferior a 0");
                    esValido = false;
                }
            }
        }

        return esValido;
    }


}