package com.techease.elocator.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.techease.elocator.R;
import com.techease.elocator.models.StoreDataModel;
import com.techease.elocator.utilities.BaseNetworking;
import com.techease.elocator.utilities.GeneralUtils;
import com.techease.elocator.utilities.GetLocation;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class StoreFragment extends Fragment {
    View view;
    @BindView(R.id.title)
    TextView tvTitle;
    @BindView(R.id.floating_search_view_shop)
    FloatingSearchView mSearchViewShop;
    @BindView(R.id.rv_stores)
    RecyclerView rvStores;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private FirebaseRecyclerAdapter adapter;
    GetLocation getLocation;

    Bundle bundle;
    String strCategory;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_store, container, false);
        getActivity().setTitle(getResources().getString(R.string.app_name));
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
             tvTitle.setText(strCategory);
        }

        firebaseDatabase = FirebaseDatabase.getInstance();
        showCustomerData(strCategory);




        mSearchViewShop.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {

            }
        });
    }

    private void showCustomerData(String category) {
        databaseReference = firebaseDatabase.getReference("Stores").child(category);

        rvStores.setHasFixedSize(true);
        rvStores.setLayoutManager(new LinearLayoutManager(getActivity()));

        FirebaseRecyclerOptions<StoreDataModel> options =
                new FirebaseRecyclerOptions.Builder<StoreDataModel>()
                        .setQuery(databaseReference, new SnapshotParser<StoreDataModel>() {
                            @NonNull
                            @Override
                            public StoreDataModel parseSnapshot(@NonNull DataSnapshot snapshot) {
                                return new StoreDataModel(
                                        snapshot.child("address").getValue().toString(),
                                        snapshot.child("contact").getValue().toString(),
                                        snapshot.child("image").getValue().toString(),
                                        snapshot.child("latitude").getValue().toString(),
                                        snapshot.child("longitude").getValue().toString(),
                                        snapshot.child("title").getValue().toString());

                            }
                        })
                        .build();



        adapter = new FirebaseRecyclerAdapter<StoreDataModel, StoreFragment.UsersViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull StoreFragment.UsersViewHolder holder, int position, @NonNull final StoreDataModel model) {

                holder.setName(getActivity(),model.getTitle(), model.getAddress(), model.getContact(), model.getImage(), model.getLatitude(), model.getLongitude());

            }

            @NonNull
            @Override
            public StoreFragment.UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.custome_store_layout, parent, false);

                return new StoreFragment.UsersViewHolder(view);
            }

            @Override
            public void onDataChanged() {
                rvStores.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        };

    }

    public class UsersViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public UsersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }


        public void setName(FragmentActivity activity, String company, String address, final String phone, String image, final String lat, final String lng) {
            TextView tvCompany = mView.findViewById(R.id.tv_title);
            ImageView ivPoster = mView.findViewById(R.id.iv_poster);
            TextView tvAddress = mView.findViewById(R.id.tv_address);
            TextView tvContact = mView.findViewById(R.id.tv_contact);
            ImageView ivCall = mView.findViewById(R.id.iv_call);
            ImageView ivMap = mView.findViewById(R.id.iv_map);
            tvCompany.setText(company);
            tvAddress.setText(address);
            tvContact.setText(phone);
            Glide.with(activity).load(image).into(ivPoster);

            ivCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadCall(phone);
                }
            });
            ivMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                navigateLocation(lat,lng);
                }
            });


        }
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void loadCall(String phone){
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
        startActivity(intent);
    }

    private void navigateLocation(String lat,String lng){
        String currentLat = GeneralUtils.getSharedPreferences(getActivity()).getString("latitude","");
        String currentLng = GeneralUtils.getSharedPreferences(getActivity()).getString("longitude","");
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps.mytracks?saddr="+currentLat+","+currentLng+"&daddr="+lat+","+lng));
        startActivity(intent);
    }


}
