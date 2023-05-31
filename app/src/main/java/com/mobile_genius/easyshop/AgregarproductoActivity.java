package com.mobile_genius.easyshop;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AgregarproductoActivity extends AppCompatActivity {
    private ImageView imagenpro;
    private EditText nombrepro, descripcionpro,preciocomprapro,precioventapro,cantidadpro;
    private TextView textox;
    private Button agregarpro;
    private static final int Gallery_Pick = 1;
    private Uri imagenUri;
    private String productoRandonKey, downloadUri;
    private StorageReference ProductoImagenRef;
    private DatabaseReference ProductoRef;
    private ProgressDialog dialog;
    private String Categoria, Nom, Desc, PreCom,PreVen,Cant,CurrentDate, CurrentTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregarproducto);

        Categoria = getIntent().getExtras().get("categoria").toString();
        ProductoImagenRef = FirebaseStorage.getInstance().getReference().child("Imagenes Productos");
        ProductoRef = FirebaseDatabase.getInstance().getReference().child("Productos");

        Toast.makeText(this, Categoria, Toast.LENGTH_SHORT).show();

        textox = (TextView) findViewById(R.id.textox);
        imagenpro = (ImageView) findViewById(R.id.imagenpro);
        nombrepro = (EditText) findViewById(R.id.nombrepro);
        descripcionpro = (EditText) findViewById(R.id.descripcionpro);
        preciocomprapro = (EditText) findViewById(R.id.preciocomprapro);
        precioventapro = (EditText) findViewById(R.id.precioventapro);
        cantidadpro = (EditText) findViewById(R.id.cantidadpro);
        agregarpro = (Button) findViewById(R.id.agregarpro);

        dialog = new ProgressDialog(this);
        
        imagenpro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AbrirGaleria();
            }
        });
        agregarpro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidarProducto();
            }
        });
        textox.setText(Categoria+"\n Agregar Producto");
    }


    private void AbrirGaleria() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/");
        startActivityForResult(intent,Gallery_Pick );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==Gallery_Pick && resultCode==RESULT_OK && data != null){
            imagenUri=data.getData();
            imagenpro.setImageURI(imagenUri);
        }
    }

    private void ValidarProducto() {
        Nom = nombrepro.getText().toString();
        Desc = descripcionpro.getText().toString();
        PreCom = preciocomprapro.getText().toString();
        PreVen = precioventapro.getText().toString();
        Cant = cantidadpro.getText().toString();

        if (imagenUri ==null){
            Toast.makeText(this, "Primero agregar una imagen", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(Nom)){
            Toast.makeText(this, "Debes ingresar el nombre del producto", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(Desc)){
            Toast.makeText(this, "Debes ingresar la descripcion del producto", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(PreCom)){
            Toast.makeText(this, "Debes ingresar el precio de compra del producto", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(PreVen)){
            Toast.makeText(this, "Debes ingresar el precio de venta del producto", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(Cant)){
            Toast.makeText(this, "Debes ingresar la cantidad de productos", Toast.LENGTH_SHORT).show();
        }else {
            GuardarInformacionProducto();
        }
    }

    private void GuardarInformacionProducto() {

        dialog.setTitle("Guardar Producto");
        dialog.setMessage("Por favor espere mientras guardamos el producto");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat curreDateFormat = new SimpleDateFormat("MM-dd-yyyy");
        CurrentDate = curreDateFormat.format(calendar.getTime());

        SimpleDateFormat CurrentTimeFormat = new SimpleDateFormat("HH:mm:ss");
        CurrentTime = CurrentTimeFormat.format(calendar.getTime());

        productoRandonKey = CurrentDate + CurrentTime;

        final StorageReference filePath = ProductoImagenRef.child(imagenUri.getLastPathSegment()+ productoRandonKey+ ".jpg");
        final UploadTask uploadTask = filePath.putFile(imagenUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String mensaje = e.toString();
                Toast.makeText(AgregarproductoActivity.this, "Error: "+mensaje, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AgregarproductoActivity.this, "Imagen guardada correctamente", Toast.LENGTH_SHORT).show();

                Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()){
                            throw task.getException();
                        }
                        downloadUri = filePath.getDownloadUrl().toString();
                        return  filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()){
                            downloadUri = task.getResult().toString();
                            Toast.makeText(AgregarproductoActivity.this, "Imagen guardada en Firebase", Toast.LENGTH_SHORT).show();
                            GuardarEnFirebase();
                        }else{
                            Toast.makeText(AgregarproductoActivity.this, "Error....", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }

    private void GuardarEnFirebase() {

        HashMap<String,Object>map = new HashMap<>();
        map.put("pid", productoRandonKey);
        map.put("fecha",CurrentDate);
        map.put("hora",CurrentTime);
        map.put("descripcion",Desc);
        map.put("nombre",Nom);
        map.put("Preciocom",PreCom);
        map.put("Precioven",PreVen);
        map.put("Cant",Cant);
        map.put("imagen",downloadUri);
        map.put("categoria",Categoria);

        ProductoRef.child(productoRandonKey).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Intent intent = new Intent(AgregarproductoActivity.this,AdminActivity.class);
                    startActivity(intent);
                    dialog.dismiss();
                    Toast.makeText(AgregarproductoActivity.this, "Solicitud Exitosa", Toast.LENGTH_SHORT).show();
                }else{
                    dialog.dismiss();
                    String mensaje = task.getException().toString();
                    Toast.makeText(AgregarproductoActivity.this, "Error: "+mensaje, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}