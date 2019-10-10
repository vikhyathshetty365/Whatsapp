package com.example.vikhyathshetty.whatsapp;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class RequestFragment extends Fragment {
private RecyclerView recyclerView;
private View requestview;
private DatabaseReference requestref,userref,contactref;
private FirebaseAuth mauth;
private String currentuserid;

    public RequestFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        requestview= inflater.inflate(R.layout.fragment_request, container, false);


        recyclerView=(RecyclerView)requestview.findViewById(R.id.request_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        requestref= FirebaseDatabase.getInstance().getReference().child("request");
        userref=FirebaseDatabase.getInstance().getReference().child("user");
        contactref=FirebaseDatabase.getInstance().getReference().child("contacts");


        mauth=FirebaseAuth.getInstance();
        currentuserid=mauth.getCurrentUser().getUid();


        return requestview;


    }

    @Override
    public void onStart()
    {
        super.onStart();

        FirebaseRecyclerOptions<contactfriends>options=new FirebaseRecyclerOptions.Builder<contactfriends>()
                .setQuery(requestref.child(currentuserid),contactfriends.class).build();


        FirebaseRecyclerAdapter<contactfriends,requestviewholder> adapter=new FirebaseRecyclerAdapter<contactfriends, requestviewholder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final requestviewholder holder, int position, @NonNull contactfriends model)
            {
                holder.itemView.findViewById(R.id.accept_button).setVisibility(View.VISIBLE);
                holder.itemView.findViewById(R.id.decline_button).setVisibility(View.VISIBLE);

                final String list_user_ids=getRef(position).getKey();

                DatabaseReference gettype=getRef(position).child("request_type").getRef();



                gettype.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {

                        if(dataSnapshot.exists())
                        {
                            String type=dataSnapshot.getValue().toString();

                            if(type.equals("received"))
                            {
                                userref.child(list_user_ids).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot)
                                    {
                                     if(dataSnapshot.hasChild("image"))
                                     {
                                         final String profileimage=dataSnapshot.child("image").getValue().toString();
                                         Picasso.get().load(profileimage).placeholder(R.drawable.profile_image).into(holder.profile);

                                     }
                                        final String pofilename=dataSnapshot.child("name").getValue().toString();
                                        final String profilestatus=dataSnapshot.child("status").getValue().toString();

                                        holder.username.setText(pofilename);
                                        holder.status.setText(profilestatus);

                                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {



                                                CharSequence options[]=new CharSequence[]
                                                        {
                                                                "Accept",

                                                                "Decline"

                                                        };
                                                AlertDialog.Builder builder=new AlertDialog.Builder(getContext());

                                                builder.setTitle(pofilename+"chat request");

                                                builder.setItems(options, new DialogInterface.OnClickListener()
                                                {


                                                    @Override
                                                    public void onClick(DialogInterface dialog, int i)
                                                    {

                                                        if(i==0)
                                                        {
                                                            contactref.child(currentuserid).child(list_user_ids)
                                                                    .child("contact").setValue("saved")
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task)
                                                                        {

                                                                            if(task.isSuccessful())
                                                                            {

                                                                                contactref.child(list_user_ids).child(currentuserid)
                                                                                        .child("contact").setValue("saved")
                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task)
                                                                                            {

                                                                                                if(task.isSuccessful())
                                                                                                {
                                                                                                    requestref.child(currentuserid).child(list_user_ids)
                                                                                                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task<Void> task)
                                                                                                        {

                                                                                                            if(task.isSuccessful())
                                                                                                            {

                                                                                                                requestref.child(list_user_ids).child(currentuserid)
                                                                                                                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                    @Override
                                                                                                                    public void onComplete(@NonNull Task<Void> task)
                                                                                                                    {
                                                                                                                        if(task.isSuccessful())
                                                                                                                        {
                                                                                                                            Toast.makeText(getContext(),"contact saved",Toast.LENGTH_SHORT).show();
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

                                                        if(i==1)
                                                        {

                                                            requestref.child(currentuserid).child(list_user_ids)
                                                                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task)
                                                                {

                                                                    if(task.isSuccessful())
                                                                    {

                                                                        requestref.child(list_user_ids).child(currentuserid)
                                                                                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task)
                                                                            {
                                                                                if(task.isSuccessful())
                                                                                {
                                                                                    Toast.makeText(getContext(),"contact saved",Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            }
                                                                        });

                                                                    }

                                                                }
                                                            });




                                                        }

                                                    }
                                                });
                                                builder.show();

                                            }
                                        });








                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });


                            }

                            else if(type.equals("sent"))
                            {

                                Button accept_btn=holder.itemView.findViewById(R.id.accept_button);
                                accept_btn.setText("request sent");

                                Button decline_btn=holder.itemView.findViewById(R.id.decline_button);
                                decline_btn.setVisibility(View.INVISIBLE);

                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        userref.child(list_user_ids).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot)
                                            {
                                                if(dataSnapshot.hasChild("image"))
                                                {
                                                   String profileimage=dataSnapshot.child("image").getValue().toString();
                                                   Picasso.get().load(profileimage).placeholder(R.drawable.profile_image).into(holder.profile);
                                                }
                                                String profilename=dataSnapshot.child("name").getValue().toString();
                                                String profilestatus=dataSnapshot.child("status").getValue().toString();

                                                holder.username.setText(profilename);
                                                holder.status.setText("you have sent request to"+" "+ profilename);

                                                AlertDialog.Builder builder=new AlertDialog.Builder(getContext());

                                                CharSequence options[] =new CharSequence[]
                                                        {
                                                          "cancel request"
                                                        };

                                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int i)
                                                    {
                                                        if(i==0)
                                                        {
                                                            requestref.child(currentuserid).child(list_user_ids)
                                                                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                    if(task.isSuccessful())
                                                                    {
                                                                        requestref.child(list_user_ids).child(currentuserid)
                                                                                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task)
                                                                            {
                                                                                if(task.isSuccessful())
                                                                                {
                                                                                    Toast.makeText(getContext(),"request cancelled",Toast.LENGTH_SHORT).show();
                                                                                }

                                                                            }
                                                                        });

                                                                    }

                                                                }
                                                            });
                                                        }


                                                    }
                                                });

                                                builder.show();
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                });







                            }







                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @NonNull
            @Override
            public requestviewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
            {

                View view=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.friend_layout,viewGroup,false);
                requestviewholder requestviewholder=new requestviewholder(view);
                return requestviewholder;
            }
        };

recyclerView.setAdapter(adapter);
adapter.startListening();

    }

    public static class requestviewholder extends RecyclerView.ViewHolder
    {
        private TextView username,status;
        private CircleImageView profile;
        private Button accept,decline;
        public requestviewholder(@NonNull View itemView) {
            super(itemView);

            username=itemView.findViewById(R.id.user_name);
            status=itemView.findViewById(R.id.user_status);
            profile=itemView.findViewById(R.id.profile_image);
            accept=itemView.findViewById(R.id.accept_button);
            decline=itemView.findViewById(R.id.decline_button);
        }
    }

}
