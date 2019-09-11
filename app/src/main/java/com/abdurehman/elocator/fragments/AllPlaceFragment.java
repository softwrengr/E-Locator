package com.abdurehman.elocator.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abdurehman.elocator.R;
import com.abdurehman.elocator.adapters.StoreAdapter;
import com.abdurehman.elocator.models.StoreDataModel;
import com.abdurehman.elocator.utilities.AlertUtils.AlertUtilities;
import com.abdurehman.elocator.utilities.GetLocation;
import com.arlib.floatingsearchview.FloatingSearchView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AllPlaceFragment extends Fragment {
    AlertDialog alertDialog;
    View view;
    @BindView(R.id.floating_search_view_shop)
    FloatingSearchView mSearchViewShop;
    @BindView(R.id.rv_all_places)
    RecyclerView rvStores;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference,databaseReference2,databaseReference3;
    private ArrayList<StoreDataModel> storeDataModelArrayList,storeDataModelArrayList2,storeDataModelArrayList3;
    StoreAdapter storeAdapter;
    LinearLayoutManager layoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_all_place, container, false);
        initUI();
        return view;
    }

    private void initUI() {
        ButterKnife.bind(this, view);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Stores").child("Hospital");
        databaseReference2 = firebaseDatabase.getReference("Stores").child("Resturants");
        databaseReference3 = firebaseDatabase.getReference("Stores").child("Shops");

        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvStores.setLayoutManager(layoutManager);
        storeDataModelArrayList = new ArrayList<>();
        storeDataModelArrayList2 = new ArrayList<>();
        storeDataModelArrayList3 = new ArrayList<>();
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

        databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    StoreDataModel upload = postSnapshot.getValue(StoreDataModel.class);
                    storeDataModelArrayList2.add(upload);
                    storeDataModelArrayList.addAll(storeDataModelArrayList2);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

        databaseReference3.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    StoreDataModel upload = postSnapshot.getValue(StoreDataModel.class);
                    storeDataModelArrayList3.add(upload);
                    storeDataModelArrayList.addAll(storeDataModelArrayList3);
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
