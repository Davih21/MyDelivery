package com.example.dv.delivery;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class Form_Cadastro extends AppCompatActivity {


    private EditText edit_nome, edit_email, edit_senha;
    private CircleImageView fotoUsuario;
    private Button bt_cadastrar, bt_selecionarFoto;
    private TextView txt_mensagemErro;
    private Uri mSelecionarUri;
    private String usuarioID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_form_cadastro);

//        FirebaseApp.initializeApp(this);
//        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
//        firebaseAppCheck.installAppCheckProviderFactory(
//                PlayIntegrityAppCheckProviderFactory.getInstance());

        //Escode parde da barra superior
        getSupportActionBar().hide();
        IniciarComponentes();

        //instancia a escuta
        edit_nome.addTextChangedListener(cadastroTextWatcher);
        edit_email.addTextChangedListener(cadastroTextWatcher);
        edit_senha.addTextChangedListener(cadastroTextWatcher);


        bt_cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CadastrarUsuario(view);
            }
        });

        bt_selecionarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelecionarFotogaleria();
            }
        });
    }


    //dados que seram enviados para o firebase
    private void CadastrarUsuario(View view) {

        String email = edit_email.getText().toString();
        String senha = edit_senha.getText().toString();

        //Cadastro no firebase direto e traz mensagem para realizar a autenticacao
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    SalvarDadosUsuario();
                    Snackbar snackbar = Snackbar.make(view, "Cadastro realizado com sucesso", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Ok", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    finish();
                                }
                            });
                    snackbar.show();
                } else {
                    //mensagem sera setada para a mensagem de erro
                    String erro;
                    try {
                        throw Objects.requireNonNull(task.getException());
                    } catch (FirebaseAuthWeakPasswordException e) {
                        erro = "Coloque uma senha com no minimo 6 numeros!";
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        erro = "E-mail invalido!";
                    } catch (FirebaseAuthUserCollisionException e) {
                        erro = "Usuario ja cadastrado";
                    } catch (FirebaseNetworkException e) {
                        erro = "Sem conexao com a inrternet";
                    } catch (Exception e) {
                        erro = "Ops, erro ao cadastrar usuario";
                    }

                    txt_mensagemErro.setText(erro);
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

    //abre a galeria e enviar para ser gerado a URI
    public void SelecionarFotogaleria() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        activityResultLauncher.launch(intent);
    }

    public void SalvarDadosUsuario() {

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

                                DocumentReference documentReference = db.collection("Usuarios").document(usuarioID);
                                documentReference.set(usuarios).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.i("db", "dados salvos com sucesso");
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("db_erro", "Erro ao salvar os dados", e);
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

    private void IniciarComponentes() {

        edit_nome = findViewById(R.id.edit_nome);
        edit_email = findViewById(R.id.edit_email);
        edit_senha = findViewById(R.id.edit_senha);
        fotoUsuario = findViewById(R.id.fotoUsuario);
        bt_cadastrar = findViewById(R.id.bt_cadastrar);
        bt_selecionarFoto = findViewById(R.id.bt_selecionarFoto);
        txt_mensagemErro = findViewById(R.id.txt_mensagemErro);
    }

    //serve para escutar o que vai ser escrito em cada campo
    TextWatcher cadastroTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {


        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            String nome = edit_nome.getText().toString();
            String email = edit_email.getText().toString();
            String senha = edit_senha.getText().toString();

            if (!nome.isEmpty() && !email.isEmpty() && !senha.isEmpty()) {
                bt_cadastrar.setEnabled(true);
                bt_cadastrar.setBackgroundColor(getResources().getColor(R.color.dark_red));
            } else {
                bt_cadastrar.setEnabled(false);
                bt_cadastrar.setBackgroundColor(getResources().getColor(R.color.gray));
            }

        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

}