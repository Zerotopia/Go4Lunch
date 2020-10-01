package com.example.goforlunch.view;

import android.content.Context;
import android.content.Intent;
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
import com.example.goforlunch.DetailActivity;
import com.example.goforlunch.MapActivity;
import com.example.goforlunch.R;
import com.example.goforlunch.RestaurantManager;
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
        holder.place = place;
        holder.setRestaurantInfo();
        holder.setLuncherInfo();
        holder.setRestaurantPicture();

    }

    @Override
    public int getItemCount() {
        return mPlaces.size();
    }

    public class RestaurantViewHolder extends RecyclerView.ViewHolder {

        //Context context;
        TextView restaurantName;
        TextView restaurantAddress;
        TextView restaurantOpen;
        TextView numberLuncher;
        TextView ratio;
        ImageView restaurantPicture;
        Place place;

        public RestaurantViewHolder(@NonNull View itemView) {
            super(itemView);
          //  context = itemView.getContext();
            restaurantName = itemView.findViewById(R.id.restaurant_name);
            restaurantAddress = itemView.findViewById(R.id.restaurant_address);
            restaurantOpen = itemView.findViewById(R.id.restaurant_open_hours);
            numberLuncher = itemView.findViewById(R.id.number_luncher);
            ratio = itemView.findViewById(R.id.ratio);
            restaurantPicture = itemView.findViewById(R.id.restaurant_picture);
            itemView.setOnClickListener(view -> {
                RestaurantManager.createRestaurant(place.getId());
                Intent intent = new Intent(itemView.getContext(), DetailActivity.class);
                intent.putExtra(MapActivity.URL_IMAGE,urlPhoto());
                intent.putExtra(MapActivity.NAME_RESTAURANT,place.getName());
                intent.putExtra(MapActivity.UID_RESTAURANT,place.getId());
                intent.putExtra(MapActivity.ADDR_RESTAURANT,place.getAddress());
                itemView.getContext().startActivity(intent);
            });
        }

        private String urlPhoto(){
            return place.getPhotos().get(0).getPhotoRef() + itemView.getContext().getString(R.string.google_maps_key);
        }

        public void setRestaurantInfo () {
            restaurantName.setText(place.getName());
            restaurantAddress.setText(place.getAddress());
            if (place.getOpen() != null) {
             if (place.getOpen().isOp())
                restaurantOpen.setText("Open Now");
            else restaurantOpen.setText("Close");
            }
        }

        public void setLuncherInfo () {
            numberLuncher.setText("2");
            ratio.setText(place.getRatio().toString());
        }

        public void setRestaurantPicture () {
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
