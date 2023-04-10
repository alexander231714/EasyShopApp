package com.mobile_genius.easyshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.content.Intent;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import java.util.concurrent.TimeUnit;

public class Login_admin extends AppCompatActivity {

    private EditText numeroAdmin, codigoAdmin;
    private Button enviarNumAdmin, enviarCodAdmin;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private String phoneNumber, vericationID;
    private PhoneAuthProvider.ForceResendingToken resendingToken;
    private FirebaseAuth auth;
    private ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login_admin);

        numeroAdmin=findViewById(R.id.txtIngresarNumAdmin);
        codigoAdmin=findViewById(R.id.txtCodigoAdmin);
        enviarCodAdmin=findViewById(R.id.btnenviarCodigoAdmin);
        enviarNumAdmin=findViewById(R.id.btnenviarnumeroAdmin);

        auth=FirebaseAuth.getInstance();
        dialog=new ProgressDialog(this);

        enviarNumAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumber = numeroAdmin.getText().toString();
                if (TextUtils.isEmpty(phoneNumber)){
                    Toast.makeText(Login_admin.this, ""+R.string.ingresar_num, Toast.LENGTH_SHORT).show();
                }else {
                    dialog.setTitle(R.string.validandoNum);
                    dialog.setMessage(""+R.string.esperaraVali_num);
                    dialog.show();
                    dialog.setCanceledOnTouchOutside(true);

                    PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth)
                            .setPhoneNumber(phoneNumber)
                            .setTimeout(60L, TimeUnit.SECONDS)
                            .setActivity(Login_admin.this)
                            .setCallbacks(callbacks).build();

                    PhoneAuthProvider.verifyPhoneNumber(options);//envia el numero

                }
            }
        });

        enviarCodAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numeroAdmin.setVisibility(View.GONE);
                enviarNumAdmin.setVisibility(View.GONE);
                String verificationCode = codigoAdmin.getText().toString();
                if (TextUtils.isEmpty(verificationCode)){
                    Toast.makeText(Login_admin.this, "Escriba el código recibido", Toast.LENGTH_SHORT).show();
                }else{
                    dialog.setTitle("Verificando");
                    dialog.setMessage("Por favor, espere...");
                    dialog.show();
                    dialog.setCanceledOnTouchOutside(true);

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(vericationID, verificationCode);
                    IngresadoConExito(credential);

                }
            }
        });

        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                IngresadoConExito(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                dialog.dismiss();
                Toast.makeText(Login_admin.this, "No se ha podido iniciar.\nCausas: \n1. Número invalido.\n2. Sin conexión a internet ", Toast.LENGTH_SHORT).show();
                numeroAdmin.setVisibility(View.VISIBLE);
                enviarNumAdmin.setVisibility(View.VISIBLE);
                codigoAdmin.setVisibility(View.GONE);
                enviarCodAdmin.setVisibility(View.GONE);
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                vericationID = s;
                resendingToken=token;
                dialog.dismiss();
                Toast.makeText(Login_admin.this, "Código enviado satisfactoriamente. \nComprueba tu bandeja de entrada.", Toast.LENGTH_SHORT).show();
                numeroAdmin.setVisibility(View.GONE);
                enviarNumAdmin.setVisibility(View.GONE);
                codigoAdmin.setVisibility(View.VISIBLE);
                enviarCodAdmin.setVisibility(View.VISIBLE);
            }
        };
    }


    private void IngresadoConExito(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    dialog.dismiss();
                    Toast.makeText(Login_admin.this, "Admitido con éxito", Toast.LENGTH_SHORT).show();
                    EnviarlaPrincipal();
                }else{
                    String err = task.getException().toString();
                    Toast.makeText(Login_admin.this, "Error: "+ err, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = auth.getCurrentUser();
        if(firebaseUser != null){
            EnviarlaPrincipal();
        }
    }

    private void EnviarlaPrincipal() {
        Intent intent = new Intent(Login_admin.this, Principal.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("papel","admin" );
        intent.putExtra("phone", phoneNumber);
        startActivity(intent);
        finish();
    }
}