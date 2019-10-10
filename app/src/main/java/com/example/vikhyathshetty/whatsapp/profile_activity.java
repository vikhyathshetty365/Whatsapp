package com.example.vikhyathshetty.whatsapp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class profile_activity extends AppCompatActivity {
    private String senderuserid, recieveid, currentstate;
    private DatabaseReference userref,requestref,contactsref;
    private TextView username;
    private TextView userstatus;
    private CircleImageView profile;
    private FirebaseAuth firebaseAuth;

    private Button send,decline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_activity);
        firebaseAuth = FirebaseAuth.getInstance();
        userref = FirebaseDatabase.getInstance().getReference().child("user");
        requestref = FirebaseDatabase.getInstance().getReference().child("request");
        contactsref=FirebaseDatabase.getInstance().getReference().child("contacts");

        senderuserid = FirebaseAuth.getInstance().getUid();
        recieveid = getIntent().getExtras().get("user_id").toString();
        //currentstate="new";



        //Toast.makeText(this,userid,Toast.LENGTH_SHORT).show();
        username = (TextView) findViewById(R.id.username);
        userstatus = (TextView) findViewById(R.id.userstatus);
        profile = (CircleImageView) findViewById(R.id.pro_img);
        send = (Button) findViewById(R.id.send_message);
        decline=(Button)findViewById(R.id.decline_message);
        currentstate = "new";
      //  Toast.makeText(profile_activity.this,recieveid,Toast.LENGTH_SHORT).show();
       // Toast.makeText(profile_activity.this,senderuserid,Toast.LENGTH_SHORT).show();



        retrieveinfo();
    }


    private void retrieveinfo() {
        userref.child(recieveid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (((dataSnapshot).exists()) && ((dataSnapshot).hasChild("image"))) {
                    String image = dataSnapshot.child("image").getValue().toString();
                    String name = dataSnapshot.child("name").getValue().toString();
                    String status = dataSnapshot.child("status").getValue().toString();

                    username.setText(name);
                    userstatus.setText(status);
                    Picasso.get().load(image).placeholder(R.drawable.profile_image).into(profile);

                    managechats();


                }
                else
                    {
                    String name = dataSnapshot.child("name").getValue().toString();
                    String status = dataSnapshot.child("status").getValue().toString();

                    username.setText(name);
                    userstatus.setText(status);
                        managechats();





                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    private void managechats()
    {

        requestref.child(recieveid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.hasChild(senderuserid))
                {
                    String requesttype=dataSnapshot.child(senderuserid).child("request_type").getValue().toString();

                    if(requesttype.equals("received"));
                    {
                        send.setText("cancel request");
                        currentstate="request_sent";

                    }

                    if(requesttype.equals("sent"))
                {
                    currentstate="request_received";
                    send.setText("accept request");
                    decline.setVisibility(View.VISIBLE);
                    decline.setEnabled(true);

                    decline.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            decline.setVisibility(View.INVISIBLE);
                            cancelrequest();

                        }
                    });
                }

                }
                else
                {
                    contactsref.child(senderuserid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild(recieveid))
                            {
                                currentstate="friends";
                                send.setText("remove from contacts");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });











        if(!senderuserid.equals(recieveid))
        {
            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    if (currentstate.equals("new"))
                    {
                       // send.setEnabled(false);
                        send.setText("cancel request");
                        managechatrequest();


                    }
                    if(currentstate.equals("request_sent"))
                    {
                       // send.setEnabled(true);
                        cancelrequest();
                    }

                    if(currentstate.equals("request_received"))
                    {

                        accepttocontacts();
                    }

                    if(currentstate.equals("friends"))
                    {
                        removefromcontacts();
                    }
                }
            });

        }
        else
        {
            send.setVisibility(View.INVISIBLE);
        }
    }

    private void removefromcontacts()
    {

        contactsref.child(senderuserid).child(recieveid)
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful())
                {
                    contactsref.child(recieveid).child(senderuserid)
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if(task.isSuccessful())
                            {
                                send.setEnabled(true);
                                send.setText("send message");
                                currentstate="new";

                                decline.setEnabled(false);
                                decline.setVisibility(View.INVISIBLE);
                            }

                        }
                    });
                }

            }
        });
    }

    private void accepttocontacts()
    {
        contactsref.child(senderuserid).child(recieveid)
                .child("contacts").setValue("saved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful())
                        {
                            contactsref.child(recieveid).child(senderuserid)
                                    .child("contacts").setValue("saved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if(task.isSuccessful())
                                            {
                                                requestref.child(senderuserid).child(recieveid)
                                                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        if(task.isSuccessful())
                                                        {
                                                            requestref.child(recieveid).child(senderuserid)
                                                                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                    if(task.isSuccessful())
                                                                    {
                                                                        send.setEnabled(true);
                                                                        currentstate="friends";
                                                                        send.setText("remove from contacts");

                                                                        decline.setVisibility(View.INVISIBLE);


                                                                    }
                                                                }
                                                            });
                                                        }

                                                    }
                                                });
                                            }

                                        }
                                    });



                        }


                    }
                });


    }

    private void cancelrequest()
    {

        requestref.child(recieveid).child(senderuserid)
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful())
                {

                    requestref.child(senderuserid).child(recieveid)
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                //send.setEnabled(true);
                                currentstate="new";
                                send.setText("send message");
                            }
                        }
                    });

                }

            }
        });
    }

    private void managechatrequest()
    {
            requestref.child(senderuserid).child(recieveid) .child("request_type").setValue("sent")
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful())
                            {
                                requestref.child(recieveid).child(senderuserid).child("request_type").setValue("received")
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful())
                                                {   currentstate="request_sent";
                                                    //send.setEnabled(false);
                                                    send.setText("cancel request");

                                                }

                                            }
                                        });
                            }

                        }
                    });




    }



}

