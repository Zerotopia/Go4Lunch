package com.example.goforlunch.view;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goforlunch.R;
import com.example.goforlunch.WorkerAdapter;
import com.example.goforlunch.activity.MapActivity;
import com.example.goforlunch.databinding.FragmentRecyclerBinding;
import com.example.goforlunch.di.Injection;
import com.example.goforlunch.model.InfoRestaurant;
import com.example.goforlunch.model.ListInfoRestaurant;
import com.example.goforlunch.model.NearByPlace;
import com.example.goforlunch.model.Place;
import com.example.goforlunch.model.User;
import com.example.goforlunch.viewmodel.NetworkViewModel;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//import static com.example.goforlunch.view.PredictionAdapter.TAG;

public class RecyclerFragment extends Fragment {

    public static final String TAG = "RECYCLERFRAGMENTTAG";
    private static final String LIST_VIEW = "LISTVIEW";
    private static final String LONGITUDE = "LONGITUDE";
    private static final String LATITUDE = "LATITUDE";
    private static final double EARTH_RADIUS = 6371009.0;

    private TextView mTextView;
    private RecyclerView mRecyclerView;
    private WorkerAdapter mWorkerAdapter;
    private List<User> mUsers;
    private int mList;

    private SharedPreferences mPreferences;

    private FragmentRecyclerBinding mBinding;
    private NetworkViewModel mNetworkViewModel;

    private List<Double> mRatioLike;
    private List<Integer> mRatios;
    private NearByPlace mNearByPlace;
    private List<Integer> mNumberOfLunchers;

    private boolean mRatioOk = false;
    private boolean mLuncherOk = false;

    private double mInitLatitude;
    private double mInitLongitude;
    private List<Integer> mDistances;

    @NonNull
    public static RecyclerFragment newInstance(int listView, LatLng initialposition) {
        Log.d(TAG, "newInstance: Creation Instance");
        RecyclerFragment recyclerFragment = new RecyclerFragment();
        Bundle arg = new Bundle();
        arg.putInt(LIST_VIEW, listView);
        arg.putDouble(LONGITUDE, Math.toRadians(initialposition.longitude));
        arg.putDouble(LATITUDE, Math.toRadians(initialposition.latitude));
        recyclerFragment.setArguments(arg);
        return recyclerFragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated: setviewmodel creation de l'activité");
        setViewModel();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: setOptionMenu");
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: creation de la vue");
        bindingView(inflater, container);
        Log.d(TAG, "onCreateView: binding stuff");
        View listView = mBinding.getRoot();
        Log.d(TAG, "onCreateView: initlistboolean");
        initListInt();
        initLocation();
        Log.d(TAG, "onCreateView: initPlace");
        initPlaces();
        Log.d(TAG, "onCreateView: exit oncreated");
        return listView;
    }

    /**
     * VIEWMODEL OBSERVERS
     */
    public void observeViewModel(NetworkViewModel networkViewModel) {
        Log.d(TAG, "observeViewModel: observe");
        Log.d(TAG, "observeViewModel: observe nearbyplace");
        networkViewModel.getNetworkObservable().observe(getViewLifecycleOwner(), this::updateNearByPlace);
        Log.d(TAG, "observeViewModel: observe workers");
        networkViewModel.getWorkersObservable().observe(getViewLifecycleOwner(), this::updateWorkers);
        Log.d(TAG, "observeViewModel: exit observe");
    }

    private void updateNearByPlace(NearByPlace nearByPlace) {
        Log.d(TAG, "updateNearByPlace: updatenearbyplace");
        if ((nearByPlace.getResults() != null) && (mList != -1) && (mList != MapActivity.WORKER_FRAGMENT)) {
            mNearByPlace = nearByPlace;
            mNetworkViewModel.newPlaces(mNearByPlace);
            Log.d(TAG, "updateNearByPlace: observeratiolist");
            mDistances = restaurantDistances(mNearByPlace);
            mNetworkViewModel.getRatioObservable().observe(getViewLifecycleOwner(), this::updateRatioList);
            Log.d(TAG, "updateNearByPlace: observe NumberofLuncher");
            mNetworkViewModel.getNumberOfLuncherObservable().observe(getViewLifecycleOwner(), this::updateNumberOfLuncher);
        }
        Log.d(TAG, "updateNearByPlace: exit updatenearbyplace");
    }

    private List<Integer> restaurantDistances(NearByPlace nearByPlace) {
        List<Integer> results = new ArrayList<>();
        for (Place place : nearByPlace.getResults()) {
            results.add(distanceRestaurant(place));
        }
        return results;
    }

    private int distanceRestaurant(Place place) {
        double latitude = Math.toRadians(place.getGeometry().getCoordinate().latitude);
        double longitude = Math.toRadians(place.getGeometry().getCoordinate().longitude);

        double delta_lng = mInitLongitude - longitude;
        double mid_lat = (mInitLatitude + latitude) / 2.0;

        double x_projection = delta_lng * Math.cos(mid_lat);
        double y_projection = mInitLatitude - latitude;

        double result = EARTH_RADIUS * Math.sqrt(x_projection * x_projection + y_projection * y_projection);
        return (int) Math.round(result);
    }

