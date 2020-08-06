package com.example.firebaseapp.Fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.firebaseapp.Model.Status;
import com.example.firebaseapp.Model.Users;
import com.example.firebaseapp.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static android.app.Activity.RESULT_OK;


public class StatusFragment extends Fragment {

    private static final int PICK_IMAGE = 1 ;
    ImageView user_status;
    private Timer timer;
    private int progress;
    private TimerTask timerTask;
    private Handler handler = new Handler();
    DatabaseReference reference;
    FirebaseUser fUser;
    private int cnt=0;
    private int f=0;
    private int uploads = 0;
    private ArrayList<Uri> ImageList = new ArrayList<Uri>();
    private ArrayList<String> st_imgs = new ArrayList<String>();


    StorageReference storageReference;
    private static final int IMAGE_REQUEST=1;
    private Uri imageUri;
    private StorageTask uploadTask;
    private int g;
    private int i=0;
    ImageView status_img;

    public StatusFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_status, container, false);
        fUser=FirebaseAuth.getInstance().getCurrentUser();
        user_status=view.findViewById(R.id.profile_image21);
        reference=FirebaseDatabase.getInstance().getReference("Status").child(fUser.getUid());

            Log.e("--------",reference.toString());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String s="";
                Status st=null;
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                {
                    st=dataSnapshot1.getValue(Status.class);
                    if (st != null) {
                        s=st.getImageURL();
                    }
                }
                if(st!=null){
                Glide.with(getContext()).load(s).into(user_status);
                Toast.makeText(getContext(), "Status uploaded!", Toast.LENGTH_SHORT).show();
                f=1;}

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        storageReference= FirebaseStorage.getInstance().getReference("Status_Uploads");


        fUser= FirebaseAuth.getInstance().getCurrentUser();


        user_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(f==0){
                AddImage();

                f=1; }else{

                    final Dialog dialog = new Dialog(getContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);


                    dialog.setContentView(R.layout.dialog);
                    dialog.setTitle("Custom Dialog");

                    reference=FirebaseDatabase.getInstance().getReference("MyUsers").child(fUser.getUid());
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Users user=dataSnapshot.getValue(Users.class);

                            Log.e("username+++",user.getUsername());
                            TextView text = (TextView) dialog.findViewById(R.id.status_user);
                            ImageView image = (ImageView) dialog.findViewById(R.id.status_profile_image);
                            text.setText(user.getUsername());
                            Glide.with(getContext()).load(user.getImageURL()).into(image);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    status_img=dialog.findViewById(R.id.status_img);
                    DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Status").child(fUser.getUid());
                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            st_imgs.clear();

                            for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()) {
                                Status st = dataSnapshot1.getValue(Status.class);
                                st_imgs.add(st.getImageURL());
                            }
                            final ProgressBar progressBar = (ProgressBar) dialog.findViewById(R.id.progressBar);

                            Glide.with(getContext()).load(st_imgs.get(0)).into(status_img);
                            dialog.show();

                                i=0;
                                timer = new Timer();
                                timerTask = new TimerTask() {
                                    public void run() {
                                        handler.post(new Runnable() {
                                            public void run(){

                                                    Glide.with(getContext()).load(st_imgs.get(i)).into(status_img);
                                                    i++;
                                                if(i==st_imgs.size()){
                                                    Log.e("zzzzzz","dismiss now");
                                                    dialog.dismiss();
                                                    stopTimer();
                                                }
                                            }
                                        });
                                    }
                                };
                                timer.schedule(timerTask, 0, 5000);







                        }



                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });




                }
            }
        });

        return view;
    }

    private void AddImage() {

        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        startActivityForResult(i, PICK_IMAGE);
    }

    private String getFileExtention(Uri uri){
        ContentResolver contentResolver =getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void UploadMyImage(){


        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Uploading");
        progressDialog.show();

        if(imageUri != null) {
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    + "." + getFileExtention(imageUri));


            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                    if (!task.isSuccessful()){

                        throw  task.getException();
                    }

                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    if (task.isSuccessful()){

                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();

                        reference = FirebaseDatabase.getInstance().getReference("Status").child(fUser.getUid());


                        HashMap<String, Object> map = new HashMap<>();
                        map.put("id",fUser.getUid());
                        map.put("imageURL", mUri);
                        reference.updateChildren(map);

                        Glide.with(getContext()).load(mUri).into(user_status);

                        progressDialog.dismiss();
                    }else{

                        Toast.makeText(getContext(), "Failed!!", Toast.LENGTH_SHORT).show();
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });


        }else
        {
            Toast.makeText(getContext(), "No Image Selected", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == PICK_IMAGE) {
            if (resultCode == RESULT_OK) {
                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();

                    int CurrentImageSelect = 0;

                    while (CurrentImageSelect < count) {
                        Uri imageuri = data.getClipData().getItemAt(CurrentImageSelect).getUri();
                        ImageList.add(imageuri);
                        CurrentImageSelect = CurrentImageSelect + 1;
                    }
                    //
                    final StorageReference ImageFolder = FirebaseStorage.getInstance().getReference().child("ImageFolder");
                    g=1;
                    for (uploads = 0; uploads < ImageList.size(); uploads++) {
                        final String s="Image"+g;
                        g++;
                        Uri Image = ImageList.get(uploads);
                        final StorageReference imagename = ImageFolder.child("image/" + Image.getLastPathSegment());

                        imagename.putFile(ImageList.get(uploads)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                imagename.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {

                                        String url = String.valueOf(uri);
                                        SendLink(url,s);
                                    }
                                });

                            }
                        });

                        //
                    }
                    reference=FirebaseDatabase.getInstance().getReference("Status").child(fUser.getUid());
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            int c=0;
                            String s="";
                            for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                                c++;
                                Status st=dataSnapshot1.getValue(Status.class);
                                Log.e("sssssssss",st.getImageURL());
                                s=st.getImageURL();
                            }
                            Log.e("sssssssss",String.valueOf(c));
                            Glide.with(getContext()).load(s).into(user_status);
                            Toast.makeText(getContext(), "Status uploaded!", Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    //Glide.with(getContext()).load().into(user_status);
                }


            }
        }
    }

    private void SendLink(String url, String s) {
        reference = FirebaseDatabase.getInstance().getReference("Status").child(fUser.getUid()).child(s);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id",fUser.getUid());
        hashMap.put("imageURL", url);
        reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                ImageList.clear();
            }
        });


    }
    private void stopTimer(){
        if(timer != null){
            timer.cancel();
            timer.purge();
        }
    }
}