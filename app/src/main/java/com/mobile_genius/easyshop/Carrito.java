package com.mobile_genius.easyshop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Carrito extends AppCompatActivity {
private RecyclerView recyclerView;
private RecyclerView.LayoutManager lytmang;
private Button btnsig;
private TextView total, mnsj;
private double precTot = 0.0 ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrito);

        recyclerView=(RecyclerView)findViewById(R.id.list);
        recyclerView.setHasFixedSize(true);
        lytmang= new LinearLayoutManager(this);
        recyclerView.setLayoutManager(lytmang);
        btnsig = (Button)findViewById(R.id.btntran);
        total =(TextView)findViewById(R.id.precitot);
        mnsj = (TextView)findViewById(R.id.msj);

        btnsig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Carrito.this,ConfirmarCompra.class);
                intent.putExtra("total", String.valueOf(precTot));
                startActivity(intent);
                finish();
            }
        });

    }
}