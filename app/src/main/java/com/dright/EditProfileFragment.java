package com.dright;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

public class EditProfileFragment extends Fragment {

    View ProfileView;
    TextInputLayout newfullname;
    TextInputLayout newaddress;
    TextInputLayout newphone;
    TextInputLayout newfacebook;

    TextView tvname;
    TextView tvaddress;

    TextView currentfollowers;
    TextView currentfollowing;
    FloatingActionButton fab1;
    public static String fullname= null;
    public static String address= null;
    public static String phone= null;
    public static String facebookaccount= null;
    public static String followers = null;
    public static String following = null;

    private DatabaseReference db;
    private FirebaseAuth currentUser;

    StorageReference storageReference;
    public static int IMAGE_REQUEST = 1;

    private Uri taskUri;
    private Uri imageUri;
    private StorageTask uploadTask;
    ImageView profilePicture;

    FloatingActionButton fabEditPicture;
    public String imageUrl;
    byte[] thumb_byte_data;
    Bitmap thumb_bitmap;
    ByteArrayOutputStream baos;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ProfileView = inflater.inflate(R.layout.edit_myprofile,container,false);
        getActivity().setTitle("Edit Profile");
        currentUser = FirebaseAuth.getInstance();
        db= FirebaseDatabase.getInstance().getReference("Users/"+currentUser.getUid());
        newfullname = ProfileView.findViewById(R.id.txt_input_fullname);
        newaddress = ProfileView.findViewById(R.id.txt_input_address);
        newphone = ProfileView.findViewById(R.id.txt_input_phone);
        newfacebook = ProfileView.findViewById(R.id.txt_input_facebook);
        fabEditPicture = ProfileView.findViewById(R.id.floatingActionButton);
        storageReference = FirebaseStorage.getInstance().getReference("profile_pic");
        profilePicture = ProfileView.findViewById(R.id.profile_image);
        tvname = ProfileView.findViewById(R.id.tv_name);
        tvaddress = ProfileView.findViewById(R.id.tv_address);

        currentfollowers = ProfileView.findViewById(R.id.current_followers);
        currentfollowing = ProfileView.findViewById(R.id.current_following);


        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                fullname = dataSnapshot.child("name").getValue().toString();
                address = dataSnapshot.child("address").getValue().toString();
                phone = dataSnapshot.child("phone").getValue().toString();
                facebookaccount = dataSnapshot.child("facebook").getValue().toString();
                followers = String.valueOf(dataSnapshot.child("followers").getChildrenCount());
                following = String.valueOf(dataSnapshot.child("following").getChildrenCount());
                imageUrl = dataSnapshot.child("imageURL").getValue().toString();
                tvname.setText(fullname);
                tvaddress.setText(address);
                currentfollowers.setText(followers);
                currentfollowing.setText(following);
                if(!imageUrl.equals(""));
                    Glide.with(ProfileView.getContext()).load(imageUrl).apply(RequestOptions.circleCropTransform()).into(profilePicture);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        fabEditPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage();
            }
        });




        fab1 = ProfileView.findViewById(R.id.fabsave_profile);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fullname = newfullname.getEditText().getText().toString();
                address = newaddress.getEditText().getText().toString();
                phone = newphone.getEditText().getText().toString();
                facebookaccount = newfacebook.getEditText().getText().toString();

                if(fullname.isEmpty())
                {

                    newfullname.setError("Please fill the Full Name field");
                }
                else
                {
                    newfullname.setError(null);
                    fullname = newfullname.getEditText().getText().toString();
                    db.child("name").setValue(fullname);
                    tvname.setText(fullname);
                }

                if(address.isEmpty())
                {
                    newaddress.setError("Please fill the Address field");
                }
                else
                {
                    db.child("address").setValue(address);
                    newaddress.setError(null);
                    tvaddress.setText(address);
                }
                db.child("phone").setValue(phone);
                db.child("facebook").setValue(facebookaccount);


            }
        });


        return ProfileView;

    }

    private void openImage() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,IMAGE_REQUEST);
    }

    private String getFileExtension(Uri imageUri)
    {
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(this.imageUri));

    }
    private void uploadImage(Bitmap bmp)
    {
        final ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("Uploading");
        pd.show();

        if(imageUri != null) {

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
                byte[] data = byteArrayOutputStream.toByteArray();
                //getting imageUri and store in file. and compress to bitmap
                final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
                Log.d("insidetry","yep");

                uploadTask = fileReference.putBytes(data);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        if (taskSnapshot.getMetadata() != null) {
                            if (taskSnapshot.getMetadata().getReference() != null) {
                                Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {

                                            imageUrl = uri.toString();
                                            Glide.with(getContext()).load(imageUrl).apply(RequestOptions.circleCropTransform().fitCenter().override(100,100)).into(profilePicture);
                                            db = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
                                            db.child("imageURL").setValue(imageUrl);
                                            Log.d("urlurl", imageUrl);

                                    }
                                });
                            }
                        }
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            //taskUri = task.getResult();
                            //String mUri = String.valueOf(taskUri);

                            db = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("imageURL", imageUrl);
                            db.updateChildren(map);
                            pd.dismiss();

                        } else {
                            Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                            ;

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        ;
                    }
                });
            }
        else{
            Toast.makeText(getContext(),"No Image",Toast.LENGTH_SHORT).show();;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == IMAGE_REQUEST && resultCode == RESULT_OK
        && data != null && data.getData() != null)
        {
            imageUri = data.getData();
            if(uploadTask != null && uploadTask.isInProgress())
            {
                Toast.makeText(getContext(),"Upload in progress",Toast.LENGTH_SHORT).show();;
            }
            else
            {
                Bitmap bmp = ImagePicker.getImageFromResult(ProfileView.getContext(), resultCode, data);
                uploadImage(bmp);
                Log.d("urlurl", imageUri.toString());
            }
                // Picasso.with(getContext()).load(imageUri).into(profilePicture);
            }
        }
    }

