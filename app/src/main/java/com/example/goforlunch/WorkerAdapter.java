package com.example.goforlunch;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.goforlunch.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class WorkerAdapter extends RecyclerView.Adapter<WorkerAdapter.WorkerViewHolder> {

    private List<User> mUserList;

    public WorkerAdapter(List<User> userList) {
        mUserList = userList;
    }

    @NonNull
    @Override
    public WorkerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new WorkerAdapter.WorkerViewHolder(inflater.inflate(R.layout.row_worker, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull WorkerViewHolder holder, int position) {
        Log.d("TAG", "onBindViewHolder: size list : " + mUserList.size());
        Log.d("TAG", "onBindViewHolder: position : " + position);
        User user = mUserList.get(position);
        Log.d("TAG", "onBindViewHolder: user :  " + user.getFirstName());
        holder.setUser(user);
        Log.d("TAG", "onBindViewHolder:  setUser done");
        holder.setImage(user);
        Log.d("TAG", "onBindViewHolder:  setImage done ");
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }

    public static class WorkerViewHolder extends RecyclerView.ViewHolder {

        private final ImageView profil;
        private final TextView choice;

        FirebaseStorage mFirebaseStorage;
        StorageReference mStorageReference;
        String mImagePath = "image_profil/";

        public WorkerViewHolder(@NonNull View itemView) {
            super(itemView);
            profil = itemView.findViewById(R.id.profile_imageview);
            choice = itemView.findViewById(R.id.choice_textview);

            mFirebaseStorage = FirebaseStorage.getInstance();
            mStorageReference = mFirebaseStorage.getReference();
        }

        public void setUser(User user) {
            choice.setText(user.getFirstName() + " lunch in" + user.getRestaurantId());
        }

        public void setImage(User user) {
            Log.d("TAG", "setImage: " + mImagePath + user.getPhoto());
            StorageReference imageref = mStorageReference.child(mImagePath + user.getPhoto());
            Log.d("TAG", "setImage: after imgeref before download ");
            imageref.getDownloadUrl().addOnSuccessListener(uri -> {
                Log.d("TAG", "setImage:  addonsucces");
                RequestOptions options = new RequestOptions()
                        .centerCrop()
                        .placeholder(R.drawable.ic_launcher_background)
                        .error(R.drawable.ic_launcher_background)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .priority(Priority.HIGH);
                Glide.with(itemView)
                        .setDefaultRequestOptions(options)
                        .load(uri)
                        .apply(RequestOptions.circleCropTransform())
                        .into(profil);
            }).addOnFailureListener(e -> {
                Log.d("TAG", "setImage:  fail : " + e.getMessage());
            });
            Log.d("TAG", "setImage:  end");
        }
    }
}

