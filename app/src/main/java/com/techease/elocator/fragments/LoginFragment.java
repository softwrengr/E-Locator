package com.techease.elocator.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
import com.techease.elocator.R;
import com.techease.elocator.activities.MainActivity;
import com.techease.elocator.activities.NavigationDrawerActivity;
import com.techease.elocator.utilities.AlertUtils.AlertUtilities;
import com.techease.elocator.utilities.GeneralUtils;

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
                    GeneralUtils.putBooleanValueInEditor(getActivity(), "loggedIn", true).commit();
                    startActivity(new Intent(getActivity(), NavigationDrawerActivity.class));
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
}
