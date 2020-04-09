package com.example.go4lunch;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class RecyclerFragment extends Fragment {

    private static final String LIST_VIEW = "LISTVIEW";
    private TextView mTextView;

    @NonNull
    public static RecyclerFragment newInstance(boolean listView) {
        RecyclerFragment recyclerFragment = new RecyclerFragment();
        Bundle arg = new Bundle();
        arg.putBoolean(LIST_VIEW, listView);
        recyclerFragment.setArguments(arg);
        return recyclerFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View listView = inflater.inflate(R.layout.fragment_recycler,container,false);
        mTextView = listView.findViewById(R.id.textfrag);
        boolean list = (getArguments() == null) || getArguments().getBoolean(LIST_VIEW, true);

        if (list) mTextView.setText("List View");
        else mTextView.setText("List Worker");

        return listView;
    }
}
