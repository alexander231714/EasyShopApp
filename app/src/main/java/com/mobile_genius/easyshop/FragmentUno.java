package com.mobile_genius.easyshop;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


public class FragmentUno extends Fragment {
    private View fragmento;

    private ImageView camisas, deporte, joyas, audifonos;
    private ImageView computadora, juegos, libros, televisiones;
    private ImageView musica, licores, camaras, lentes;

    public FragmentUno() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragmento = inflater.inflate(R.layout.fragment_uno, container, false);

        camisas = (ImageView) fragmento.findViewById(R.id.camisetas);
        deporte = (ImageView) fragmento.findViewById(R.id.deporte);
        joyas = (ImageView) fragmento.findViewById(R.id.joyas);
        audifonos = (ImageView) fragmento.findViewById(R.id.audifonos);

        computadora = (ImageView) fragmento.findViewById(R.id.computadoras);
        juegos = (ImageView) fragmento.findViewById(R.id.juegos);
        libros = (ImageView) fragmento.findViewById(R.id.libros);
        televisiones = (ImageView) fragmento.findViewById(R.id.televisiones);

        musica = (ImageView) fragmento.findViewById(R.id.musica);
        licores = (ImageView) fragmento.findViewById(R.id.licores);
        camaras = (ImageView) fragmento.findViewById(R.id.camaras);
        lentes = (ImageView) fragmento.findViewById(R.id.lentes);

        camisas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        return fragmento;
    }
}