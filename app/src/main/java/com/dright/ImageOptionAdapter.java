package com.dright;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.List;

public class ImageOptionAdapter extends BaseAdapter {

    private List<Uri> imageOptionList;
    private LayoutInflater layoutInflater;
    private Context context;
    private FirebaseAuth auth;
    private FirebaseStorage fbStorage;
    private StorageReference imageRef;


    public ImageOptionAdapter(Context context, List<Uri> imageOptionList) {
        this.imageOptionList = imageOptionList;
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
        fbStorage = FirebaseStorage.getInstance();
        imageRef = fbStorage.getReference("dilema_photos");
        auth=FirebaseAuth.getInstance();
    }


    @Override
    public int getCount() {
        return imageOptionList.size();
    }

    @Override
    public Object getItem(int position) {
        return imageOptionList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.image_options_layout, null);
            holder = new ViewHolder();
            holder.image = (ImageView) convertView.findViewById(R.id.img_option_1);
            holder.imageRemove = (ImageView) convertView.findViewById(R.id.icon_remove);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Uri imageOption = this.imageOptionList.get(position);
        Glide.with(context).load(imageOption).into(holder.image);
        holder.imageRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Add image to storage
                StorageReference newImage=imageRef.child(imageOptionList.get(position).getLastPathSegment()+auth.getUid());
                newImage.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                });

                imageOptionList.remove(position);
                MyDecisionsTab.optionsList.remove(position);
                notifyDataSetChanged();

            }
        });

        return convertView;
    }
}

class ViewHolder{
    ImageView image;
    ImageView imageRemove;
}
