package com.abdurehman.elocator.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.abdurehman.elocator.R;
import com.abdurehman.elocator.activities.NavigationDrawerActivity;
import com.abdurehman.elocator.utilities.AlertUtils.AlertUtilities;
import com.abdurehman.elocator.utilities.GeneralUtils;

import butterknife.BindView;
import butterknife.ButterKnife;



public class LoginFragment extends Fragment {
    AlertDialog alertDialog;
    View view;
    @BindView(R.id.et_email)
    EditText etEmail;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.tv_sign_up)
    TextView tvSignUp;


    String strEmail, strPassword;
    boolean valid = false;

    private FirebaseAuth auth;
    public static DatabaseReference databaseReference;
    public static FirebaseDatabase firebaseDatabase;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, view);
        auth = FirebaseAuth.getInstance();
        initViews();

        return view;
    }

    private void initViews(){
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    alertDialog = AlertUtilities.createProgressDialog(getActivity());
                    alertDialog.show();
                    userLogin();
                }
            }
        });

        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeneralUtils.connectFragment(getActivity(), new SignUpFragment());
            }
        });
    }


    private void userLogin() {

        auth.signInWithEmailAndPassword(strEmail, strPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    alertDialog.dismiss();
                    Toast.makeText(getActivity(), "your email or password is incorrect", Toast.LENGTH_SHORT).show();
                } else {
                    alertDialog.dismiss();
                    String[] splitStr = strEmail.split("@");
                    String child = splitStr[0];
                    showProfileInfo(child);
                }
            }
        });
    }

    private boolean validate() {
        valid = true;
        strEmail = etEmail.getText().toString().trim();
        strPassword = etPassword.getText().toString().trim();


        GeneralUtils.putStringValueInEditor(getActivity(),"email",strEmail);


        if (strEmail.isEmpty()) {
            etEmail.setError("enter a valid email address");
            valid = false;
        } else {
            etEmail.setError(null);
        }


        if (strPassword.isEmpty()) {
            etPassword.setError("Please enter a your password");
            valid = false;
        } else {
            etPassword.setError(null);
        }
        return valid;
    }

    public void showProfileInfo(String child) {

        firebaseDatabase = FirebaseDatabase.getInstance();

        databaseReference = firebaseDatabase.getReference("Profile").child(child);
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!dataSnapshot.exists()) {
                    Log.d("data","no data available");
                } else {
                    String strImage = dataSnapshot.child("image").getValue().toString();
                    String strName = dataSnapshot.child("name").getValue().toString();

                    GeneralUtils.putBooleanValueInEditor(getActivity(), "loggedIn", true).commit();
                    GeneralUtils.putStringValueInEditor(getActivity(),"image",strImage);
                    GeneralUtils.putStringValueInEditor(getActivity(),"username",strName);

                    startActivity(new Intent(getActivity(), NavigationDrawerActivity.class));

                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        databaseReference.addListenerForSingleValueEvent(eventListener);
    }
}
