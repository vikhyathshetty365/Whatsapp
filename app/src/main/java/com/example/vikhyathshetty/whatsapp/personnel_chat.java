package com.example.vikhyathshetty.whatsapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class personnel_chat extends AppCompatActivity

{
    private TextView username,lastseen;
    private CircleImageView profile;
    private Toolbar mtoolbar;
    private String receivername,receiverid,userimage;
    private FirebaseAuth mauth;
    private DatabaseReference rootref;
private ImageButton send,send_file;
private EditText input_message;
private String senderid,checker="",currentdate,currenttime,myurl;

private final List<messages>messagesList=new ArrayList<>();

    private LinearLayoutManager linearLayoutManager;
   private MessagesAdapter messageadapter;
    private RecyclerView usermessagelist;
    private UploadTask upload;
private Uri fileuri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personnel_chat);

        receiverid=getIntent().getExtras().get("userids").toString();
        receivername=getIntent().getExtras().get("username").toString();
        userimage=getIntent().getExtras().get("image").toString();

        intialize();
        rootref= FirebaseDatabase.getInstance().getReference();

        mauth=FirebaseAuth.getInstance();
        senderid=mauth.getCurrentUser().getUid();


        username.setText(receivername);
        Picasso.get().load(userimage).placeholder(R.drawable.profile_image).into(profile);

        send.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                savetodatabase();

            }

        });

        send_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                final CharSequence options[]=new CharSequence[]
                        {
                                "image",
                                "PDF",
                                "docx"

                        };

                AlertDialog.Builder builder=new AlertDialog.Builder(personnel_chat.this);

                builder.setTitle("select file type");

                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i)
                    {
                        if(i==0)
                        {
                           checker="image";

                            Intent intent=new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");
                            startActivityForResult(Intent.createChooser(intent,"select image"),444);

                        }
                        if(i==1)
                        {
                            Intent intent=new Intent();
                            intent.setAction(intent.ACTION_GET_CONTENT);
                            intent.setType("application/pdf");
                            startActivityForResult(Intent.createChooser(intent,"choose a PDF file"),444);


                        }
                        if(i==2)
                        {
                            Intent intent=new Intent();
                            intent.setAction(intent.ACTION_GET_CONTENT);
                            intent.setType("application/docx");
                            startActivityForResult(Intent.createChooser(intent,"choose a document file"),444);


                        }


                    }
                });builder.show();

            }
        });

    }







    private void savetodatabase()
    {
        Calendar calendardate=Calendar.getInstance();
        SimpleDateFormat curformat=new SimpleDateFormat("MMM dd,yyyy");
        currentdate=curformat.format(calendardate.getTime());


        Calendar calendartime=Calendar.getInstance();
        SimpleDateFormat curformattime=new SimpleDateFormat("hh:mm a");
        currenttime=curformattime.format(calendartime.getTime());



        String sendermessage=input_message.getText().toString();

        if(TextUtils.isEmpty(sendermessage))
        {
            Toast.makeText(personnel_chat.this,"enter message",Toast.LENGTH_SHORT).show();
        }

        String sendermessageref="messages/"+senderid+"/"+receiverid;
        String receivermessageref="messages/"+receiverid+"/"+senderid;

        DatabaseReference messagekeyref=rootref.child("messages").child(senderid)
                .child(receiverid).push();

        String messagekey=messagekeyref.getKey();

        Map bodytext=new HashMap();
        bodytext.put("message",sendermessage);
        bodytext.put("type","text");
        bodytext.put("from",senderid);
        bodytext.put("to",receiverid);
        bodytext.put("date",currentdate);
        bodytext.put("time",currenttime);

        Map bodydetails=new HashMap();
            bodydetails.put(sendermessageref+"/"+messagekey,bodytext);
            bodydetails.put(receivermessageref+"/"+messagekey,bodytext);

            rootref.updateChildren(bodydetails);

input_message.setText("");

    }

    private void intialize()
    {

        mtoolbar=(Toolbar)findViewById(R.id.chat_tool);

        setSupportActionBar(mtoolbar);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater inflater=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionviewbar=inflater.inflate(R.layout.chat_toolbar,null);



        actionBar.setCustomView(actionviewbar);

        username=(TextView)findViewById(R.id.chat_user_name);
        lastseen=(TextView)findViewById(R.id.chat_lastseen);
        profile=(CircleImageView)findViewById(R.id.chat_profile);
        send=(ImageButton) findViewById(R.id.send);
        input_message=(EditText)findViewById(R.id.input);
        send_file=(ImageButton)findViewById(R.id.send_file);



        messageadapter=new MessagesAdapter(messagesList);

        usermessagelist=(RecyclerView)findViewById(R.id.personnel_chat_recycler);
        linearLayoutManager=new LinearLayoutManager(this);
        usermessagelist.setLayoutManager(linearLayoutManager);
        usermessagelist.setAdapter(messageadapter);









    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        fileuri=data.getData();

        if(requestCode==444 && resultCode==RESULT_OK && data!=null && data.getData()!=null)
        {
            if(!checker.equals("image"))
            {
                StorageReference storageReference=FirebaseStorage.getInstance().getReference().child("files");

                final String senderef="messages/"+senderid+"/"+receiverid;
                final String receiveref="messages/"+receiverid+"/"+senderid;

                DatabaseReference storagekey =rootref.child("messages").child(senderid)
                        .child(receiverid).push();

                final String keyref=storagekey.getKey().toString();

                StorageReference filepath=storageReference.child("files"+"."+checker);

                 filepath.putFile(fileuri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
                    {
                       Map map=new HashMap();
                       map.put("message",task.getResult().getDownloadUrl().toString());
                       map.put("type",checker);
                       map.put("from",senderid);
                       map.put("to",receiverid);
                       map.put("message_pushid",keyref);
                       map.put("date",currentdate);
                       map.put("time",currenttime);

                       Map bodydetails=new HashMap();
                       bodydetails.put(senderef+"/"+keyref,map);
                       bodydetails.put(receiveref+"/"+keyref,map);

                       rootref.updateChildren(bodydetails);





                    }
                });


            }
            else if(checker.equals("image"))
            {
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("image_file");

                final String messagesenderref="messages/"+senderid+"/"+receiverid;
                final String messagereceiveref="messages/"+receiverid+"/"+senderid;



                DatabaseReference pushid = rootref.child("messages").child(senderid).child(receiverid).push();

                final String keyref=pushid.getKey();

                final StorageReference filepath=storageReference.child("image_file"+"."+"jpg");

                upload=filepath.putFile(fileuri);

               upload.continueWithTask(new Continuation() {
                   @Override
                   public Object then(@NonNull Task task) throws Exception {
                       if(!task.isSuccessful())
                       {
                           throw task.getException();
                       }

                       return filepath.getDownloadUrl();
                   }
               }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                   @Override
                   public void onComplete(@NonNull Task<Uri> task)
                   {
                       if(task.isSuccessful())
                       {

                           Uri downloadurl=task.getResult();

                           myurl=downloadurl.toString();

                           Map bodytext=new HashMap();
                           bodytext.put("message",myurl);
                           bodytext.put("type",checker);
                           bodytext.put("from",senderid);
                           bodytext.put("to",receiverid);
                           bodytext.put("date",currentdate);
                           bodytext.put("time",currenttime);

                           Map bodydetails= new HashMap();
                           bodydetails.put(messagesenderref+"/"+keyref,bodytext);
                           bodydetails.put(messagereceiveref+"/"+keyref,bodytext);

                           rootref.updateChildren(bodydetails);

                           input_message.setText("");




                     }



                   }
               });



            }
            else
            {

            }

        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        rootref.child("messages").child(senderid).child(receiverid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                messages message=dataSnapshot.getValue(messages.class);
                messagesList.add(message);
                messageadapter.notifyDataSetChanged();

                usermessagelist.smoothScrollToPosition(usermessagelist.getAdapter().getItemCount());



            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s)
            {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot)
            {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s)
            {

            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }
}
