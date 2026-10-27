package com.example.nextturn_project.staff;

import android.os.Bundle;

import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.ExperimentalGetImage;
import androidx.fragment.app.Fragment;

import com.example.nextturn_project.R;
import com.example.nextturn_project.staff.fragments.HomeFragment;
import com.example.nextturn_project.staff.fragments.ProfileFragment;
import com.example.nextturn_project.staff.fragments.QueueFragment;
import com.example.nextturn_project.staff.fragments.RegisterPatientFragment;
import com.example.nextturn_project.staff.fragments.ScanQRCodeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class StaffDashBoardActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigation;

    @OptIn(markerClass = ExperimentalGetImage.class)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_dashboard);

        bottomNavigation = findViewById(R.id.bottomNavigation);

        // Load Home Fragment by default
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        // Bottom Navigation Click Listener
        bottomNavigation.setOnItemSelectedListener(item -> {

            Fragment fragment = null;

            int id = item.getItemId();

            if (id == R.id.nav_home) {
                fragment = new HomeFragment();
            }

            else if (id == R.id.nav_register) {
                fragment = new RegisterPatientFragment();
            }

            else if (id == R.id.nav_scan) {
                fragment = new ScanQRCodeFragment(); // IMPORTANT FIX
            }

            else if (id == R.id.nav_queue) {
                fragment = new QueueFragment();
            }

            if (fragment != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frameLayout, fragment)
                        .commit();
            }

            return true;
        });

    }

    // Method to Load Fragments
    private void loadFragment(Fragment fragment) {

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout, fragment)
                .commit();

    }
}