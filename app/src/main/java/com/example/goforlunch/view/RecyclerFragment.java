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

    public static final String TAG = "TAG";
    private static final String LIST_VIEW = "LISTVIEW";
    private TextView mTextView;
    private RecyclerView mRecyclerView;
    private WorkerAdapter mWorkerAdapter;
    private List<User> mUsers;
    private boolean mList;

    private SharedPreferences mPreferences;

    private FragmentRecyclerBinding mBinding;
    private NetworkViewModel mNetworkViewModel;

    @NonNull
    public static RecyclerFragment newInstance(boolean listView) {
        RecyclerFragment recyclerFragment = new RecyclerFragment();
        Bundle arg = new Bundle();
        arg.putBoolean(LIST_VIEW, listView);
        recyclerFragment.setArguments(arg);
        return recyclerFragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mNetworkViewModel = //((MapActivity) getActivity()).getNetworkViewModel();
                ViewModelProviders.of(requireActivity(), Injection.provideNetworkViewModelFactory(getContext())).get(NetworkViewModel.class);
        Log.d(TAG, "onActivityCreated: mapfragment viewModel");
        //networkViewModel.init(mCurrentId);
        Log.d(TAG, "onActivityCreated: mapfragment init");
        observeViewModel(mNetworkViewModel);
        Log.d(TAG, "onActivityCreated: mapfragment observe");
    }

    //@Override
    public void observeViewModel(NetworkViewModel networkViewModel) {
        Log.d(TAG, "observeViewModel: mapfragment in observe");
        networkViewModel.getNetworkObservable().observe(this, this::updateNearByPlace);
        Log.d(TAG, "observeViewModel: mapgrgment fin observe");
        networkViewModel.getWorkersObservable().observe(this, this::updateWorkers);
    }

    private void updateWorkers(List<User> users) {
        mUsers = users;
        if (!mList) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            mRecyclerView.setLayoutManager(layoutManager);
            mWorkerAdapter = new WorkerAdapter(mUsers, false);
            mRecyclerView.setAdapter(mWorkerAdapter);
        }
    }

    private void updateNearByPlace(NearByPlace nearByPlace) {
        Log.d(TAG, "updnAAAAAAAAAt");
        if ((nearByPlace.getResults() != null) && (mList)) {
            Log.d(TAG, "updateNearByPlace: cool mapfragment  :: " + nearByPlace.getResults().size());
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            mRecyclerView.setLayoutManager(layoutManager);
            mRecyclerView.setAdapter(new RestaurantAdapter(nearByPlace.getResults()));
        } else Log.d(TAG, "updateNearByPlace: Hmmmm mapfragment");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_recycler, container, false);
        // View listView = inflater.inflate(R.layout.fragment_recycler, container, false);
        View listView = mBinding.getRoot();

        mRecyclerView = mBinding.fragmentRecyclerview;
        mList = (getArguments() == null) || getArguments().getBoolean(LIST_VIEW, true);
        Log.d("TAG", "onCreateView: ici");

        if (!mList)
            mRecyclerView.addItemDecoration(new CustomItemDecoration(listView.getContext()));

        // Initialize Places.
        if ((getActivity() != null) && (!Places.isInitialized())) {
            Places.initialize(getActivity().getApplicationContext(), String.valueOf(R.string.google_api_key));
            Log.d("TAG", "onCreateView: initiliaze");
        }
        return listView;
    }

    public void updateList(String newText) {
        List<User> newList = new ArrayList<>();
        if (mUsers != null) {
            for (User user : mUsers) {
                if ((user.getUserName().startsWith(newText))) {// || (user.getLastName().startsWith(newText))) {
                    newList.add(user);
                }
            }
            mWorkerAdapter = new WorkerAdapter(newList, false);
            mRecyclerView.setAdapter(mWorkerAdapter);
        }
    }
}
