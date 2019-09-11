package com.abdurehman.elocator.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.abdurehman.elocator.R;
import com.abdurehman.elocator.activities.MainActivity;
import com.abdurehman.elocator.utilities.GeneralUtils;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ProfileFragment extends Fragment {
    View view;
    @BindView(R.id.tv_user_email)
    TextView tvUserEmail;
    @BindView(R.id.tv_user_name)
    TextView tvUsername;
    @BindView(R.id.iv_user_profile)
    ImageView ivUserProfile;
    @BindView(R.id.btn_logout)
    Button btnLogout;

    private DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this,view);
        showProfileInfo();


        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeneralUtils.putBooleanValueInEditor(getActivity(), "loggedIn", false).commit();
                startActivity(new Intent(getActivity(), MainActivity.class));
            }
        });
        return view;
    }


    private void showProfileInfo() {

        String user = GeneralUtils.getSharedPreferences(getActivity()).getString("email","");
        String[] splitStr = user.split("@");
        String child = splitStr[0];
        firebaseDatabase = FirebaseDatabase.getInstance();

        databaseReference = firebaseDatabase.getReference("Profile").child(child);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!dataSnapshot.exists()) {
                    Toast.makeText(getActivity(), "No data available", Toast.LENGTH_SHORT).show();
                } else {
                    String image = dataSnapshot.child("image").getValue().toString();
                    String name = dataSnapshot.child("name").getValue().toString();
                    String email = dataSnapshot.child("email").getValue().toString();

                    Glide.with(getActivity()).load(image).into(ivUserProfile);
                    tvUsername.setText(name);
                    tvUserEmail.setText(email);
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        databaseReference.addListenerForSingleValueEvent(eventListener);
    }
}
