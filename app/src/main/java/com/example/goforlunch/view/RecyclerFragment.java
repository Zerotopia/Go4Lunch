package com.example.goforlunch.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.appcompat.widget.SearchView;

import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goforlunch.MapActivity;
import com.example.goforlunch.R;
import com.example.goforlunch.UserManager;
import com.example.goforlunch.WorkerAdapter;
import com.example.goforlunch.di.Injection;
import com.example.goforlunch.model.NearByPlace;
import com.example.goforlunch.model.Place;
import com.example.goforlunch.model.User;
import com.example.goforlunch.viewmodel.NetworkViewModel;
import com.example.goforlunch.viewmodel.PredictionViewModel;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import static com.example.goforlunch.view.PredictionAdapter.TAG;

public class RecyclerFragment extends Fragment {

    private static final String LIST_VIEW = "LISTVIEW";
    private TextView mTextView;
    private RecyclerView mRecyclerView;

    private String[] mListName;
    private AutoCompleteTextView mAutoCompleteTextView;

    private Handler handler = new Handler();
    private PlacesClient placesClient;
    private AutocompleteSessionToken sessionToken;
    private PredictionAdapter adapter = new PredictionAdapter();
    private WorkerAdapter mWorkerAdapter;
    private PredictionViewModel mPredictionViewModel;
    private List<User> mUsers;
    private boolean mList;

    private AdapterListener mAdapterListener;
    private SharedPreferences mPreferences;
    private String mCurrentId;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
//        mAdapterListener = (AdapterListener) context;
    }

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

        mPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        mCurrentId = mPreferences.getString(MapActivity.CURRENTID, "");
        Log.d(TAG, "onActivityCreated: ID" + mCurrentId);

        final NetworkViewModel networkViewModel =
                ViewModelProviders.of(this, Injection.provideNetworkViewModelFactory(getContext())).get(NetworkViewModel.class);
        Log.d(TAG, "onActivityCreated: mapfragment viewModel");
        networkViewModel.init();
        Log.d(TAG, "onActivityCreated: mapfragment init");
        observeViewModel(networkViewModel);
        Log.d(TAG, "onActivityCreated: mapfragment observe");
//        mPredictionViewModel =
//                ViewModelProviders.of(this, Injection.provideNetworkViewModelFactory(getContext())).get(PredictionViewModel.class);
//        mPredictionViewModel.init();
//        observeViewModel();
    }

    private void observeViewModel(NetworkViewModel networkViewModel) {
        Log.d(TAG, "observeViewModel: mapfragment in observe");
        networkViewModel.getNetworkObservable().observe(this, this::updateNearByPlace);
        Log.d(TAG, "observeViewModel: mapgrgment fin observe");
    }

    private void updateNearByPlace(NearByPlace nearByPlace) {
        Log.d(TAG, "updnAAAAAAAAAt");
        if ((nearByPlace.getResults() != null) && (mList)){
            Log.d(TAG, "updateNearByPlace: cool mapfragment  :: " + nearByPlace.getResults().size());
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            mRecyclerView.setLayoutManager(layoutManager);
            mRecyclerView.setAdapter(new RestaurantAdapter(nearByPlace.getResults()));
        }
        else Log.d(TAG, "updateNearByPlace: Hmmmm mapfragment");
    }
    //
//    private void observeViewModel() {
//        mPredictionViewModel.getPredictionObservable().observe(this, this::updateRestaurant);
//        Log.d("TAG", "observeViewModel: nameobserve");
//    }
//
//    private void updateRestaurant(List<AutocompletePrediction> predictions) {
//        Log.d("TAG", "updateRestaurant: " + predictions.size());
//        adapter.setPredictions(predictions);
//    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View listView = inflater.inflate(R.layout.fragment_recycler, container, false);
        mTextView = listView.findViewById(R.id.textfrag);
        mRecyclerView = listView.findViewById(R.id.fragment_recyclerview);
        mList = (getArguments() == null) || getArguments().getBoolean(LIST_VIEW, true);
        Log.d("TAG", "onCreateView: ici");
        if (mList) mTextView.setText("List View");
        else {
            mTextView.setText("List Worker");
            Log.d("TAG", "onCreateView: là ");
            UserManager.getAllUser().addOnSuccessListener(documentSnapshots -> {
                mUsers = new ArrayList<>();
                for (DocumentSnapshot document : documentSnapshots.getDocuments()) {
                    if (!document.getId().equals(mCurrentId)) {
                        Log.d(TAG, "onCreateView: fordoc " + document.getId());
                        User user = document.toObject(User.class);
                        mUsers.add(user);
                    }
                }
                Log.d("TAG", "onCreateView: success");

               // mUsers = documentSnapshots.toObjects(User.class);
                // mAdapterListener.setSearchViewAdapter(mUsers,mRecyclerView);
                //Log.d("TAG", "onCreateView: " + mUsers.size());
                //for (User user : mUsers) {
                //    Log.d("TAG", "onCreateView: " + user.getEmail());
                //}
                Log.d("TAG", "onCreateView: muserok");
                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                mRecyclerView.setLayoutManager(layoutManager);
                mWorkerAdapter = new WorkerAdapter(mUsers, false);
                mRecyclerView.setAdapter(mWorkerAdapter);

                while (mRecyclerView.getItemDecorationCount() > 0) {
                    mRecyclerView.removeItemDecorationAt(0);
                }
                mRecyclerView.addItemDecoration(new CustomItemDecoration(listView.getContext()));
            }).addOnFailureListener(e -> {
                Log.d("TAG", "onCreateView: fail " + e.getMessage());
            });

        }

