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
import com.example.goforlunch.model.InfoRestaurant;
import com.example.goforlunch.model.ListInfoRestaurant;
import com.example.goforlunch.model.Place;

import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder> {

    //private List<Place> mPlaces;
    private List<InfoRestaurant> mInfoRestaurants;
    //private List<Double> mRatioLike;
    //private List<Integer> mNumberOfLunchers;
    //private List<Integer> mDistances;

    private String TAG = "ADAPTERTAGADAPTERTAGADAPTERTAG";

    public RestaurantAdapter(ListInfoRestaurant infoRestaurant) {
      //  mPlaces = places;
        mInfoRestaurants = infoRestaurant.getInfoRestaurantList();
      //  mRatioLike = ratioLike;
      //  mNumberOfLunchers = numberOfLunchers;
      //  mDistances = distances;
    }

    @NonNull
    @Override
    public RestaurantAdapter.RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new RestaurantAdapter.RestaurantViewHolder(inflater.inflate(R.layout.row_restaurant, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantAdapter.RestaurantViewHolder holder, int position) {
        //Place place = mPlaces.get(position);
        // double ratioPlace = mRatioLike.get(position);
        //Integer numberOfLuncher = mNumberOfLunchers.get(position);
        holder.place = mInfoRestaurants.get(position).getPlace();
        holder.infoRestaurant = mInfoRestaurants.get(position);
        //holder.ratioPlace = mRatioLike.get(position);
        //holder.numberOfluncher = mNumberOfLunchers.get(position);
        //holder.distance = mDistances.get(position);
        holder.setRestaurantInfo();
        holder.setLuncherInfo();
        holder.setRestaurantPicture();
        holder.setDistance();
    }

    @Override
    public int getItemCount() {
        return mInfoRestaurants.size();
    }

    public class RestaurantViewHolder extends RecyclerView.ViewHolder {


        TextView restaurantName;
        TextView restaurantAddress;
        TextView restaurantOpen;
        TextView numberLuncher;
        TextView distanceRestaurant;
        RatingBar ratio;
        ImageView restaurantPicture;
        Place place;
        InfoRestaurant infoRestaurant;
        //double ratioPlace;
        //Integer numberOfluncher;
        //Integer distance;

        public RestaurantViewHolder(@NonNull View itemView) {
            super(itemView);
            restaurantName = itemView.findViewById(R.id.restaurant_name);
            restaurantAddress = itemView.findViewById(R.id.restaurant_address);
            restaurantOpen = itemView.findViewById(R.id.restaurant_open_hours);
            numberLuncher = itemView.findViewById(R.id.number_luncher);
            distanceRestaurant = itemView.findViewById(R.id.distance);
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
            place.getGeometry().getCoordinate();
            if (place.getOpen() != null) {
                if (place.getOpen().isOp())
                    restaurantOpen.setText(R.string.open_now);
                else restaurantOpen.setText(R.string.close);
            }
        }

        public void setLuncherInfo() {
            numberLuncher.setText("(" + infoRestaurant.getHeadCount().toString() + ")");
            int rate = infoRestaurant.getRatio();
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

        private void setDistance() {
            distanceRestaurant.setText(infoRestaurant.getDistance().toString() + "m");
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
