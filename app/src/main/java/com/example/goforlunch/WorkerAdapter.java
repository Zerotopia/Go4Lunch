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
import com.example.goforlunch.databinding.RowWorkerBinding;
import com.example.goforlunch.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class WorkerAdapter extends RecyclerView.Adapter<WorkerAdapter.WorkerViewHolder> {

    private List<User> mUserList;
    private boolean mDetail;

    public WorkerAdapter(List<User> userList, boolean detail) {
        mUserList = userList;
        mDetail = detail;
    }

    @NonNull
    @Override
    public WorkerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RowWorkerBinding binding = RowWorkerBinding.inflate(inflater,parent,false);
        return new WorkerAdapter.WorkerViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkerViewHolder holder, int position) {
        User user = mUserList.get(position);
        holder.bindUser(user, mDetail);
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }

    public static class WorkerViewHolder extends RecyclerView.ViewHolder {

        private RowWorkerBinding mBinding;

        public WorkerViewHolder(RowWorkerBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        public void bindUser (User user, Boolean detail) {
            mBinding.setUser(user);
            mBinding.setList(detail);
            mBinding.executePendingBindings();
        }
    }
}

