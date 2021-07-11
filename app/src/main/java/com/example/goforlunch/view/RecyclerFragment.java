package com.example.goforlunch.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goforlunch.R;
import com.example.goforlunch.activity.MapActivity;
import com.example.goforlunch.databinding.FragmentRecyclerBinding;
import com.example.goforlunch.di.Injection;
import com.example.goforlunch.model.ListInfoRestaurant;
import com.example.goforlunch.model.NearByPlace;
import com.example.goforlunch.model.Place;
import com.example.goforlunch.model.User;
import com.example.goforlunch.viewmodel.NetworkViewModel;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment that manage recyclerview.
 */
public class RecyclerFragment extends Fragment {

    /**
     * Bundle TAG
     */
    private static final String LIST_VIEW = "LISTVIEW";
    private static final String LONGITUDE = "LONGITUDE";
    private static final String LATITUDE = "LATITUDE";

    /**
     * To compute distances.
     */
    private static final double EARTH_RADIUS = 6371009.0;

    private double mInitLatitude;
    private double mInitLongitude;
    private List<Integer> mDistances;

    private RecyclerView mRecyclerView;
    private WorkerAdapter mWorkerAdapter;

    private FragmentRecyclerBinding mBinding;
    private NetworkViewModel mNetworkViewModel;

    /**
     * List of workmates
     */
    private List<User> mUsers;

    /**
     * Integer that determines witch item has been selected to sort
     * the restaurants
     */
    private int mList;

    /**
     * List of the ratios of the restaurants
     */
    private List<Integer> mRatios;

    /**
     * List of the restaurants.
     */
    private NearByPlace mNearByPlace;

    /**
     * List of the number of lunchers of the restaurants
     */
    private List<Integer> mNumberOfLunchers;

    /**
     * This boolean are true when respectively
     * ratios and lunchers has been observed.
     */
    private boolean mRatioOk = false;
    private boolean mLuncherOk = false;


    @NonNull
    public static RecyclerFragment newInstance(int listView, LatLng initialposition) {
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
        setViewModel();
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        bindingView(inflater, container);
        View listView = mBinding.getRoot();
        initListInt();
        initLocation();
        initPlaces();
        return listView;
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

    /**
     * VIEWMODEL
     */

    private void setViewModel() {
        mNetworkViewModel =
                ViewModelProviders.of(requireActivity(), Injection.provideNetworkViewModelFactory(getContext())).get(NetworkViewModel.class);
        observeViewModel(mNetworkViewModel);
    }

    public void observeViewModel(NetworkViewModel networkViewModel) {
        networkViewModel.getNetworkObservable().observe(getViewLifecycleOwner(), this::updateNearByPlace);
        networkViewModel.getWorkersObservable().observe(getViewLifecycleOwner(), this::updateWorkers);
    }

    /**
     * We compute distance between the restaurants and observe ratios and number of lunchers.
     */
    private void updateNearByPlace(NearByPlace nearByPlace) {
        if ((nearByPlace.getResults() != null) && (mList != -1) && (mList != MapActivity.WORKER_FRAGMENT)) {
            mNearByPlace = nearByPlace;
            mNetworkViewModel.newPlaces(mNearByPlace);
            mDistances = restaurantDistances(mNearByPlace);
            mNetworkViewModel.getRatioObservable().observe(getViewLifecycleOwner(), this::updateRatioList);
            mNetworkViewModel.getNumberOfLuncherObservable().observe(getViewLifecycleOwner(), this::updateNumberOfLuncher);
        }
    }

    private List<Integer> restaurantDistances(NearByPlace nearByPlace) {
        List<Integer> results = new ArrayList<>();
        for (Place place : nearByPlace.getResults()) {
            results.add(distanceRestaurant(place, mInitLatitude, mInitLongitude));
        }
        return results;
    }

    public static int distanceRestaurant(Place place, double initLatitude, double initLongitude) {
        double latitude = Math.toRadians(place.getGeometry().getCoordinate().latitude);
        double longitude = Math.toRadians(place.getGeometry().getCoordinate().longitude);

        double delta_lng = initLongitude - longitude;
        double mid_lat = (initLatitude + latitude) / 2.0;

        double x_projection = delta_lng * Math.cos(mid_lat);
        double y_projection = initLatitude - latitude;

        double result = EARTH_RADIUS * Math.sqrt(x_projection * x_projection + y_projection * y_projection);
        return (int) Math.round(result);
    }

    /**
     * We display the list of wormate if the "worker fragment" is "selsected"
     *
     * @param users list of workmates.
     */
    private void updateWorkers(List<User> users) {
        mUsers = users;
        if (mList == MapActivity.WORKER_FRAGMENT) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            mRecyclerView.setLayoutManager(layoutManager);
            mWorkerAdapter = new WorkerAdapter(mUsers, false);
            mRecyclerView.addItemDecoration(new CustomItemDecoration(requireContext()));
            mRecyclerView.setAdapter(mWorkerAdapter);
        }
    }

    private void updateRatioList(List<Integer> ratios) {
        mRatios = ratios;
        mRatioOk = true;
        setRecyclerView();
    }

    private void updateNumberOfLuncher(List<Integer> integers) {
        mNumberOfLunchers = integers;
        mLuncherOk = true;
        setRecyclerView();
    }

    /**
     * display the list of the restaurants depending of the choice of the
     * user about the sorted preferences
     */
    private void setRecyclerView() {
        if (mRatioOk && mLuncherOk) {
            ListInfoRestaurant infoRestaurant = new ListInfoRestaurant(mNearByPlace.getResults(), mRatios, mNumberOfLunchers, mDistances);
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

    /**
     * Function that update the list of wormates for the Search features.
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
}
