package com.example.goforlunch.view;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.goforlunch.ListItemClickListener;
import com.example.goforlunch.R;
import com.example.goforlunch.model.Place;

import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder> {

    private List<Place> mPlaces;
    private List<Double> mRatioLike;
    private List<Integer> mNumberOfLunchers;

    private String TAG = "ADAPTERTAGADAPTERTAGADAPTERTAG";

    public RestaurantAdapter(List<Place> places, List<Double> ratioLike, List<Integer> numberOfLunchers) {
        mPlaces = places;
        mRatioLike = ratioLike;
        mNumberOfLunchers = numberOfLunchers;
    }

    @NonNull
    @Override
    public RestaurantAdapter.RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new RestaurantAdapter.RestaurantViewHolder(inflater.inflate(R.layout.row_restaurant, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantAdapter.RestaurantViewHolder holder, int position) {
        Place place = mPlaces.get(position);
        double ratioPlace = mRatioLike.get(position);
        Integer numberOfLuncher = mNumberOfLunchers.get(position);
        holder.place = place;
        holder.ratioPlace = ratioPlace;
        holder.numberOfluncher = numberOfLuncher;
        holder.setRestaurantInfo();
        holder.setLuncherInfo();
        holder.setRestaurantPicture();

    }

    @Override
    public int getItemCount() {
        return mPlaces.size();
    }

    public class RestaurantViewHolder extends RecyclerView.ViewHolder {

        TextView restaurantName;
        TextView restaurantAddress;
        TextView restaurantOpen;
        TextView numberLuncher;
        RatingBar ratio;
        ImageView restaurantPicture;
        Place place;
        double ratioPlace;
        Integer numberOfluncher;

        public RestaurantViewHolder(@NonNull View itemView) {
            super(itemView);
            restaurantName = itemView.findViewById(R.id.restaurant_name);
            restaurantAddress = itemView.findViewById(R.id.restaurant_address);
            restaurantOpen = itemView.findViewById(R.id.restaurant_open_hours);
            numberLuncher = itemView.findViewById(R.id.number_luncher);
            ratio = itemView.findViewById(R.id.ratio);
            restaurantPicture = itemView.findViewById(R.id.restaurant_picture);
            itemView.setOnClickListener(view -> {
                ((ListItemClickListener) itemView.getContext()).itemClick(place.getId());
            });
        }

        private String urlPhoto() {
            return place.getPhotos().get(0).getPhotoRef() + itemView.getContext().getString(R.string.google_maps_key);
        }

        public void setRestaurantInfo() {
            restaurantName.setText(place.getName());
            restaurantAddress.setText(place.getAddress().split(",")[0]);
            if (place.getOpen() != null) {
                if (place.getOpen().isOp())
                    restaurantOpen.setText("Open Now");
                else restaurantOpen.setText("Close");
            }
        }

        public void setLuncherInfo() {
            numberLuncher.setText("(" + numberOfluncher.toString() + ")");
            int rate = computeRatio(ratioPlace, place.getRatio());
            Log.d(TAG, "setLuncherInfo: rate = " + rate);
            if (rate == 0)
                ratio.setVisibility(View.GONE);
            else {
                ratio.setNumStars(rate);
                ratio.setRating(rate);
            }
        }

        private Integer computeRatio(double ratioPlace, Double ratio) {
            double result = 3 * (0.7 * ratioPlace + 0.3 * (ratio / 5.0));
            if (result <= 0.5) return 0;
            if (result <= 1.5) return 1;
            if (result <= 2.5) return 2;
            return 3;
        }

        public void setRestaurantPicture() {
            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .priority(Priority.HIGH);
            if (place.getPhotos() != null) {
                if ((place.getPhotos().get(0).getPhotoRef() != null))
                    Glide.with(itemView)
                            .setDefaultRequestOptions(options)
                            .load(urlPhoto())
                            .into(restaurantPicture);
            }
        }
    }
}
