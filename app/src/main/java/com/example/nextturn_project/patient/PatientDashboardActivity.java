package com.example.nextturn_project.patient;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.nextturn_project.R;
import com.example.nextturn_project.patient.fragments.PatientHomeFragment;
import com.example.nextturn_project.patient.fragments.PatientProfileFragment;
import com.example.nextturn_project.patient.fragments.PatientQRFragment;
import com.example.nextturn_project.patient.fragments.PatientQRFragment;
import com.example.nextturn_project.patient.fragments.PatientQueueFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

public class PatientDashboardActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_dashboard);
        saveFCMToken();

        bottomNavigation = findViewById(R.id.bottomNavigation);

        // Default Fragment
        loadFragment(new PatientHomeFragment());
        bottomNavigation.setSelectedItemId(R.id.nav_home);

        // Bottom Navigation Click Listener
        bottomNavigation.setOnItemSelectedListener(item -> {

            Fragment fragment = null;

            int id = item.getItemId();

            if (id == R.id.nav_home) {

                fragment = new PatientHomeFragment();

            } else if (id == R.id.nav_queue) {

                fragment = new PatientQueueFragment();

            } else if (id == R.id.nav_qrcode) {

                fragment = new PatientQRFragment();

            } else if (id == R.id.nav_profile) {

                fragment = new PatientProfileFragment();

            }

            if (fragment != null) {
                loadFragment(fragment);
            }

            return true;
        });
    }

    private void loadFragment(Fragment fragment) {

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.patientFrameLayout, fragment)
                .commit();
    }
    private void saveFCMToken() {

        if (FirebaseAuth.getInstance().getCurrentUser() == null)
            return;

        String uid = FirebaseAuth.getInstance()
                .getCurrentUser()
                .getUid();

        FirebaseMessaging.getInstance()
                .getToken()
                .addOnSuccessListener(token -> {

                    FirebaseFirestore.getInstance()
                            .collection("patients")
                            .document(uid)
                            .update("fcmToken", token);

                });

    }
}