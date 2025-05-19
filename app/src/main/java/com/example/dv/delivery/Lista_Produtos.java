package com.example.dv.delivery;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dv.delivery.Adapter.AdapterProdutor;
import com.example.dv.delivery.Model.Produto;
import com.example.dv.delivery.RecyclerItemClickListener.RecyclerItemClickListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Lista_Produtos extends AppCompatActivity {

    private RecyclerView recyclerView_produtos;
    private AdapterProdutor adapterProdutor;
    private List<Produto> produtoList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lista_produtos);

        recyclerView_produtos = findViewById(R.id.recyclerView_produtos);
        produtoList = new ArrayList<>();
        adapterProdutor = new AdapterProdutor(getApplicationContext(), produtoList);
        recyclerView_produtos.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView_produtos.setHasFixedSize(true);
        recyclerView_produtos.setAdapter(adapterProdutor);

        //evento de click RecyclerItemClickListener
        recyclerView_produtos.addOnItemTouchListener(new RecyclerItemClickListener(
                getApplicationContext(),
                recyclerView_produtos,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(Lista_Produtos.this, Detalhes_Produtos.class);
                        intent.putExtra("foto", produtoList.get(position).getFoto());
                        intent.putExtra("nome", produtoList.get(position).getNome());
                        intent.putExtra("descricao", produtoList.get(position).getDescricao());
                        intent.putExtra("preco", produtoList.get(position).getPreco());
                        startActivity(intent);

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                }
        ));

db = FirebaseFirestore.getInstance();
db.collection("Produtos").orderBy("nome")
        .get()
        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) { //captura o resultado da consulta
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                        Produto produto = queryDocumentSnapshot.toObject(Produto.class);
                        produtoList.add(produto);
                        adapterProdutor.notifyDataSetChanged(); // notifica atualizacao dentro da colecao
                    }
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int itemID = item.getItemId();

        if (itemID == R.id.perfil){
            Intent perfil = new Intent(Lista_Produtos.this, Perfil_Usuarios.class);
            startActivity(perfil);

        }else if (itemID == R.id.pedidos){

        }else if (itemID == R.id.deslogar){
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(Lista_Produtos.this, "Deslogar usuario", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Lista_Produtos.this, Form_Login.class);
            startActivity(intent);
            finish();
        }


        return super.onOptionsItemSelected(item);
    }
}