package com.example.vikhyathshetty.whatsapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class Group extends Fragment {
private View groupview;
private ArrayAdapter<String> arrayAdapter;
private ListView listview;
private ArrayList<String> array_list=new ArrayList<>();
private DatabaseReference groupref;
private String current_group;

    public Group() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

       groupview= inflater.inflate(R.layout.fragment_group, container, false);

       groupref= FirebaseDatabase.getInstance().getReference();

     initialize();

     retrievegroupname();

     listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
         @Override
         public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
             String group=parent.getItemAtPosition(position).toString();

             Intent groupintent=new Intent(getContext(),groupchatactivity.class);
             groupintent.putExtra("name",group);
             startActivity(groupintent);
         }
     });




       return groupview;
    }



    private void initialize() {
        listview=(ListView)groupview.findViewById(R.id.list_view);

        arrayAdapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_expandable_list_item_1,array_list);
        listview.setAdapter(arrayAdapter);
    }

    private void retrievegroupname() {
        groupref.child("groups").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
Set<String> set=new HashSet<>();//for avoidig duplicates

Iterator iterator=dataSnapshot.getChildren().iterator();

while (iterator.hasNext())
{
set.add(((DataSnapshot)iterator.next()).getKey());

}
                array_list.clear();
                array_list.addAll(set);
               arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


}
