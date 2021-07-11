package com.example.goforlunch.view;

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
import com.example.goforlunch.interfaces.ListItemClickListener;
import com.example.goforlunch.R;
import com.example.goforlunch.model.InfoRestaurant;
import com.example.goforlunch.model.ListInfoRestaurant;
import com.example.goforlunch.model.Place;

import java.util.List;

/**
 * Adapter for display the list of restaurants in the recycler view.
 */
public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder> {

    private List<InfoRestaurant> mInfoRestaurants;

    public RestaurantAdapter(ListInfoRestaurant infoRestaurant) {
        mInfoRestaurants = infoRestaurant.getInfoRestaurantList();
    }

    @NonNull
    @Override
    public RestaurantAdapter.RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new RestaurantViewHolder(inflater.inflate(R.layout.row_restaurant, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantAdapter.RestaurantViewHolder holder, int position) {
        holder.place = mInfoRestaurants.get(position).getPlace();
        holder.infoRestaurant = mInfoRestaurants.get(position);
        holder.setRestaurantInfo();
        holder.setLuncherInfo();
        holder.setRestaurantPicture();
        holder.setDistance();
    }

    @Override
    public int getItemCount() {
        return mInfoRestaurants.size();
    }

    public static class RestaurantViewHolder extends RecyclerView.ViewHolder {

        TextView restaurantName;
        TextView restaurantAddress;
        TextView restaurantOpen;
        TextView numberLuncher;
        TextView distanceRestaurant;
        RatingBar ratio;
        ImageView restaurantPicture;
        Place place;
        InfoRestaurant infoRestaurant;

        public RestaurantViewHolder(@NonNull View itemView) {
            super(itemView);
            restaurantName = itemView.findViewById(R.id.restaurant_name);
            restaurantAddress = itemView.findViewById(R.id.restaurant_address);
            restaurantOpen = itemView.findViewById(R.id.restaurant_open_hours);
            numberLuncher = itemView.findViewById(R.id.number_luncher);
            distanceRestaurant = itemView.findViewById(R.id.distance);
            ratio = itemView.findViewById(R.id.ratio);
            restaurantPicture = itemView.findViewById(R.id.restaurant_picture);
            itemView.setOnClickListener(view -> ((ListItemClickListener) itemView.getContext()).itemClick(place.getId()));
        }

        private String urlPhoto() {
            return place.getPhotos().get(0).getPhotoRef() + itemView.getContext().getString(R.string.google_maps_key);
        }

        public void setRestaurantInfo() {
            restaurantName.setText(place.getName());
            restaurantAddress.setText(place.getAddress().split(",")[0]);
            place.getGeometry().getCoordinate();
            if (place.getOpen() != null) {
                if (place.getOpen().isOp())
                    restaurantOpen.setText(R.string.open_now);
                else restaurantOpen.setText(R.string.close);
            }
        }

        public void setLuncherInfo() {
            numberLuncher.setText(itemView.getContext().getString(R.string.number_luncher, infoRestaurant.getHeadCount().toString()));
            int rate = infoRestaurant.getRatio();
            if (rate == 0)
                ratio.setVisibility(View.GONE);
            else {
                ratio.setNumStars(rate);
                ratio.setRating(rate);
            }
        }

        private void setDistance() {
            distanceRestaurant.setText(itemView.getContext().getString(R.string.distance, infoRestaurant.getDistance().toString()));
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
