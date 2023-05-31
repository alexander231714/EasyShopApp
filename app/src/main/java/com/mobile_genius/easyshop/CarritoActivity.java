package com.mobile_genius.easyshop;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mobile_genius.easyshop.modal.Carrito;

public class CarritoActivity extends AppCompatActivity {
private RecyclerView recyclerView;
private RecyclerView.LayoutManager lytmang;
private Button btnsig;
private TextView total, mnsj;
private double precTot = 0.0 ;
private String CurrentUserId;
private FirebaseAuth auth;
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
        auth=FirebaseAuth.getInstance();
        CurrentUserId = auth.getCurrentUser().getUid();
        btnsig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CarritoActivity.this,ConfirmarCompra.class);
                intent.putExtra("total", String.valueOf(precTot));
                startActivity(intent);
                finish();
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        
        VerificarEstadOrden();

        final DatabaseReference CartListRef = FirebaseDatabase.getInstance().getReference().child("Carrito");

        FirebaseRecyclerOptions<Carrito> options = new FirebaseRecyclerOptions.Builder<Carrito>()
                .setQuery(CartListRef.child("Usuario Compra").child(CurrentUserId).child("Productos"), Carrito.class).build();

        FirebaseRecyclerAdapter<Carrito, CarritoViewHolder> adapter= new FirebaseRecyclerAdapter<Carrito, CarritoViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CarritoViewHolder holder, int position, @NonNull Carrito model) {
                holder.carProductoNombre.setText(model.getNombre());
                holder.carProductoCantidad.setText("Cantidad: "+ model.getCantidad());
                holder.carProductoPrecio.setText(model.getPrecio());
                double UnTipoPrecio = (Double.valueOf(model.getPrecio()))*Integer.valueOf(model.getCantidad());
                precTot = precTot + UnTipoPrecio;
                total.setText("Total:  " + String.valueOf(precTot));
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CharSequence options[] = new CharSequence[]{
                                "Editar",
                                "Eliminar"
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(CarritoActivity.this);
                        builder.setTitle("Opciones del producto");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (1==0){
                                    Intent intent = new Intent(CarritoActivity.this,ProductoDetallesActivity.class);
                                    intent.putExtra("pid", model.getPid());
                                    startActivity(intent);
                                }
                                if (i==1){
                                    CartListRef.child("Usuario Compra")
                                            .child(CurrentUserId)
                                            .child("Productos")
                                            .child(model.getPid()).removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        Toast.makeText(CarritoActivity.this, "Producto Eliminado", Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(CarritoActivity.this,Principal.class);
                                                    }
                                                }
                                            });
                                }
                            }
                        });
                        builder.show();
                    }
                });
            }

            @NonNull
            @Override
            public CarritoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.car_item_layout, parent,false);
                CarritoViewHolder holder = new CarritoViewHolder(view);
                return holder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void VerificarEstadOrden() {
        DatabaseReference orderRef;
        orderRef = FirebaseDatabase.getInstance().getReference().child("Ordenes").child(CurrentUserId);

        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String estado = snapshot.child("estado").getValue().toString();
                    String nombre = snapshot.child("nombre").getValue().toString();
                    if (estado.equals("Enviado")){
                        total.setText("Estimado"+nombre+"Su pedido fue enviado");
                        recyclerView.setVisibility(View.GONE);
                        mnsj.setText("Su pedido se enviara pronto");
                        mnsj.setVisibility(View.VISIBLE);
                        btnsig.setVisibility(View.GONE);
                    }else if (estado.equals("No Enviado")){
                        total.setText("Su orden esta siendo procesada..");
                        recyclerView.setVisibility(View.GONE);
                        mnsj.setVisibility(View.VISIBLE);
                        btnsig.setVisibility(View.GONE);
                        Toast.makeText(CarritoActivity.this, "Puedes comprar mas productos cuando el anterior finalice", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}