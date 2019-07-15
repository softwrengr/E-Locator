package com.techease.elocator.fragments;

import android.content.Context;
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
import com.techease.elocator.utilities.AlertUtils.AlertUtilities;
import com.techease.elocator.utilities.GeneralUtils;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SignUpFragment extends Fragment {
    AlertDialog alertDialog;
    View view;
    @BindView(R.id.et_signup_name)
    EditText etName;
    @BindView(R.id.et_signup_email)
    EditText etEmail;
    @BindView(R.id.et_signup_password)
    EditText etPassword;
    @BindView(R.id.tv_aleady_login)
    TextView tvLogin;
    @BindView(R.id.btn_sign_up)
    Button btnSignup;

    boolean valid = false;
    String strName, strEmail, strPassword;
    FirebaseAuth auth;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        ButterKnife.bind(this, view);
        auth = FirebaseAuth.getInstance();
        initUI();
        return view;
    }

    private void initUI() {

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    alertDialog = AlertUtilities.createProgressDialog(getActivity());
                    alertDialog.show();
                    userRegistration();

                }
            }
        });

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeneralUtils.connectFragmentWithBack(getActivity(), new LoginFragment());
            }
        });
    }

    private void userRegistration() {

        auth.createUserWithEmailAndPassword(strEmail,strPassword).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    Toast.makeText(getActivity(), "user successfully added", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                    GeneralUtils.connectFragment(getActivity(),new LoginFragment());
                }
                else {
                    Toast.makeText(getActivity(), "try with another email", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

    private boolean validate() {
        valid = true;

        strName = etName.getText().toString();
        strEmail = etEmail.getText().toString().trim();
        strPassword = etPassword.getText().toString();

        if (strName.isEmpty()) {
            etName.setError("enter your full name");
            valid = false;
        } else {
            etName.setError(null);
        }

        if (strEmail.isEmpty()) {
            etEmail.setError("enter a valid email address");
            valid = false;
        } else {
            etEmail.setError(null);
        }


        if (strPassword.isEmpty()) {
            etPassword.setError("Please enter a valid password");
            valid = false;
        } else {
            etPassword.setError(null);
        }
        return valid;
    }
}
