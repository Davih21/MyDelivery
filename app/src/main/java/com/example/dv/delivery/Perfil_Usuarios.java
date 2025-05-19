package com.example.dv.delivery;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import de.hdodenhof.circleimageview.CircleImageView;

public class Perfil_Usuarios extends AppCompatActivity {

    private CircleImageView foto_usuario;
    private TextView nome_usuario, email_usuario;
    private Button bt_editarPerfil;
    private String usuarioID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_perfil_usuarios);


        IniciarComponentes();

        bt_editarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Perfil_Usuarios.this, Editar_Perfil.class);
                startActivity(intent);
            }
        });
    }

    //cliclo de vida
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        DocumentReference documentReference = db.collection("Usuarios").document(usuarioID);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot != null) {
                    Glide.with(getApplicationContext()).load(documentSnapshot.getString("foto")).into(foto_usuario); //baixar e reenderiza imagem no banco
                    nome_usuario.setText(documentSnapshot.getString("nome")); // recupera o nome do usuario
                    email_usuario.setText(email);// recupera o nome do email
                }
            }
        });

    }

    private void IniciarComponentes() {

        foto_usuario = findViewById(R.id.foto_usuario);
        nome_usuario = findViewById(R.id.nome_usuario);
        email_usuario = findViewById(R.id.email_usuario);
        bt_editarPerfil = findViewById(R.id.bt_editarPerfil);
    }
}