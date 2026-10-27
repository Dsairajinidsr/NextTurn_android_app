package com.example.nextturn_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.nextturn_project.auth.manager.ManagerLoginActivity;
import com.example.nextturn_project.auth.patient.PatientLoginActivity;
import com.example.nextturn_project.auth.staff.StaffLoginActivity;

public class ChooseUsers_page extends AppCompatActivity {
    Button btnPatient, btnStaff, btnManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chooseusers_page);
        //initialize Buttons
        btnPatient= (Button) findViewById(R.id.btnpatient);
        btnStaff= (Button) findViewById(R.id.btnstaff);
        btnManager= (Button) findViewById(R.id.btnmanager);

        // Patient Login
        btnPatient.setOnClickListener(v -> {
            Intent intent = new Intent(ChooseUsers_page.this,PatientLoginActivity.class);
            startActivity(intent);
        });

        // Staff Login
        btnStaff.setOnClickListener(v -> {
            Intent intent = new Intent(ChooseUsers_page.this, StaffLoginActivity.class);
            startActivity(intent);
        });

        // Manager Login
        btnManager.setOnClickListener(v -> {
            Intent intent = new Intent(ChooseUsers_page.this, ManagerLoginActivity.class);
            startActivity(intent);
        });

    }
}