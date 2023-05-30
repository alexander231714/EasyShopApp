package com.mobile_genius.easyshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;


public class Principal extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

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

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,drawerLayout,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);

        TextView nombreHeader = (TextView) headerView.findViewById(R.id.nombreUsuario);
        CircleImageView imagenHeader = (CircleImageView) headerView.findViewById(R.id.usuario);
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

    @Override
    public void onBackPressed(){
       DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
       if (drawerLayout.isDrawerOpen(GravityCompat.START)){
           drawerLayout.closeDrawer(GravityCompat.START);
       }else{
           super.onBackPressed();
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.principal_drawer,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.carrito){
            ActivityCarrito();
        }
        else if (id == R.id.categoria){
            ActivityCategoria();
        }
        else if (id == R.id.busqueda){
            ActivityBuscar();
        }
        else if (id == R.id.perfilUsuario){
            ActivityPerfil();
        }
        else if (id == R.id.salir){
            auth.signOut();
            EnviarAlLogin();
        }

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void ActivityPerfil() {
        Toast.makeText(this, "Perfil", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Principal.this, PerfilActivity.class);
        startActivity(intent);
    }

    private void ActivityBuscar() {
        Toast.makeText(this, "Buscar", Toast.LENGTH_SHORT).show();
    }

    private void ActivityCategoria() {
        Toast.makeText(this, "Categoria", Toast.LENGTH_SHORT).show();
    }

    private void ActivityCarrito() {
        Toast.makeText(this, "Carrito", Toast.LENGTH_SHORT).show();
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