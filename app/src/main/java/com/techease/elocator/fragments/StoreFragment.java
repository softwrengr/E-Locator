package com.techease.elocator.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.techease.elocator.R;
import com.techease.elocator.adapters.StoreAdapter;
import com.techease.elocator.models.StoreDataModel;
import com.techease.elocator.utilities.AlertUtils.AlertUtilities;
import com.techease.elocator.utilities.GeneralUtils;
import com.techease.elocator.utilities.GetLocation;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class StoreFragment extends Fragment {
    AlertDialog alertDialog;
    View view;
    @BindView(R.id.floating_search_view_shop)
    FloatingSearchView mSearchViewShop;
    @BindView(R.id.rv_stores)
    RecyclerView rvStores;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private FirebaseRecyclerAdapter adapter;
    GetLocation getLocation;
    private ArrayList<StoreDataModel> storeDataModelArrayList;
    StoreAdapter storeAdapter;
    LinearLayoutManager layoutManager;

    Bundle bundle;
    String strCategory;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_store, container, false);
        getLocation = new GetLocation();
        getLocation.getLocation(getActivity());
        initUI();
        return view;
    }

    private void initUI() {
        ButterKnife.bind(this, view);
        bundle = this.getArguments();
        if (bundle != null) {
            strCategory = bundle.getString("category");
            getActivity().setTitle(strCategory);
        }

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Stores").child(strCategory);

        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvStores.setLayoutManager(layoutManager);
        storeDataModelArrayList = new ArrayList<>();
        alertDialog = AlertUtilities.createProgressDialog(getActivity());
        alertDialog.show();
        storeAdapter = new StoreAdapter(getActivity(), storeDataModelArrayList);
        rvStores.setAdapter(storeAdapter);
        showData();


        mSearchViewShop.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {
              storeAdapter.getFilter().filter(newQuery);
            }
        });

    }

    private void showData(){

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    StoreDataModel upload = postSnapshot.getValue(StoreDataModel.class);
                    storeDataModelArrayList.add(upload);
                }
                storeAdapter = new StoreAdapter(getActivity(), storeDataModelArrayList);

                rvStores.setAdapter(storeAdapter);
                storeAdapter.notifyDataSetChanged();
                alertDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }


}