//         mAutoCompleteTextView = listView.findViewById(R.id.autocomplete);
//        if (mListName != null) {
//            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(listView.getContext(), R.layout.autocompletion, mListName);
//            Log.d("TAG", "onCreateView: namenotnull");
//            mAutoCompleteTextView.setAdapter(arrayAdapter);
//        } else Log.d("TAG", "onCreateView: nulll" );
        // Initialize Places.
        if ((getActivity() != null) && (!Places.isInitialized())) {
            Places.initialize(getActivity().getApplicationContext(), String.valueOf(R.string.google_api_key));
            Log.d("TAG", "onCreateView: initiliaze");
        }
        // Create a new Places client instance.
//        if (listView.getContext() != null) {
//            placesClient = Places.createClient(listView.getContext());
//        }
        initRecyclerView(listView);

//            RectangularBounds bounds = RectangularBounds.newInstance(
//                    new LatLng(-33.878872968030315, 151.18850925236305),
//                    new LatLng(-33.85214696974035, 151.2186014922668));
//            FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
//                    //.setLocationBias(bounds)
//                    .setLocationRestriction(bounds)
//                    .setCountry("au")
//                    .setTypeFilter(TypeFilter.ADDRESS)
//                    .setSessionToken(token)
//                    // .setQuery("restaurant")
//                    .build();
//            placesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {
//                Log.d("TAG", "onCreateView: PRED ");
//                for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
//                    Log.d("TAG", prediction.getPlaceId());
//                    Log.d("TAG", prediction.getPrimaryText(null).toString());
//                }
//            }).addOnFailureListener((exception) -> {
//                if (exception instanceof ApiException) {
//                    ApiException apiException = (ApiException) exception;
//                    Log.e("TAG", "Place not found: " + apiException.getMessage());
//                }
//            });
//
//            Log.d("TAG", "onCreateView: client");
//        } else Log.d("TAG", "onCreateView: clientnull");
//        //   } else Log.d("TAG", "onCreateView: initnull");

//        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
//                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);
//
//// Specify the types of place data to return.
//        if (autocompleteFragment != null) {
//            autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.NAME));
//
//
//            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//                @Override
//                public void onPlaceSelected(Place place) {
//                    // TODO: Get info about the selected place.
//                    Log.i("TAG", "Place: " + place.getName() + ", ");
//                }
//
//                @Override
//                public void onError(Status status) {
//                    // TODO: Handle the error.
//                    Log.i("TAG", "An error occurred: " + status);
//                }
//            });
//        } else Log.d("TAG", "onCreateView: fragmet null");

        return listView;
    }

//    @Override
//    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
//        final SearchView searchView = (SearchView) menu.findItem(R.id.search_item).getActionView();
//        initSearchView(searchView);
//        super.onCreateOptionsMenu(menu, inflater);
//    }


//    public void initSearchView(SearchView searchView) {
//        searchView.setQueryHint("Search");
//        searchView.setIconifiedByDefault(false);
//        searchView.setFocusable(true);
//        searchView.setIconified(false);
//        searchView.requestFocusFromTouch();
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                //  progressBar.setIndeterminate(true);
//
//                // Cancel any previous place prediction requests
//                handler.removeCallbacksAndMessages(null);
//
//                // Start a new place prediction request in 300 ms
//                handler.postDelayed(() -> {
//                    mPredictionViewModel.newQuery(newText);
//                    // getPlacePredictions(newText);
//                }, 300);
//                return true;
//            }
//        });
//    }

    private void initRecyclerView(View view) {

        final RecyclerView recyclerView = view.findViewById(R.id.fragment_recyclerview);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        if (getContext() != null)
            recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), layoutManager.getOrientation()));
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

//   private LocationBias bias = RectangularBounds.newInstance(
//            new LatLng(47.38545, 0.67909), // SW lat, lng
//            new LatLng(47.39585, 0.69519) );// NE lat, lng

//    private void getPlacePredictions(String query) {
//
//        final LocationBias bias = RectangularBounds.newInstance(
//                new LatLng(47.38545, 0.67909), // SW lat, lng
//                new LatLng(47.39585, 0.69519) // NE lat, lng
//        );

//        final LocationRestriction rest = RectangularBounds.newInstance(
//                new LatLng(22.458744, 88.208162), // SW lat, lng
//                new LatLng(22.730671, 88.524896) // NE lat, lng
//        );

    // Create a new programmatic Place Autocomplete request in Places SDK for Android
//        final FindAutocompletePredictionsRequest newRequest = FindAutocompletePredictionsRequest
//                .builder()
//                .setSessionToken(((MapActivity) getActivity()).getSessionToken())
//                .setLocationBias(bias)
//                .setTypeFilter(TypeFilter.ESTABLISHMENT)
//                .setQuery(query)
//                .setCountries("FR")
//                .build();
//
//        // Perform autocomplete predictions request
//        placesClient.findAutocompletePredictions(newRequest).addOnSuccessListener((response) -> {
//            List<AutocompletePrediction> predictions = response.getAutocompletePredictions();
//            List<AutocompletePrediction> pred = new ArrayList<>();
//            for (AutocompletePrediction p : predictions) {
//                Log.d("TAG", "getPlacePredictions: " + p.getPlaceTypes());
//                if (p.getPlaceTypes().contains(Place.Type.FOOD)) {
//                    pred.add(p);
//                }
//            }
//            adapter.setPredictions(pred);
//
//        }).addOnFailureListener((exception) -> {
//            if (exception instanceof ApiException) {
//                ApiException apiException = (ApiException) exception;
//                Log.e("TAG", "Place not found: " + apiException.getStatusCode());
//            }
//        });
//    }
    public interface AdapterListener {
        void setSearchViewAdapter(List<User> users, RecyclerView recyclerView);
    }

}
