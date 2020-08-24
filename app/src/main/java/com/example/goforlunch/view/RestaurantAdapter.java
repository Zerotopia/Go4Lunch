package com.example.goforlunch.view;

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
import com.example.goforlunch.R;
import com.example.goforlunch.WorkerAdapter;
import com.example.goforlunch.model.NearByPlace;
import com.example.goforlunch.model.Place;
import com.example.goforlunch.repository.NetworkRepository;

import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder> {

    private List<Place> mPlaces;

    public RestaurantAdapter(List<Place> places) {
        mPlaces = places;
    }

    @NonNull
    @Override
    public RestaurantAdapter.RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new RestaurantAdapter.RestaurantViewHolder(inflater.inflate(R.layout.row_restaurant,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantAdapter.RestaurantViewHolder holder, int position) {
        Place place = mPlaces.get(position);
        holder.setRestaurantInfo(place);
        holder.setLuncherInfo(place);
        holder.setRestaurantPicture(place);

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
        TextView ratio;
        ImageView restaurantPicture;


        public RestaurantViewHolder(@NonNull View itemView) {
            super(itemView);
            restaurantName = itemView.findViewById(R.id.restaurant_name);
            restaurantAddress = itemView.findViewById(R.id.restaurant_address);
            restaurantOpen = itemView.findViewById(R.id.restaurant_open_hours);
            numberLuncher = itemView.findViewById(R.id.number_luncher);
            ratio = itemView.findViewById(R.id.ratio);
            restaurantPicture = itemView.findViewById(R.id.restaurant_picture);
        }

        public void setRestaurantInfo (Place place) {
            restaurantName.setText(place.getName());
            restaurantAddress.setText(place.getAddress());
            restaurantOpen.setText("Open");
        }

        public void setLuncherInfo (Place place) {
            numberLuncher.setText("2");
            ratio.setText("2.87");
        }

        public void setRestaurantPicture (Place place) {
            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .priority(Priority.HIGH);
            Glide.with(itemView)
                    .setDefaultRequestOptions(options)
                    .load(place.getIconUrl())
                    .into(restaurantPicture);
        }
    }
}
