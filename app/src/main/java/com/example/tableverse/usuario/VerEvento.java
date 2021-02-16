package com.example.tableverse.usuario;

import android.graphics.Color;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.example.tableverse.AppUtilities;
import com.example.tableverse.R;
import com.example.tableverse.UsuarioActividad;
import com.example.tableverse.objetos.Evento;
import com.example.tableverse.objetos.ReservaEvento;
import com.example.tableverse.objetos.Usuario;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;


public class VerEvento extends Fragment {

    private ImageView fotoEvento;
    private TextView tv_nombre, tv_precio, tv_fecha, tv_ocupadas, tv_libres;
    private Button b_apuntarse;
    private Evento evento;
    private DatabaseReference ref;
    private UsuarioActividad usuarioActividad;


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
        b_apuntarse = view.findViewById(R.id.b_apuntarse);

        usuarioActividad = (UsuarioActividad)getActivity();
        evento = usuarioActividad.getLista_eventos().get(usuarioActividad.getPosition());
        usuarioActividad.setVisibilitySearchView(0);
        ref = usuarioActividad.getRef();
        setView();
        PieChart pc = view.findViewById(R.id.pieChart);
        setPieChart(pc);

        b_apuntarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                apuntarseEvento();
            }
        });
    }

    private void setView(){
        tv_nombre.setText(evento.getNombre());
        AppUtilities utilities = new AppUtilities();

        Calendar fechaEvento = utilities.transfomarStringACalendar(evento.getFecha());

        if(utilities.esElMismoDia(fechaEvento)){
            tv_fecha.setText("El evento es hoy mismo! No pierdas la oportunidad de apuntarte!");
        }else{
            tv_fecha.setText(evento.getFecha());
        }

        setTvPrecio();
        tv_libres.setText("Plazas libres: " + (evento.getAforoMax() - evento.getOcupado()));
       /* tv_ocupadas.setText("Plazas ocupadas: " + evento.getOcupado());*/
        tv_ocupadas.setVisibility(View.INVISIBLE);
        StorageReference sto = FirebaseStorage.getInstance().getReference();
        sto.child("tienda").child("eventos").child(evento.getId()).getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(getContext()).load(evento.getUrlImagen())
                                .placeholder(android.R.drawable.progress_indeterminate_horizontal)
                                .error(android.R.drawable.stat_notify_error).into(fotoEvento);
                        fotoEvento.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    }
                });
    }

    private void setTvPrecio(){
        if(evento.getPrecio() == 0){
            tv_precio.setText("La entrada es gratuita. ");
        }else{
            if(usuarioActividad.getPos_ratio_elegido() == -1){
                tv_precio.setText("Precio de la entrada: " + evento.getPrecio()
                        + usuarioActividad.getDivisas()[usuarioActividad.getPos_ratio_elegido()+1]);
            }else{
                DecimalFormat df = new DecimalFormat("#.##");
                tv_precio.setText("Precio de la entrada: " + df.format(evento.getPrecio() *
                        usuarioActividad.getRatios()[usuarioActividad.getPos_ratio_elegido()])
                        + usuarioActividad.getDivisas()[usuarioActividad.getPos_ratio_elegido()+1]);
            }

        }
    }

    private void setPieChart(PieChart pc){
        PieData pd;
        ArrayList<PieEntry> pieList = new ArrayList<>();
        PieDataSet pieDataSet;

        pc.setUsePercentValues(true);

        pieList.add(new PieEntry(evento.getOcupado(),"Apuntados"));
        pieList.add(new PieEntry(evento.getAforoMax() - evento.getOcupado(), "libres"));
        pieDataSet = new PieDataSet(pieList, "");
        pd = new PieData(pieDataSet);
        pc.setData(pd);

        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pc.getDescription().setText("");
        Legend l = pc.getLegend();
        if(usuarioActividad.isTema()){
            l.setTextColor(Color.WHITE);
        }
        l.setTextSize(18f);
        pd.setValueTextSize(18f);
    }

    public void apuntarseEvento(){
        final Usuario usuario = usuarioActividad.getUsuario();
        ref.child("tienda").child("reservas_eventos").orderByChild("id_cliente")
                .equalTo(usuario.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReservaEvento reservaExiste = null;
                Iterator<DataSnapshot> iterator = snapshot.getChildren().iterator();

                while (iterator.hasNext() && reservaExiste == null){
                    ReservaEvento re = iterator.next().getValue(ReservaEvento.class);
                    if(re.getId_cliente().equals(usuario.getId()) &&
                            re.getId_evento().equals(evento.getId())){
                        reservaExiste = re;
                    }
                }

                if(reservaExiste != null){
                    Toast.makeText(getContext(), "Ya estás apuntado a este evento", Toast.LENGTH_SHORT).show();
                }else{
                    ReservaEvento reserva = new ReservaEvento(evento.getId(), usuario.getId());
                    String id = ref.child("tienda").child("reservas_eventos").push().getKey();
                    ref.child("tienda").child("reservas_eventos").child(id).setValue(reserva);
                    evento.setOcupado(evento.getOcupado() + 1);
                    ref.child("tienda").child("eventos").child(evento.getId()).setValue(evento);

                    Toast.makeText(getContext(), "Has reservado una entrada para este evento! Esperemos que lo pases bien!", Toast.LENGTH_SHORT).show();
                    usuarioActividad.getNavController().navigate(R.id.eventosApuntado);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}