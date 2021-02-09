package com.example.tableverse.usuario;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.tableverse.R;
import com.example.tableverse.UsuarioActividad;
import com.example.tableverse.objetos.Evento;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


public class VerEvento extends Fragment {

    private ImageView fotoEvento;
    private TextView tv_nombre, tv_precio, tv_fecha, tv_ocupadas, tv_libres;
    private Button b_apuntarse;
    private Evento evento;


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public VerEvento() {

    }


    public static VerEvento newInstance(String param1, String param2) {
        VerEvento fragment = new VerEvento();
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
        return inflater.inflate(R.layout.fragment_ver_evento, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tv_nombre = view.findViewById(R.id.tv_titulo_evento);
        tv_fecha = view.findViewById(R.id.tv_fecha_evento);
        tv_precio = view.findViewById(R.id.tv_precio);
        tv_ocupadas = view.findViewById(R.id.tv_plazas_ocupadas);
        tv_libres = view.findViewById(R.id.tv_plazas_libres);
        fotoEvento = view.findViewById(R.id.iv_evento);

        UsuarioActividad usuarioActividad = (UsuarioActividad)getActivity();
        evento = usuarioActividad.getLista_eventos().get(usuarioActividad.getPosition());

        setView();
        PieChart pc = view.findViewById(R.id.pieChart);
        setPieChart(pc);
    }

    private void setView(){
        tv_nombre.setText(evento.getNombre());
        tv_fecha.setText(evento.getFecha());
        tv_precio.setText("Precio: " + evento.getPrecio());
        tv_libres.setText("Plazas libres: " + (evento.getAforoMax() - evento.getOcupado()));
        tv_ocupadas.setText("Plazas ocupadas: " + evento.getOcupado());
        CargarImagen ci = new CargarImagen(evento);
        ci.start();
    }

    private void setPieChart(PieChart pc){
        PieData pd;
        ArrayList<PieEntry> pieList = new ArrayList<>();
        PieDataSet pieDataSet;

        pc.setUsePercentValues(true);
        /*double ocupado = (double)evento.getOcupado() / (double)evento.getAforoMax() * 100;
        double libre = (double)evento.getAforoMax() - (double)evento.getOcupado() / (double)evento.getAforoMax();*/

        pieList.add(new PieEntry(evento.getOcupado(),"Apuntados"));
        pieList.add(new PieEntry(evento.getAforoMax() - evento.getOcupado(), "Plazas restantes"));
        pieDataSet = new PieDataSet(pieList, "");
        pd = new PieData(pieDataSet);
        pc.setData(pd);


        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        pc.getDescription().setText("");
        Legend l = pc.getLegend();
        l.setTextSize(18f);
        pd.setValueTextSize(18f);
    }

    private class CargarImagen extends Thread{
        Evento pojo_evento;

        public CargarImagen(Evento pojo_evento) {
            this.pojo_evento = pojo_evento;
        }

        @Override
        public void run(){
            StorageReference sto = FirebaseStorage.getInstance().getReference();
            sto.child("tienda").child("eventos").child(pojo_evento.getId()).getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide.with(getContext()).load(evento.getUrlImagen())
                                    .placeholder(android.R.drawable.progress_indeterminate_horizontal)
                                    .error(android.R.drawable.stat_notify_error).into(fotoEvento);
                        }
                    });
        }
    }


}