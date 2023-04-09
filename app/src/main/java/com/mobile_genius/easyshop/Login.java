package com.mobile_genius.easyshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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


public class Login extends AppCompatActivity {

    private EditText numero, codigo;
    private Button enviarNum, enviarCod;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private String phoneNumber, vericationID;
    private PhoneAuthProvider.ForceResendingToken resendingToken;
    private FirebaseAuth auth;
    private ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login);

        numero=findViewById(R.id.txtIngresarNum);
        codigo=findViewById(R.id.txtCodigo);
        enviarCod=findViewById(R.id.btnenviarCodigo);
        enviarNum=findViewById(R.id.btnenviarnumero);

        auth=FirebaseAuth.getInstance();
        dialog=new ProgressDialog(this);

        enviarNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumber = numero.getText().toString();
                if (TextUtils.isEmpty(phoneNumber)){
                    Toast.makeText(Login.this, ""+R.string.ingresar_num, Toast.LENGTH_SHORT).show();
                }else {
                    dialog.setTitle(R.string.validandoNum);
                    dialog.setMessage(""+R.string.esperaraVali_num);
                    dialog.show();
                    dialog.setCanceledOnTouchOutside(true);

                    PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth)
                            .setPhoneNumber(phoneNumber)
                            .setTimeout(60L, TimeUnit.SECONDS)
                            .setActivity(Login.this)
                            .setCallbacks(callbacks).build();

                    PhoneAuthProvider.verifyPhoneNumber(options);//envia el numero

                }

            }
        });

        enviarCod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numero.setVisibility(View.GONE);
                enviarNum.setVisibility(View.GONE);
                String verificationCode = codigo.getText().toString();
                if (TextUtils.isEmpty(verificationCode)){
                    Toast.makeText(Login.this, "Escriba el código recibido", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(Login.this, "No se ha podido iniciar.\nCausas: \n1. Número invalido.\n2. Sin conexión a internet ", Toast.LENGTH_SHORT).show();
                numero.setVisibility(View.VISIBLE);
                enviarNum.setVisibility(View.VISIBLE);
                codigo.setVisibility(View.GONE);
                enviarCod.setVisibility(View.GONE);
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                vericationID = s;
                resendingToken=token;
                dialog.dismiss();
                Toast.makeText(Login.this, "Código enviado satisfactoriamente. \nComprueba tu bandeja de entrada.", Toast.LENGTH_SHORT).show();
                numero.setVisibility(View.GONE);
                enviarNum.setVisibility(View.GONE);
                codigo.setVisibility(View.VISIBLE);
                enviarCod.setVisibility(View.VISIBLE);
            }
        };
    }

    private void IngresadoConExito(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    dialog.dismiss();
                    Toast.makeText(Login.this, "Admitido con éxito", Toast.LENGTH_SHORT).show();
                    EnviarlaPrincipal();
                }else{
                    String err = task.getException().toString();
                    Toast.makeText(Login.this, "Error: "+ err, Toast.LENGTH_SHORT).show();
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
        Intent intent = new Intent(Login.this, Principal.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("phone", phoneNumber);
        startActivity(intent);
        finish();
    }

}