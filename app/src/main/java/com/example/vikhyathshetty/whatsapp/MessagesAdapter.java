package com.example.vikhyathshetty.whatsapp;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewholder>
{

    private List<messages> usermessagelist;
    private FirebaseAuth mauth;
    private DatabaseReference userref;


    public MessagesAdapter(List<messages> usermessagelist)
    {
        this.usermessagelist=usermessagelist;
    }

    public class MessageViewholder extends RecyclerView.ViewHolder
    {
        public TextView sendertext,receivertext;
        public CircleImageView pro_image;
       public ImageView senderimage,receiverimage;
        public MessageViewholder(@NonNull View itemView)
        {
            super(itemView);

            sendertext=(TextView)itemView.findViewById(R.id.message_sender);
            receivertext=(TextView)itemView.findViewById(R.id.message_receiver);
            pro_image=(CircleImageView)itemView.findViewById(R.id.personnel_profile);

            senderimage=(ImageView)itemView.findViewById(R.id.sender_image);

            receiverimage=(ImageView)itemView.findViewById(R.id.receiver_image);

        }
    }


    @NonNull
    @Override
    public MessageViewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)

    {
     View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_personnel_chat,viewGroup,false);
     mauth=FirebaseAuth.getInstance();
     return new MessageViewholder(view);



    }




    @Override
    public void onBindViewHolder(@NonNull final MessageViewholder messageViewholder, final int position)
    {
        String senderuserid=mauth.getCurrentUser().getUid();

        messages message=usermessagelist.get(position);

        String from_userid=message.getFrom();
        String frommessagetype=message.getType();

        userref=FirebaseDatabase.getInstance().getReference().child("user").child(from_userid);

        userref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {

                if(dataSnapshot.hasChild("image"))
                {
                    String receiverimage=dataSnapshot.child("image").getValue().toString();
                    Picasso.get().load(receiverimage).placeholder(R.drawable.profile_image).into(messageViewholder.pro_image);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


      messageViewholder.sendertext.setVisibility(View.GONE);
       messageViewholder.receivertext.setVisibility(View.GONE);

        messageViewholder.pro_image.setVisibility(View.GONE);


        if(frommessagetype.equals("text"))
        {

            if(from_userid.equals(senderuserid))
            {
                messageViewholder.sendertext.setVisibility(View.VISIBLE);
                messageViewholder.sendertext.setBackgroundResource(R.drawable.message_sender_layout);
                messageViewholder.sendertext.setText(message.getMessage()+"\n\n"+message.getTime()+"-"+message.getDate());


            }
            else
            {
                messageViewholder.pro_image.setVisibility(View.VISIBLE);
                messageViewholder.receivertext.setVisibility(View.VISIBLE);
                messageViewholder.receivertext.setBackgroundResource(R.drawable.message_receiver_layout);
                messageViewholder.receivertext.setText(message.getMessage()+"\n\n"+message.getTime()+"-"+message.getDate());
            }


        }
        else if(frommessagetype.equals("image"))
        {
            if(from_userid.equals(senderuserid))
            {

                messageViewholder.senderimage.setVisibility(View.VISIBLE);

                Picasso.get().load(message.getMessage()).into(messageViewholder.senderimage);


            }

            else
            {


                messageViewholder.receiverimage.setVisibility(View.VISIBLE);
                Picasso.get().load(message.getMessage()).into(messageViewholder.receiverimage);


            }




        }
        else
        {
            if(senderuserid.equals(from_userid))
            {
                messageViewholder.senderimage.setVisibility(View.VISIBLE);
                messageViewholder.senderimage.setBackgroundResource(R.drawable.edit_profile);

               messageViewholder.itemView.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(usermessagelist.get(position).getMessage()));
                       messageViewholder.itemView.getContext().startActivity(intent);
                   }
               });
            }
            else
            {
                 messageViewholder.receiverimage.setVisibility(View.VISIBLE);
                 messageViewholder.receiverimage.setBackgroundResource(R.drawable.edit_profile);

                 messageViewholder.itemView.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {

                         Intent intent=new Intent(Intent.ACTION_VIEW,Uri.parse(usermessagelist.get(position).getMessage()));
                         messageViewholder.itemView.getContext().startActivity(intent);
                     }

                 });


            }





        }






    }




    @Override
    public int getItemCount()
    {
      return usermessagelist.size();
    }


}
