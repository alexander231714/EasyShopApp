package com.mobile_genius.easyshop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class Setup extends AppCompatActivity {

    private EditText nombre, direccion, telefono, ciudad;
    private Button guardar;
    private String phone = "";
    private CircleImageView imagen;
    private FirebaseAuth auth;
    private DatabaseReference UserRef;
    private ProgressDialog dialog;
    private String CurrentUserId;
    private static int Galery_Pick = 1;
    private StorageReference UserImagenPerfil;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_setup);

        auth = FirebaseAuth.getInstance();
        CurrentUserId = auth.getCurrentUser().getUid();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Usuarios");
        dialog = new ProgressDialog(this);
        UserImagenPerfil = FirebaseStorage.getInstance().getReference().child("Perfil");
        nombre = (EditText) findViewById(R.id.txt_setup_nombre);
        direccion = (EditText) findViewById(R.id.txt_setup_direccion);
        telefono = (EditText) findViewById(R.id.txt_setup_telefono);
        ciudad = (EditText) findViewById(R.id.txt_setup_ciudad);
        guardar = (Button) findViewById(R.id.btn_setup);
        imagen = (CircleImageView) findViewById(R.id.setup_imagen);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            phone = bundle.getString("phone");
        }

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GuardarInformacion();
            }
        });
        imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, Galery_Pick);
            }
        });

        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){

                    if (snapshot.hasChild("imagen")){
                        String imagestr = snapshot.child("imagen").getValue().toString();
                        Picasso.get().load(imagestr).placeholder(R.drawable.logo_transparent).into(imagen);
                    }else{
                        Toast.makeText(Setup.this, "Por favor seleccione una imagen de perfil....", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


    }//Oncreate********


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Galery_Pick && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();


            //Para recortar imagen
            CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON)
            .setAspectRatio(1,1).start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                dialog.setTitle("Imagen de perfil");
                dialog.setMessage("Por favor espere");
                dialog.show();
                dialog.setCanceledOnTouchOutside(true);

                final Uri resultUri = result.getUri();
                StorageReference filePath = UserImagenPerfil.child(CurrentUserId + ".jpg");
                final File url = new File(resultUri.getPath());
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            UserImagenPerfil.child(CurrentUserId + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final String downloadUri = uri.toString();
                                    UserRef.child(CurrentUserId).child("imagen").setValue(downloadUri).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {
                                                Picasso.get().load(downloadUri).into(imagen);
                                                dialog.dismiss();
                                            } else {
                                                String mensaje = task.getException().getMessage();
                                                Toast.makeText(Setup.this, "Error: " + mensaje, Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Imagen no soportada", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        }
    }

    private void GuardarInformacion() {
        String nombres = nombre.getText().toString().toUpperCase();
        String direcciones = direccion.getText().toString();
        String ciudades = ciudad.getText().toString();
        String phones = telefono.getText().toString();

        if (TextUtils.isEmpty(nombres)){
            Toast.makeText(this, "Ingrese el nombre", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(direcciones)){
            Toast.makeText(this, "Ingrese la direccion", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(ciudades)){
            Toast.makeText(this, "Ingrese la ciudad", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(phones)){
            Toast.makeText(this, "Ingrese su telefono", Toast.LENGTH_SHORT).show();
        }else{
            dialog.setTitle("Guardando");
            dialog.setMessage("Por favor espere...");
            dialog.show();
            dialog.setCanceledOnTouchOutside(true);

            HashMap map = new HashMap();
            map.put("nombre", nombres);
            map.put("direccion", direcciones);
            map.put("telefono", phones);
            map.put("ciudad", ciudades);
            map.put("uid", CurrentUserId);
            
            UserRef.child(CurrentUserId).updateChildren(map).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()){
                        EnviarAlInicio();
                        dialog.dismiss();
                    }else{
                        String mensaje = task.getException().toString();
                        Toast.makeText(Setup.this, "Error: "+mensaje, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void EnviarAlInicio() {
        Intent intent = new Intent(Setup.this, Principal.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}