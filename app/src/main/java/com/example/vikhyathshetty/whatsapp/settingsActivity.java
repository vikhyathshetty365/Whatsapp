package com.example.vikhyathshetty.whatsapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.RecoverySystem;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.text.TextUtilsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;


import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class settingsActivity extends AppCompatActivity {
    private EditText user;
    private EditText status;
    private Button update;
    private CircleImageView profileimg;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference rootref;
private StorageReference userprofileimg;
    private String currentuser;
    private ProgressDialog loading;
    private static final int gallerypick=1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
init();
        firebaseAuth=FirebaseAuth.getInstance();

        currentuser=firebaseAuth.getCurrentUser().getUid();

        rootref= FirebaseDatabase.getInstance().getReference();

        userprofileimg= FirebaseStorage.getInstance().getReference().child("profile image");

  update.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
          updatedetaails();
      }
  });
        retrieveuserinfo();

       profileimg.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent galleryintent=new Intent();
               galleryintent.setAction(Intent.ACTION_GET_CONTENT);
               galleryintent.setType("image/*");
               startActivityForResult(galleryintent, gallerypick);
           }
       });
    }

    private void init() {
        user=(EditText)findViewById(R.id.user);
        status=(EditText)findViewById(R.id.status);
        update=(Button)findViewById(R.id.update);
        profileimg=(CircleImageView)findViewById(R.id.profile_image);
        loading=new ProgressDialog(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==gallerypick && resultCode==RESULT_OK && data!=null)
        {
            Uri imguri=data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode==RESULT_OK)
            {
                loading.setTitle("uploading...");
                loading.setMessage("please wait...");
                loading.show();


                Uri resulturi=result.getUri();

                StorageReference profilemap=userprofileimg.child(currentuser+".jpg");
                profilemap.putFile(resulturi).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(settingsActivity.this,"uploaded successfuly",Toast.LENGTH_SHORT).show();
                            final String downloadurl=task.getResult().getDownloadUrl().toString();
                            rootref.child("user").child(currentuser).child("image").setValue(downloadurl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        Toast.makeText(settingsActivity.this,"saved to database",Toast.LENGTH_SHORT).show();
                                        loading.dismiss();
                                    }
                                    else
                                    {
                                        Toast.makeText(settingsActivity.this,"failed",Toast.LENGTH_SHORT).show();
                                        loading.dismiss();
                                    }

                                }
                            });
                        }
                        else
                        {
                            Toast.makeText(settingsActivity.this,"failed",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }

        }

    }

    private void updatedetaails()
{

    String username=user.getText().toString();
    String stat=status.getText().toString();
if(TextUtils.isEmpty(username))
{
    Toast.makeText(settingsActivity.this,"give email id",Toast.LENGTH_SHORT).show();
}
    if(TextUtils.isEmpty(stat))
    {
        Toast.makeText(settingsActivity.this,"guve your status",Toast.LENGTH_SHORT).show();
    }
else {
        loading.setTitle("updating");
        loading.setMessage("please wait...");
        loading.show();

        HashMap<String, String> profilemap = new HashMap<>();
        profilemap.put("uid", currentuser);
        profilemap.put("name", username);
        profilemap.put("status", stat);

        rootref.child("user").child(currentuser).setValue(profilemap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    loading.dismiss();
                    mainintent();
                    Toast.makeText(settingsActivity.this, "update sucessful", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(settingsActivity.this, "update failed", Toast.LENGTH_SHORT).show();
                    loading.dismiss();
                }
            }


        });
    }

}

    private void retrieveuserinfo() {

        rootref.child("user").child(currentuser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name") && dataSnapshot.hasChild("image")))
                {
                    String username=dataSnapshot.child("name").getValue().toString();
                    String userstatus=dataSnapshot.child("status").getValue().toString();
                    String profileimg1=dataSnapshot.child("image").getValue().toString();
                    user.setText(username);
                    status.setText(userstatus);

                    Picasso.get().load(profileimg1).into(profileimg);

                }
                else if((dataSnapshot.exists())&&(dataSnapshot.hasChild("name")))
                {

                    String username=dataSnapshot.child("name").getValue().toString();
                    String userstatus=dataSnapshot.child("status").getValue().toString();
                    user.setText(username);
                    status.setText(userstatus);
                }
                else
                {
                    Toast.makeText(settingsActivity.this,"complete your profile...",Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void mainintent() {
        Intent main=new Intent(settingsActivity.this,MainActivity.class);
        startActivity(main);
    }


}
