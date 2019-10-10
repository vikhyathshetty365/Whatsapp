package com.example.vikhyathshetty.whatsapp;


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

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class Contact extends Fragment {
 private View contactview;
private RecyclerView recyclerView;
private DatabaseReference contactref,userref;
private FirebaseAuth mauth;
private String currentuserID;


    public Contact() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        mauth=FirebaseAuth.getInstance();
        currentuserID=mauth.getUid();

       contactref= FirebaseDatabase.getInstance().getReference().child("contacts").child(currentuserID);
       userref=FirebaseDatabase.getInstance().getReference().child("user");

        contactview = inflater.inflate(R.layout.fragment_contact, container, false);
        recyclerView=(RecyclerView)contactview.findViewById(R.id.contact_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return contactview;


    }

    @Override
    public void onStart()
    {
        super.onStart();

        FirebaseRecyclerOptions<contactfriends> options=new FirebaseRecyclerOptions.Builder<contactfriends>()
                .setQuery(contactref,contactfriends.class).build();


        FirebaseRecyclerAdapter<contactfriends,contactsviewholder> adapter =new
                FirebaseRecyclerAdapter<contactfriends, contactsviewholder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final contactsviewholder holder, int position, @NonNull final contactfriends model)
                    {
                       String userids=getRef(position).getKey();

                       userref.child(userids).addValueEventListener(new ValueEventListener() {
                           @Override
                           public void onDataChange(DataSnapshot dataSnapshot)
                           {
                               if(dataSnapshot.hasChild("image"))
                               {
                                   String profileimage=dataSnapshot.child("image").getValue().toString();
                                   String profilename=dataSnapshot.child("name").getValue().toString();
                                   String profilestatus=dataSnapshot.child("status").getValue().toString();

                                   holder.username.setText(profilename);
                                   holder.status.setText(profilestatus);
                                   Picasso.get().load(profileimage).placeholder(R.drawable.profile_image).into(holder.profile);



                               }
                               else
                               {


                                   String profilename=dataSnapshot.child("name").getValue().toString();
                                   String profilestatus=dataSnapshot.child("status").getValue().toString();

                                   holder.username.setText(profilename);
                                   holder.status.setText(profilestatus);

                               }

                           }

                           @Override
                           public void onCancelled(DatabaseError databaseError) {

                           }
                       });
                    }

                    @NonNull
                    @Override
                    public contactsviewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {


                        View view=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.friend_layout,viewGroup,false);
                        contactsviewholder contactsviewholder=new contactsviewholder(view);
                        return contactsviewholder;

                    }



                };

recyclerView.setAdapter(adapter);
adapter.startListening();







    }
    public static class contactsviewholder extends RecyclerView.ViewHolder
    {
        private TextView username,status;
        private CircleImageView profile;
        public contactsviewholder(@NonNull View itemView) {
            super(itemView);
            username=itemView.findViewById(R.id.user_name);
            status=itemView.findViewById(R.id.user_status);
            profile=itemView.findViewById(R.id.profile_image);
        }
    }


}
