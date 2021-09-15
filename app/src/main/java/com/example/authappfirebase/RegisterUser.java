package com.example.authappfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterUser extends AppCompatActivity implements View.OnClickListener {

    private TextView tituloApp, registerUser;
    private EditText editTextNome, editTextIdade, editTextEmail, editTextSenha;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        mAuth = FirebaseAuth.getInstance();

        tituloApp = findViewById(R.id.txtTitulo);
        tituloApp.setOnClickListener(this);

        registerUser = findViewById(R.id.registerUser);
        registerUser.setOnClickListener(this);

        editTextNome = findViewById(R.id.fullname);
        editTextIdade = findViewById(R.id.age);
        editTextEmail = findViewById(R.id.emailRegister);
        editTextSenha = findViewById(R.id.passwordRegister);

        progressBar = findViewById(R.id.progressBarRegister);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.txtTitulo:
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                break;
            case R.id.registerUser:
                registerUser();
                break;
        }
    }

    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String senha = editTextSenha.getText().toString().trim();
        String nome = editTextNome.getText().toString().trim();
        String idade = editTextIdade.getText().toString().trim();

        if(nome.isEmpty()){
            editTextNome.setError("Campo vazio, digite seu nome!");
            editTextNome.requestFocus();
            return;
        }

        if(idade.isEmpty()){
            editTextIdade.setError("Campo vazio, digite sua idade!");
            editTextIdade.requestFocus();
            return;
        }

        if(email.isEmpty()){
            editTextEmail.setError("Campo vazio. digite seu Email!");
            editTextEmail.requestFocus();
            return;
        }

        if(senha.isEmpty()){
            editTextSenha.setError("Campo vazio, digite sua senha!");
            editTextSenha.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError("Email inválido, insira um email válido!");
            return;
        }

        if (senha.length() < 6){
            editTextSenha.setError("Senha mínima de 6 caracteres");
            editTextSenha.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            User user = new User(nome, idade, email);
                            progressBar.setVisibility(View.VISIBLE);

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){
                                        Toast.makeText(RegisterUser.this, "Registrado com sucesso!", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);

                                        //
                                    }else{
                                        Toast.makeText(RegisterUser.this, "Falha ao registrar usuário, tente novamente", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                        }else {
                            Toast.makeText(RegisterUser.this, "Falha ao registrar", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }
}