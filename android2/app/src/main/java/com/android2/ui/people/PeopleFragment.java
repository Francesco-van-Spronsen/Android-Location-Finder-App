package com.android2.ui.people;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android2.MainActivity;
import com.android2.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PeopleFragment extends Fragment {

    private PeopleViewModel peopleViewModel;

    private RecyclerView peopleRecyclerView;
    private PeopleAdapter peopleAdapter;

    public PeopleAdapter getPeopleAdapter() { return this.peopleAdapter; }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        peopleViewModel =  ViewModelProviders.of(this).get(PeopleViewModel.class);
        View root = inflater.inflate(R.layout.fragment_people, container, false);

        MainActivity mainActivity = (MainActivity)getActivity();

        peopleRecyclerView = root.findViewById(R.id.people_recycler_view);
        peopleAdapter = new PeopleAdapter(mainActivity.getPeopleList());
        peopleAdapter.addContext(getActivity());
        RecyclerView.LayoutManager peopleLayoutManager = new LinearLayoutManager(getActivity());
        peopleRecyclerView.setLayoutManager(peopleLayoutManager);
        peopleRecyclerView.setItemAnimator(new DefaultItemAnimator());
        peopleRecyclerView.addItemDecoration(new DividerItemDecoration(Objects.requireNonNull(getActivity()), DividerItemDecoration.VERTICAL));
        peopleRecyclerView.setAdapter(peopleAdapter);

        return root;
    }
}
