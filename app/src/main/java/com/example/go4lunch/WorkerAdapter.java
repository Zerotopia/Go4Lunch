package com.example.go4lunch;

import android.net.Uri;
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
import com.example.go4lunch.model.User;
import com.example.go4lunch.view.PredictionAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
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
        User user = mUserList.get(position);
        holder.setUser(user);
        for (User userProfile : mUserList)
            holder.setImage(userProfile);
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
        String mImagePath = "image_profile/";

        public WorkerViewHolder(@NonNull View itemView) {
            super(itemView);
            profil = itemView.findViewById(R.id.profile_imageview);
            choice = itemView.findViewById(R.id.choice_textview);

            mFirebaseStorage = FirebaseStorage.getInstance();
        }

        public void setUser(User user) {
            choice.setText(user.getFirstName() + "lunch in" + user.getRestaurantId());
        }

        public void setImage(User user) {
            StorageReference imageref = mStorageReference.child(mImagePath + user.getPhoto());
            imageref.getDownloadUrl().addOnSuccessListener(uri -> {
                RequestOptions options = new RequestOptions()
                        .centerCrop()
                        .placeholder(R.drawable.ic_launcher_background)
                        .error(R.drawable.ic_launcher_background)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .priority(Priority.HIGH);
                Glide.with(itemView)
                        .load(uri)
                        .into(profil);
            });
        }

    }
}

