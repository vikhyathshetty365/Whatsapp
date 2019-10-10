package com.example.vikhyathshetty.whatsapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
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

public class loginActivity extends AppCompatActivity {
private EditText useremail,userpass;
private TextView fpass;
private Button login;
private FirebaseAuth firebaseAuth;
//private FirebaseUser currentuser;
private ProgressDialog loading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initialize();
        loading=new ProgressDialog(this);
        firebaseAuth=FirebaseAuth.getInstance();
        // currentuser=firebaseAuth.getCurrentUser();

        fpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent register=new Intent(loginActivity.this,registerActivity.class);
                startActivity(register);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compute();
            }
        });


    }




   /*@Override
    protected void onStart() {
        super.onStart();
        if(currentuser!=null)
        {
            mainActivity();
        }
    }*/

    private void mainActivity() {
        Intent main=new Intent(loginActivity.this,MainActivity.class);
        startActivity(main);
    }

    private void compute()
{
    loading.setTitle("logging in...");
loading.setMessage("please wait...");
loading.show();
    String Email=useremail.getText().toString();
    String password=userpass.getText().toString();
    firebaseAuth.signInWithEmailAndPassword(Email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if(task.isSuccessful())
            {
                mainActivity();
                Toast.makeText(loginActivity.this,"login sucessful",Toast.LENGTH_SHORT).show();
                loading.dismiss();
            }else
                {
                    Toast.makeText(loginActivity.this,"login failed",Toast.LENGTH_SHORT).show();
                    loading.dismiss();
                }
        }
    });

}

    private void initialize()
    {
        useremail=(EditText)findViewById(R.id.email);
     userpass=(EditText)findViewById(R.id.password);
     fpass=(TextView)findViewById(R.id.fopass);
     login=(Button)findViewById(R.id.login);

    }

}
