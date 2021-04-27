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
import com.example.goforlunch.databinding.FragmentRecyclerBinding;
import com.example.goforlunch.di.Injection;
import com.example.goforlunch.model.NearByPlace;
import com.example.goforlunch.model.User;
import com.example.goforlunch.viewmodel.NetworkViewModel;
import com.google.android.libraries.places.api.Places;

import java.util.ArrayList;
import java.util.List;

//import static com.example.goforlunch.view.PredictionAdapter.TAG;

public class RecyclerFragment extends Fragment {

    public static final String TAG = "RECYCLERFRAGMENTTAG";
    private static final String LIST_VIEW = "LISTVIEW";
    private TextView mTextView;
    private RecyclerView mRecyclerView;
    private WorkerAdapter mWorkerAdapter;
    private List<User> mUsers;
    private boolean mList;

    private SharedPreferences mPreferences;

    private FragmentRecyclerBinding mBinding;
    private NetworkViewModel mNetworkViewModel;

    private List<Double> mRatioLike;
    private NearByPlace mNearByPlace;
    private List<Integer> mNumberOfLunchers;

    private boolean mRatioOk = false;
    private boolean mLuncherOk = false;

    @NonNull
    public static RecyclerFragment newInstance(boolean listView) {
        Log.d(TAG, "newInstance: Creation Instance");
        RecyclerFragment recyclerFragment = new RecyclerFragment();
        Bundle arg = new Bundle();
        arg.putBoolean(LIST_VIEW, listView);
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
        initListBoolean();
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
        if ((nearByPlace.getResults() != null) && (mList)) {
            mNearByPlace = nearByPlace;
            mNetworkViewModel.newPlaces(mNearByPlace);
            Log.d(TAG, "updateNearByPlace: observeratiolist");
            mNetworkViewModel.getRatioObservable().observe(getViewLifecycleOwner(), this::updateRatioList);
            Log.d(TAG, "updateNearByPlace: observe NumberofLuncher");
            mNetworkViewModel.getNumberOfLuncherObservable().observe(getViewLifecycleOwner(), this::updateNumberOfLuncher);
        }
        Log.d(TAG, "updateNearByPlace: exit updatenearbyplace");
    }

    private void updateWorkers(List<User> users) {
        Log.d(TAG, "updateWorkers: updateworker");
        mUsers = users;
        if (!mList) {
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
                if ((user.getUserName().startsWith(newText))) {// || (user.getLastName().startsWith(newText))) {
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
            mRatioOk = false;
            mLuncherOk = false;
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            mRecyclerView.setLayoutManager(layoutManager);
            mRecyclerView.setAdapter(new RestaurantAdapter(mNearByPlace.getResults(), mRatioLike, mNumberOfLunchers));
        }
    }

    private void bindingView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_recycler, container, false);
        mRecyclerView = mBinding.fragmentRecyclerview;
    }

    private void initListBoolean() {
        mList = (getArguments() == null) || getArguments().getBoolean(LIST_VIEW, true);
    }

    private void initPlaces() {
        if ((getActivity() != null) && (!Places.isInitialized())) {
            Places.initialize(getActivity().getApplicationContext(), String.valueOf(R.string.google_api_key));
        }
    }
}
