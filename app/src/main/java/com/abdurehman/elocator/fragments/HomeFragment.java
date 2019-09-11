package com.abdurehman.elocator.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.abdurehman.elocator.R;
import com.abdurehman.elocator.utilities.GeneralUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeFragment extends Fragment {
    View view;
    @BindView(R.id.layoutHospital)
    FrameLayout layoutHospital;
    @BindView(R.id.layoutHotel)
    FrameLayout layoutHotel;
    @BindView(R.id.layoutShops)
    FrameLayout layoutShops;
    @BindView(R.id.layoutOther)
    FrameLayout layoutOthers;

    Bundle bundle;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this,view);
        getActivity().setTitle(getResources().getString(R.string.app_name));
        onback(view);
        initViews();

        return view;
    }

    private void initViews(){
        bundle = new Bundle();

        layoutHospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putString("category","Hospital");
                GeneralUtils.connectFragmentWithDrawer(getActivity(),new StoreFragment()).setArguments(bundle);
            }
        });

        layoutHotel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putString("category","Resturants");
                GeneralUtils.connectFragmentWithDrawer(getActivity(),new StoreFragment()).setArguments(bundle);
            }
        });

        layoutShops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putString("category","Shops");
                GeneralUtils.connectFragmentWithDrawer(getActivity(),new StoreFragment()).setArguments(bundle);
            }
        });

        layoutOthers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putString("category","Others");
                GeneralUtils.connectFragmentWithDrawer(getActivity(),new StoreFragment()).setArguments(bundle);
            }
        });
    }

    private void onback(View view) {

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    getActivity().finishAffinity();
                    return true;
                }
                return false;
            }
        });

    }
}
