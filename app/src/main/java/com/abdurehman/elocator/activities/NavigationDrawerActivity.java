package com.abdurehman.elocator.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import com.abdurehman.elocator.fragments.AllPlaceFragment;
import com.abdurehman.elocator.fragments.StoreFragment;
import com.bumptech.glide.Glide;
import com.abdurehman.elocator.R;
import com.abdurehman.elocator.fragments.AddStoreFragment;
import com.abdurehman.elocator.fragments.HomeFragment;
import com.abdurehman.elocator.fragments.NewsFragment;
import com.abdurehman.elocator.fragments.ProfileFragment;
import com.abdurehman.elocator.utilities.GeneralUtils;
import com.abdurehman.elocator.utilities.ShareUtils;

public class NavigationDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    ImageView ivProfile;
    TextView navUsername;
    String username,image;
    DrawerLayout drawer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        GeneralUtils.connectFragmentWithDrawer(this, new HomeFragment());
        username = GeneralUtils.getSharedPreferences(this).getString("username","");
        image = GeneralUtils.getSharedPreferences(this).getString("image","");



        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);

        navUsername = (TextView) headerView.findViewById(R.id.name);
        ivProfile = headerView.findViewById(R.id.imageView);

        Glide.with(this).load(image).into(ivProfile);
        navUsername.setText(username);

        navUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawer(GravityCompat.START);
                GeneralUtils.connectFragmentWithDrawer(NavigationDrawerActivity.this,new ProfileFragment());
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            GeneralUtils.connectFragmentWithDrawer(this, new HomeFragment());
        } else if (id == R.id.nav_store) {
            GeneralUtils.connectFragmentWithDrawer(this,new AllPlaceFragment());
        } else if (id == R.id.nav_share) {
            startActivity(ShareUtils.shareApp());
        } else if (id == R.id.nav_rate) {
            loadGooglePlay();
        } else if (id == R.id.nav_add) {
            this.setTitle("Register your store");
            GeneralUtils.connectFragmentWithDrawer(this, new AddStoreFragment());
        }
        else if (id == R.id.nav_news) {
            GeneralUtils.connectFragmentWithDrawer(this, new NewsFragment());
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadGooglePlay() {
        try {
            startActivity(ShareUtils.loadApp(this));
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + this.getPackageName())));
        }
    }




}
