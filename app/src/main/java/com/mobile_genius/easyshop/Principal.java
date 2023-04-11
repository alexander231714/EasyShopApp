package com.mobile_genius.easyshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class Principal extends AppCompatActivity {

    private FirebaseAuth auth;
    private String CurrentUserId;
    private DatabaseReference UserRef;
    private String Telefono = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_principal);

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            Telefono = bundle.getString("phone");
        }

        auth = FirebaseAuth.getInstance();
        CurrentUserId = auth.getCurrentUser().getUid();

        UserRef = FirebaseDatabase.getInstance().getReference().child("Usurios");
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = auth.getCurrentUser();
        if (firebaseUser == null){
            EnviarAlLogin();
        }else{
            VerificarUsuarioExistente();
        }
    }

    private void VerificarUsuarioExistente() {
        final String CurrentUserId = auth.getCurrentUser().getUid();
        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               if(!snapshot.hasChild(CurrentUserId)){
                   EnviarAlSetup();
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void EnviarAlSetup() {
        Intent intent = new Intent(Principal.this, Setup.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("phone", Telefono);
        startActivity(intent);
        finish();
    }

    private void EnviarAlLogin() {
        Intent intent = new Intent(Principal.this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}