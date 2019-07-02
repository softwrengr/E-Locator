package com.techease.elocator.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.techease.elocator.R;
import com.techease.elocator.models.StoreDataModel;
import com.techease.elocator.utilities.GeneralUtils;
import com.techease.elocator.utilities.ShareUtils;

import java.util.ArrayList;
import java.util.List;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.MyViewHolder> implements Filterable {
    List<StoreDataModel> storeList;
     List<StoreDataModel> listFiltered;
    Context context;

    public StoreAdapter(Context context, List<StoreDataModel> storeList) {
        this.context = context;
        this.storeList = storeList;
        this.listFiltered = storeList;
    }


    @NonNull
    @Override
    public StoreAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custome_store_layout, parent, false);

        return new StoreAdapter.MyViewHolder(itemView);
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull final StoreAdapter.MyViewHolder viewHolder, final int position) {
        final StoreDataModel model = listFiltered.get(position);

        viewHolder.tvCompany.setText(model.getTitle());
        viewHolder.tvAddress.setText(model.getAddress());
        viewHolder.tvContact.setText(model.getContact());
        Glide.with(context).load(model.getImage()).into(viewHolder.ivPoster);

        viewHolder.tvContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadCall(model.getContact());
            }
        });

        viewHolder.ivMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateLocation(model.getLatitude(),model.getLongitude());
            }
        });

        viewHolder.ivPoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareUtils.showImage((Activity) context,model.getImage());
            }
        });

    }

    @Override
    public int getItemCount() {
        return listFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    listFiltered = storeList;
                } else {
                    List<StoreDataModel> filteredList = new ArrayList<>();
                    for (StoreDataModel row : storeList) {

                        if (row.getTitle().toLowerCase().contains(charString.toLowerCase()) || row.getContact().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    listFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = listFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                listFiltered = (ArrayList<StoreDataModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvCompany,tvAddress,tvContact;
        ImageView ivPoster,ivCall,ivMap;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

             tvCompany = itemView.findViewById(R.id.tv_title);
             ivPoster = itemView.findViewById(R.id.iv_poster);
             tvAddress = itemView.findViewById(R.id.tv_address);
             tvContact = itemView.findViewById(R.id.tv_contact);
             ivCall = itemView.findViewById(R.id.iv_call);
             ivMap = itemView.findViewById(R.id.iv_map);

        }
    }

    private void loadCall(String phone) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
        context.startActivity(intent);
    }

    private void navigateLocation(String lat, String lng) {
        String currentLat = GeneralUtils.getSharedPreferences(context).getString("latitude", "");
        String currentLng = GeneralUtils.getSharedPreferences(context).getString("longitude", "");
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps.mytracks?saddr=" + currentLat + "," + currentLng + "&daddr=" + lat + "," + lng));
        context.startActivity(intent);
    }



}
