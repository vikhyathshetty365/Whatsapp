package com.example.vikhyathshetty.whatsapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class Chat extends Fragment {

    private View chatview;
    private RecyclerView recyclerView;
    private DatabaseReference contactref,userref;
    private FirebaseAuth mauth;
   // private String currentuserid;
    private String userid;




    public Chat() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        chatview = inflater.inflate(R.layout.fragment_chat, container, false);

        mauth = FirebaseAuth.getInstance();
        userref = FirebaseDatabase.getInstance().getReference().child("user");

       userid = mauth.getCurrentUser().getUid();

        recyclerView = (RecyclerView)chatview.findViewById(R.id.chat_recycler);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        contactref=FirebaseDatabase.getInstance().getReference().child("contacts");









        return chatview;









    }

    @Override
    public void onStart()
    {
        super.onStart();

        FirebaseRecyclerOptions<contactfriends> options=new FirebaseRecyclerOptions.Builder<contactfriends>()
                .setQuery(contactref.child(userid),contactfriends.class).build();


        FirebaseRecyclerAdapter<contactfriends,chatviewholder>adapter =new FirebaseRecyclerAdapter<contactfriends, chatviewholder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final chatviewholder holder, int position, @NonNull contactfriends model)
            {
                final String list_user_ids=getRef(position).getKey();
                final String[] profileimage = {"default image"};

                userref.child(list_user_ids).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild("image"))
                        {
                             profileimage[0] =dataSnapshot.child("image").getValue().toString();
                            Picasso.get().load(profileimage[0]).placeholder(R.drawable.profile_image).into(holder.profile);


                        }
                        final String profilename=dataSnapshot.child("name").getValue().toString();
                        final String profilestatus=dataSnapshot.child("status").getValue().toString();

                        holder.username.setText(profilename);
                        holder.status.setText("last seen"+"\n"+"date");

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent=new Intent(getContext(),personnel_chat.class);
                                intent.putExtra("username",profilename);
                                intent.putExtra("userids",list_user_ids);
                                intent.putExtra("image",profileimage);

                                startActivity(intent);

                            }
                        });


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



            }

            @NonNull
            @Override
            public chatviewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
               View view=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.friend_layout,viewGroup,false);
               chatviewholder viewholdr=new chatviewholder(view);
               return viewholdr;


            }
        };
recyclerView.setAdapter(adapter);
adapter.startListening();


    }

    public static class chatviewholder extends RecyclerView.ViewHolder
    {
            private TextView username,status;
            private CircleImageView profile;
        public chatviewholder(@NonNull View itemView) {
            super(itemView);

            username=itemView.findViewById(R.id.user_name);
            status=itemView.findViewById(R.id.user_status);
            profile=itemView.findViewById(R.id.profile_image);

        }
    }






}
