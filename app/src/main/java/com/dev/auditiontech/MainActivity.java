package com.dev.auditiontech;

import android.content.res.Configuration;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {
    private TextView mTextMessage;
    private Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_monitor:
                    selectedFragment = MeterFragment.getInstance();
                    break;
                case R.id.navigation_history:
                    //mTextMessage.setText(R.string.title_history);
                    break;
                case R.id.navigation_essentials:
                    selectedFragment = EssentialFragment.getInstance();
                    break;
            }
            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_fragment_container, selectedFragment).commit();
            }

            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupUI();
    }

    private void setupUI() {
        BottomNavigationView navView = findViewById(R.id.nav_view);
        mTextMessage = findViewById(R.id.message);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navView.setSelectedItemId(R.id.navigation_monitor);


        toolbar = findViewById(R.id.main_toolbar);
        mDrawerLayout = findViewById(R.id.main_drawer);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Hearing Aid");


        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                    toolbar, R.string.open_drawer, R.string.close_drawer) {

                public void onDrawerClosed(View view) {
                    supportInvalidateOptionsMenu();
                    //drawerOpened = false;
                }

                public void onDrawerOpened(View drawerView) {
                    supportInvalidateOptionsMenu();
                    //drawerOpened = true;
                }
            };
            mDrawerToggle.setDrawerIndicatorEnabled(true);
            mDrawerLayout.setDrawerListener(mDrawerToggle);
            mDrawerToggle.syncState();

        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

}
