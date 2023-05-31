package com.mobile_genius.easyshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ConfirmarCompra extends AppCompatActivity {

    private EditText nombre,correo,direccion,telefono;
    private Button confirmar;
    private String toalpago = "";
    private FirebaseAuth auth;
    private String CurrentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmar_compra);

        toalpago = getIntent().getStringExtra("total");
        Toast.makeText(this,"Total a pagar : $ "+toalpago,Toast.LENGTH_SHORT).show();

        nombre = (EditText) findViewById(R.id.finalnomb);
        direccion = (EditText) findViewById(R.id.finaldireccion);
        telefono = (EditText) findViewById(R.id.finaltelefono);
        correo = (EditText) findViewById(R.id.finalcorreo);

        auth = FirebaseAuth.getInstance();
        CurrentUserId = auth.getCurrentUser().getUid();

        confirmar = (Button) findViewById(R.id.btnconfirmar);
        confirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verficarDatos();
            }
        });

    }

    private void verficarDatos() {

        if (TextUtils.isEmpty(nombre.getText().toString())){
            Toast.makeText(this,"Porfavor ingerese su nombre",Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(direccion.getText().toString())){
            Toast.makeText(this,"Porfavor ingerese su direccion",Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(telefono.getText().toString())){
            Toast.makeText(this,"Porfavor ingerese su telefono",Toast.LENGTH_SHORT).show();
        } else{
            confirmarorden();
        }
    }

    private void confirmarorden() {
    final String CurrenTime,CurrenDate;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyy");
        CurrenDate = dateFormat.format(calendar.getTime());

        SimpleDateFormat dateFormat1 =  new SimpleDateFormat("HH:mm:ss");
        CurrenTime = dateFormat1.format(calendar.getTime());

        final DatabaseReference OrdenesRef  = FirebaseDatabase.getInstance().getReference().child("Ordenes").child(CurrentUserId);


        HashMap<String,Object> map = new HashMap<>();
        map.put("total",toalpago);
        map.put("nombre",nombre.getText().toString());
        map.put("direccion",direccion.getText().toString());
        map.put("telefono",telefono.getText().toString());
        map.put("correo",correo.getText().toString());
        map.put("fecha",CurrenDate);
        map.put("hora",CurrenTime);
        map.put("estado","No enviado");

        OrdenesRef.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    FirebaseDatabase.getInstance().getReference().child("Carrito").child("Usuario Compra")
                            .child(CurrentUserId).removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(ConfirmarCompra.this,"Tu orden fue tomada con exito",Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(ConfirmarCompra.this, Principal.class);
                                    startActivity(intent);
                                    finish();
                                }
                                }
                            });
                }
            }
        });

    }
}