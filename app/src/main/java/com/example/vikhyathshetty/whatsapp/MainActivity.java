package com.example.vikhyathshetty.whatsapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
private AcessTabAdapter acessTabAdapter;
private ViewPager viewPager;
private TabLayout tabLayout;
private Toolbar mytoolbar;
private FirebaseAuth firebaseAuth;
private FirebaseUser currentuser;
private DatabaseReference rootref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

firebaseAuth=FirebaseAuth.getInstance();
//add downside FirebaseUser
 currentuser=firebaseAuth.getCurrentUser();

rootref=FirebaseDatabase.getInstance().getReference();

mytoolbar=(Toolbar)findViewById(R.id.app_tool);
setSupportActionBar(mytoolbar);

getSupportActionBar().setTitle("WhatsApp");

acessTabAdapter=new AcessTabAdapter(getSupportFragmentManager());
viewPager=(ViewPager)findViewById(R.id.view_pager);
viewPager.setAdapter(acessTabAdapter);


tabLayout = (TabLayout) findViewById(R.id.tab_layout);
tabLayout.setupWithViewPager(viewPager);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.app_menus,menu);
        return true;
    }
/*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.app_menus,menu);
        return true;
    }*/

@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId()==R.id.and_logout)
        {
            firebaseAuth.signOut();
            loginintent();
        }
        if(item.getItemId()==R.id.and_settings)
        {
            Intent setting=new Intent(MainActivity.this,settingsActivity.class);
            startActivity(setting);

        }

        if(item.getItemId()==R.id.and_group)
        {
            requestgroup();
        }
        if(item.getItemId()==R.id.and_findfriends)
        {
            friendintent();
        }
        return true;
    }

    private void friendintent() {
Intent friend=new Intent(MainActivity.this,findfriend.class);
startActivity(friend);
    }


    @Override
    protected void onStart() {
        super.onStart();
        if(currentuser==null)
        {
            loginintent();
        }
        else
            verify();
    }

    private void verify() {
        String currentuser=firebaseAuth.getCurrentUser().getUid();

        rootref.child("user").child(currentuser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("name").exists())
                {
                    Toast.makeText(MainActivity.this,"welcome",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    settings();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void requestgroup() {
        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this,R.style.AlertDialog);
        builder.setTitle("create group");

        final EditText groupfieldname=new EditText(MainActivity.this);
        builder.setView(groupfieldname);



        builder.setPositiveButton("create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String groupname=groupfieldname.getText().toString();

                if(TextUtils.isEmpty(groupname))
                {
                    Toast.makeText(MainActivity.this,"give group name..",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    creategroup(groupname);
                }

            }


        });

        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();
    }


    private void settings()
    {
        Intent set=new Intent(MainActivity.this,settingsActivity.class);
        startActivity(set);
    }

    private void loginintent() {
        Intent login=new Intent(MainActivity.this,loginActivity.class);
        startActivity(login);
    }

    private void creategroup(String groupname) {
        rootref.child("groups").child(groupname).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
           if(task.isSuccessful())
           {
               Toast.makeText(MainActivity.this,"group created",Toast.LENGTH_SHORT).show();
           }
            }
        });
    }



}
