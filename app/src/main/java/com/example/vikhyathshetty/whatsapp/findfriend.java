package com.example.vikhyathshetty.whatsapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class findfriend extends AppCompatActivity {

    private Toolbar mtoolbar;
    private RecyclerView friend_recycler;
    private DatabaseReference userref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findfriend);
        userref= FirebaseDatabase.getInstance().getReference().child("user");

        friend_recycler=(RecyclerView)findViewById(R.id.find_friend_recycler);
        friend_recycler.setLayoutManager(new LinearLayoutManager(this));

        mtoolbar=(Toolbar)findViewById(R.id.findfriend_tool);
       setSupportActionBar(mtoolbar);
       getSupportActionBar().setDisplayHomeAsUpEnabled(true);
       getSupportActionBar().setDisplayShowHomeEnabled(true);
       getSupportActionBar().setTitle("Find friends");
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<contactfriends>options=new
                FirebaseRecyclerOptions.Builder<contactfriends>().setQuery(userref,contactfriends.class).build();

        FirebaseRecyclerAdapter<contactfriends,findfriendviewholder>adapter
                =new FirebaseRecyclerAdapter<contactfriends, findfriendviewholder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull findfriendviewholder holder, final int position, @NonNull contactfriends model) {
                holder.username.setText(model.getName());
                holder.status.setText(model.getStatus());
                Picasso.get().load(model.getImage()).placeholder(R.drawable.profile_image).into(holder.profile);



                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent profileintent=new Intent(findfriend.this,profile_activity.class);
                        String userid=getRef(position).getKey();
                                profileintent.putExtra("user_id",userid);
                        startActivity(profileintent);

                    }
                });
            }

            @NonNull
            @Override
            public findfriendviewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.friend_layout,viewGroup,false);
                findfriendviewholder viewholder=new findfriendviewholder(view);
                return viewholder;

            }

        };
        friend_recycler.setAdapter(adapter);
        adapter.startListening();



    }
    public static class findfriendviewholder extends RecyclerView.ViewHolder
    {
        TextView username,status;
        CircleImageView profile;
        public findfriendviewholder(@NonNull View itemView) {
            super(itemView);
            username=itemView.findViewById(R.id.user_name);
            status=itemView.findViewById(R.id.user_status);
            profile=itemView.findViewById(R.id.profile_image);

        }
    }



}