    private void updateWorkers(List<User> users) {
        Log.d(TAG, "updateWorkers: updateworker");
        mUsers = users;
        if (mList == MapActivity.WORKER_FRAGMENT) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            mRecyclerView.setLayoutManager(layoutManager);
            mWorkerAdapter = new WorkerAdapter(mUsers, false);
            mRecyclerView.addItemDecoration(new CustomItemDecoration(requireContext()));
            mRecyclerView.setAdapter(mWorkerAdapter);
        }
        Log.d(TAG, "updateWorkers: exit updateworker");
    }

    private void updateRatioList(List<Double> ratios) {
        Log.d(TAG, "updateRatioList: ");
        mRatioLike = ratios;
        computeRatioList(mRatioLike);
        mRatioOk = true;
        setRecyclerView();
        Log.d(TAG, "updateRatioList: exit ratiolist");
    }




    private void updateNumberOfLuncher(List<Integer> integers) {
        Log.d(TAG, "updateNumberOfLuncher: ");
        mNumberOfLunchers = integers;
        mLuncherOk = true;
        setRecyclerView();
        Log.d(TAG, "updateNumberOfLuncher: exit updateNumberofLuncher");
    }
/*******************************************************************************/
    /**
     * annexe function
     */
    public void updateList(String newText) {
        List<User> newList = new ArrayList<>();
        if (mUsers != null) {
            for (User user : mUsers) {
                if ((user.getUserName().startsWith(newText)) || (user.getLastName().startsWith(newText)) || (user.getFirstName().startsWith(newText))) {// || (user.getLastName().startsWith(newText))) {
                    newList.add(user);
                }
            }
            mWorkerAdapter = new WorkerAdapter(newList, false);
            mRecyclerView.addItemDecoration(new CustomItemDecoration(requireContext()));
            mRecyclerView.setAdapter(mWorkerAdapter);
        }
    }

    private void setViewModel() {
        mNetworkViewModel =
                ViewModelProviders.of(requireActivity(), Injection.provideNetworkViewModelFactory(getContext())).get(NetworkViewModel.class);
        observeViewModel(mNetworkViewModel);
    }

    private void setRecyclerView() {
       // if (!mList)

        if (mRatioOk && mLuncherOk) {
            ListInfoRestaurant infoRestaurant = new ListInfoRestaurant(mNearByPlace.getResults(),mRatios,mNumberOfLunchers,mDistances);
            switch (mList) {
                case MapActivity.SORT_NAME_LIST:
                    infoRestaurant.sortByNames();
                    break;
                case MapActivity.SORT_RATIO_LIST:
                    infoRestaurant.sortByRatios();
                    break;
                case MapActivity.SORT_HEADCOUNT_LIST:
                    infoRestaurant.sortByHeadCounts();
                    break;
                case MapActivity.SORT_DISTANCE_LIST:
                    infoRestaurant.sortByDistances();
                    break;
                default:
            }
            mRatioOk = false;
            mLuncherOk = false;
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            mRecyclerView.setLayoutManager(layoutManager);
            mRecyclerView.setAdapter(new RestaurantAdapter(infoRestaurant));
            }

    }

    private void bindingView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_recycler, container, false);
        mRecyclerView = mBinding.fragmentRecyclerview;
    }

    private void initListInt() {
        mList = (getArguments() == null) ? -1 : getArguments().getInt(LIST_VIEW, -1);
    }

    private void initLocation() {
        if (getArguments() != null) {
            mInitLatitude = getArguments().getDouble(LATITUDE);
            mInitLongitude = getArguments().getDouble(LONGITUDE);
        }
    }
    private void initPlaces() {
        if ((getActivity() != null) && (!Places.isInitialized())) {
            Places.initialize(getActivity().getApplicationContext(), String.valueOf(R.string.google_api_key));
        }
    }

    private void computeRatioList(List<Double> ratioLike) {
        mRatios = new ArrayList<>();
        List<Place> places = mNearByPlace.getResults();
        for (int i=0; i < ratioLike.size(); i++) {
            mRatios.add(computeRatio(ratioLike.get(i),places.get(i).getRatio()));
        }
    }

    private Integer computeRatio(double ratioPlace, Double ratio) {
        double result = 3 * (0.7 * ratioPlace + 0.3 * (ratio / 5.0));
        if (result <= 0.5) return 0;
        if (result <= 1.5) return 1;
        if (result <= 2.5) return 2;
        return 3;
    }

//    public static List<InfoRestaurant> listToSort (List<String> names, List<Double> ratios, List<Integer> headCount, List<Integer> distances) {
//        List<InfoRestaurant> result = new ArrayList<>();
//        for (int i = 0; i < names.size(); i ++)
//            result.add(new InfoRestaurant(names.get(i), ratios.get(i), headCount.get(i), distances.get(i)));
//        return result;
//    }


//    public interface LocationListener {
//        List<Integer> restaurantDistances (List <Place);
//    }
//
//    @Override
//    public void updateUI(NearByPlace nearbyPlace, List<String> reservedRestaurant) {
//
//    }
}
