package com.techease.elocator.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.techease.elocator.R;
import com.techease.elocator.fragments.LoginFragment;
import com.techease.elocator.utilities.GeneralUtils;

public class MainActivity extends AppCompatActivity {
    private boolean login = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setTitle(getResources().getString(R.string.app_name));


        login = GeneralUtils.getSharedPreferences(this).getBoolean("loggedIn",false);

        if(login){
            startActivity(new Intent(this, NavigationDrawerActivity.class));
        }
        else {
            GeneralUtils.connectFragment(MainActivity.this,new LoginFragment());
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finishAffinity();
    }
}
