package com.example.dv.delivery;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class Editar_Perfil extends AppCompatActivity {

    private CircleImageView fotoUsuario;
    private EditText edit_nome;
    private Button bt_selecionarFoto, bt_atualizarDados;
    private Uri mSelecionarUri;
    private String usuarioID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_editar_perfil);

        InicializarComponentes();

        bt_selecionarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Selecioanrfotogaleria();
            }
        });

        bt_atualizarDados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String nome = edit_nome.getText().toString();
                if (nome.isEmpty()) {
                    Snackbar snackbar = Snackbar.make(view, "Preencha todos os campos!", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                } else {
                    AtualizardadosPerfil(view);
                }
            }
        });

    }

    //Vai gerar uma URI da imagem da galeria selecionado
    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        mSelecionarUri = data.getData();

                        try {
                            fotoUsuario.setImageURI(mSelecionarUri);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
            });

    private void Selecioanrfotogaleria() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        activityResultLauncher.launch(intent);
    }

    public void AtualizardadosPerfil(View view) {

        String nomeArquivo = UUID.randomUUID().toString(); // gera um id aleatorio

        final StorageReference reference = FirebaseStorage.getInstance().getReference("/imagens/" + nomeArquivo); // instancia o storage e cria uma pasta com o nome imagens salve a referencia
        reference.putFile(mSelecionarUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                //Converte as variaveis para String URI
                                String foto = uri.toString();
                                String nome = edit_nome.getText().toString();

                                //Inicializar o banco de dados - FireStore
                                FirebaseFirestore db = FirebaseFirestore.getInstance();

                                Map<String, Object> usuarios = new HashMap<>();
                                usuarios.put("nome", nome);
                                usuarios.put("foto", foto);

                                usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid(); // salve o usuario atual e o ID no firestore

                                db.collection("Usuarios").document(usuarioID)
                                        .update("nome", nome, "foto", foto)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Snackbar snackbar = Snackbar.make(view, "Sucesso ao atualziar os dados", Snackbar.LENGTH_INDEFINITE)
                                                        .setAction("Ok", new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View view) {
                                                                finish();
                                                            }
                                                        });
                                                snackbar.show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                            }
                                        });


                            }
                        }).addOnFailureListener(new OnFailureListener() { //esse so serve se der erro no download da imagem
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }

    private void InicializarComponentes() {

        fotoUsuario = findViewById(R.id.fotoUsuario);
        edit_nome = findViewById(R.id.edit_nome);
        bt_selecionarFoto = findViewById(R.id.bt_selecionarFoto);
        bt_atualizarDados = findViewById(R.id.bt_atualizarDados);
    }
}