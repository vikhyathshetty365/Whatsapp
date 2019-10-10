package com.example.vikhyathshetty.whatsapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class groupchatactivity extends AppCompatActivity {
    private Toolbar mtoolbar;
    private ScrollView scrollView;
    private TextView dispmes;
    private EditText input;
    private ImageButton send;
    private String groupname;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference useref,groupnameref,groupkeyref;
    private String currentuser,username,currentdate,currenttime,message;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groupchatactivity);
        groupname=getIntent().getExtras().get("name").toString();

        Toast.makeText(groupchatactivity.this,groupname,Toast.LENGTH_SHORT).show();

        firebaseAuth=FirebaseAuth.getInstance();

        currentuser=firebaseAuth.getCurrentUser().getUid();

        useref= FirebaseDatabase.getInstance().getReference().child("user");
        groupnameref=FirebaseDatabase.getInstance().getReference().child("groups").child(groupname);











        initialize();
        getuserinfo();








        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                savetodatabase();
                input.setText("");
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });






    }

    @Override
    protected void onStart() {
        super.onStart();

    groupnameref.child("groups").addChildEventListener(new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            if(dataSnapshot.exists())
            {
                displaymes(dataSnapshot);

            }
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            if(dataSnapshot.exists())
            {
                displaymes(dataSnapshot);

            }

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    });

    }




    private void initialize()

    {
        mtoolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(mtoolbar);
     getSupportActionBar().setTitle(groupname);

        dispmes=(TextView)findViewById(R.id.display_message);
        input=(EditText)findViewById(R.id.input_message);
        send=(ImageButton)findViewById(R.id.send_message);
scrollView=(ScrollView)findViewById(R.id.myscroll);

    }

   private void getuserinfo()
    {


      useref.child(currentuser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    username=dataSnapshot.child("name").getValue().toString();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    private void savetodatabase()
    {
         message=input.getText().toString();

        String mesagekey=groupnameref.push().getKey();

        if(TextUtils.isEmpty(message))
        {
            Toast.makeText(groupchatactivity.this,"please enter message...",Toast.LENGTH_SHORT).show();
        }
        else
        {
            Calendar calendardate=Calendar.getInstance();
            SimpleDateFormat curformat=new SimpleDateFormat("MMM dd,yyyy");
            currentdate=curformat.format(calendardate.getTime());

            /*Calendar calendardate=Calendar.getInstance();
            SimpleDateFormat curformat=new SimpleDateFormat("MMM dd,yyyy");
            currentdate=curformat.format(calendardate.getTime());*/

            Calendar calendartime=Calendar.getInstance();
            SimpleDateFormat curformattime=new SimpleDateFormat("hh:mm a");
            currenttime=curformattime.format(calendartime.getTime());

            HashMap<String,Object> groupmessagekey=new HashMap<>();
           groupnameref.updateChildren(groupmessagekey);

           groupkeyref=groupnameref.child(mesagekey);

           HashMap<String,Object> infomap=new HashMap<>();

           infomap.put("name",username);
            infomap.put("id",currentuser);
            infomap.put("time",currenttime);
            infomap.put("date",currentdate);
            infomap.put("message",message);

            groupkeyref.updateChildren(infomap);

        }
    }
    private void displaymes(DataSnapshot dataSnapshot) {

        Iterator iterator=dataSnapshot.getChildren().iterator();


        while (iterator.hasNext())
        {

            String chatdate=(String)((DataSnapshot)iterator.next()).getValue();
            String chatmessage=(String)((DataSnapshot)iterator.next()).getValue();
            String chatname=(String)((DataSnapshot)iterator.next()).getValue();
            String chattime=(String)((DataSnapshot)iterator.next()).getValue();

            dispmes.append(chatname+"\n"+chatmessage+"\n"+chatdate+"\n"+chattime);
           // dispmes.setText(chatname+"\n"+chatmessage+"\n"+chatdate+"\n"+chattime);

scrollView.fullScroll(ScrollView.FOCUS_DOWN);

        }

    }


}
