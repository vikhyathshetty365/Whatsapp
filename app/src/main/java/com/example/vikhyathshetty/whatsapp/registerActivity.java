package com.example.vikhyathshetty.whatsapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class registerActivity extends AppCompatActivity {
private EditText Email;
private EditText Pass;
private Button register;
private TextView log;
private FirebaseAuth firebaseAuth;
private DatabaseReference rootref;
private ProgressDialog loading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        firebaseAuth=FirebaseAuth.getInstance();

        rootref= FirebaseDatabase.getInstance().getReference();



        initialize();





        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Login();
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading.setTitle("creating an account");
                loading.setMessage("please wait...");
                loading.show();



                String email=Email.getText().toString();
                String pass=Pass.getText().toString();


           {
                    firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                String current=firebaseAuth.getCurrentUser().getUid();
                                rootref.child("users").child(current).setValue("");
                                main();
                                loading.dismiss();
                                Toast.makeText(registerActivity.this, "registration sucessful", Toast.LENGTH_SHORT).show();
                            } else
                                Toast.makeText(registerActivity.this, "registration failed", Toast.LENGTH_SHORT).show();
                            loading.dismiss();
                        }
                    });
                }
            }
        });



    }

    private void main()
    {
        Intent mainintent=new Intent(registerActivity.this,MainActivity.class);
        startActivity(mainintent);
    }


    private void Login() {
        Intent login=new Intent(registerActivity.this,MainActivity.class);
        startActivity(login);
    }

    private void initialize() {
        loading=new ProgressDialog(this);
        Email=(EditText)findViewById(R.id.email1);
        Pass=(EditText)findViewById(R.id.password1);
        register=(Button)findViewById(R.id.login1);
        log=(TextView)findViewById(R.id.fopass1);

    }
}
