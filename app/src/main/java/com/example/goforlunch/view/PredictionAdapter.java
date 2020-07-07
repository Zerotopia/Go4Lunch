package com.example.goforlunch.view;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goforlunch.R;
import com.google.android.libraries.places.api.model.AutocompletePrediction;

import java.util.ArrayList;
import java.util.List;

public class PredictionAdapter extends RecyclerView.Adapter<PredictionAdapter.PredictionViewHolder> {

    private final List<AutocompletePrediction> predictions = new ArrayList<>();

    private OnPlaceClickListener onPlaceClickListener;
    public static final String TAG = "TAG";
    
    @NonNull
    @Override
    public PredictionAdapter.PredictionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        Log.d(TAG, "onCreateViewHolder: predictionAdapter");
        return new PredictionViewHolder(inflater.inflate(R.layout.place_prediction,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull PredictionAdapter.PredictionViewHolder holder, int position) {
        final AutocompletePrediction prediction = predictions.get(position);
        holder.setPrediction(prediction);
        holder.itemView.setOnClickListener(v -> {
            if (onPlaceClickListener != null) {
                onPlaceClickListener.onPlaceClicked(prediction);
            }
        });
        Log.d(TAG, "onBindViewHolder: predicitonAdapter");
    }

    @Override
    public int getItemCount() {
        return predictions.size();
    }

    public void setPredictions(List<AutocompletePrediction> predictions) {
        this.predictions.clear();
        this.predictions.addAll(predictions);
        notifyDataSetChanged();
    }

    public static class PredictionViewHolder extends  RecyclerView.ViewHolder{
        private final TextView title;
        private final TextView address;

        public PredictionViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.text_view_title);
            address = itemView.findViewById(R.id.text_view_address);
        }

        public void setPrediction(AutocompletePrediction prediction) {
            title.setText(prediction.getPrimaryText(null));
            address.setText(prediction.getSecondaryText(null));
        }
    }


    interface OnPlaceClickListener {
        void onPlaceClicked(AutocompletePrediction place);
    }
}
