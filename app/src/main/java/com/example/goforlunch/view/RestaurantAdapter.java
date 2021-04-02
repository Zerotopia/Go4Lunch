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

    public RestaurantAdapter(List<Place> places, List<Double> ratioLike, List<Integer> numberOfLunchers) {
        mPlaces = places;
        mRatioLike = ratioLike;
        mNumberOfLunchers = numberOfLunchers;
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

        //Context context;
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
          //  context = itemView.getContext();
            restaurantName = itemView.findViewById(R.id.restaurant_name);
            restaurantAddress = itemView.findViewById(R.id.restaurant_address);
            restaurantOpen = itemView.findViewById(R.id.restaurant_open_hours);
            numberLuncher = itemView.findViewById(R.id.number_luncher);
            ratio = itemView.findViewById(R.id.ratio);
            restaurantPicture = itemView.findViewById(R.id.restaurant_picture);
            itemView.setOnClickListener(view -> {
               // final List<String> likers = new ArrayList<>();
//                Intent intent = new Intent(itemView.getContext(), DetailActivity.class);
//
//                RestaurantManager.getRestaurant(place.getId()).addOnSuccessListener(documentSnapshot -> {
//                   Restaurant currentRestaurant = documentSnapshot.toObject(Restaurant.class);
//                   if (currentRestaurant == null) {
//                       RestaurantManager.createRestaurant(place.getId());
//                       RestaurantManager.updateRestaurantName(place.getName(),place.getId());
//                       intent.putExtra(MapActivity.LIST_LIKERS,new ArrayList<String>());
//                   } else {
//                       intent.putExtra(MapActivity.LIST_LIKERS, (ArrayList<String>) currentRestaurant.getLikers());
//                   }
//                });
//
//
//                intent.putExtra(MapActivity.URL_IMAGE,urlPhoto());
//                intent.putExtra(MapActivity.NAME_RESTAURANT,place.getName());
//                intent.putExtra(MapActivity.UID_RESTAURANT,place.getId());
//                intent.putExtra(MapActivity.ADDR_RESTAURANT,place.getAddress());
//                Log.d("TAG", "onrestaurantclick: before detailactivity : url :" + urlPhoto() + ", namer : " + place.getName() + ", id: " + place.getId() + ", addr: " + place.getAddress());
//                itemView.getContext().startActivity(intent);
                ((ListItemClickListener) itemView.getContext()).itemClick(place.getId());
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
            numberLuncher.setText(numberOfluncher.toString());
//            Log.d("TAG", "setLuncherInfo:  ratioPlace :: " + ratioPlace);
//            Log.d("TAG", "setLuncherInfo:  ratio ::::::: " + place.getRatio());
//            Log.d("TAG", "setLuncherInfo:  result :::::: " + computeRatio(ratioPlace,place.getRatio()));
            //Integer s = computeRatio(ratioPlace,place.getRatio());
            ratio.setNumStars(computeRatio(ratioPlace, place.getRatio()));
            ratio.setRating(computeRatio(ratioPlace, place.getRatio()));
        }

        private Integer computeRatio(double ratioPlace, Double ratio) {
            double result = 3 * (0.7 * ratioPlace + 0.3 * (ratio / 5.0));
            if (result <= 0.5) return 0;
            if (result <= 1.5) return 1;
            if (result <= 2.5) return 2;
            return 3;
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
