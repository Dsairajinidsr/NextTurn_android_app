package com.example.nextturn_project.staff.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.nextturn_project.R;
import com.example.nextturn_project.manager.FirestoreManager;
import com.example.nextturn_project.staff.model.PatientModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.example.nextturn_project.staff.fragments.PatientTokenFragment;
import com.example.nextturn_project.staff.fragments.PatientTokenFragment;

public class RegisterPatientFragment extends Fragment {

    private TextInputEditText etPatientName;
    private TextInputEditText etAge;
    private TextInputEditText etMobile;
    private TextInputEditText etSymptoms;

    private Spinner spDepartment;

    private RadioGroup rgGender;
    private RadioGroup rgPriority;

    private MaterialButton btnRegisterPatient;

    private FirestoreManager firestoreManager;

    public RegisterPatientFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_register_patient,
                container,
                false);

        initViews(view);

        firestoreManager = new FirestoreManager();

        loadDepartments();

        btnRegisterPatient.setOnClickListener(v -> validateInputs());

        return view;
    }

    private void initViews(View view) {

        etPatientName = view.findViewById(R.id.etPatientName);
        etAge = view.findViewById(R.id.etAge);
        etMobile = view.findViewById(R.id.etMobile);
        etSymptoms = view.findViewById(R.id.etSymptoms);

        spDepartment = view.findViewById(R.id.spDepartment);

        rgGender = view.findViewById(R.id.rgGender);
        rgPriority = view.findViewById(R.id.rgPriority);

        btnRegisterPatient = view.findViewById(R.id.btnRegisterPatient);
    }

    private void loadDepartments() {

        String[] departments = {
                "Select Department",
                "General Medicine",
                "Cardiology",
                "Orthopedics",
                "Neurology",
                "Dermatology",
                "ENT",
                "Pediatrics",
                "Gynecology"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                departments
        );

        spDepartment.setAdapter(adapter);
    }
    private void validateInputs() {

        String patientName = etPatientName.getText().toString().trim();
        String age = etAge.getText().toString().trim();
        String mobile = etMobile.getText().toString().trim();
        String symptoms = etSymptoms.getText().toString().trim();

        if (TextUtils.isEmpty(patientName)) {
            etPatientName.setError("Enter Patient Name");
            etPatientName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(age)) {
            etAge.setError("Enter Age");
            etAge.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(mobile)) {
            etMobile.setError("Enter Mobile Number");
            etMobile.requestFocus();
            return;
        }

        if (mobile.length() != 10) {
            etMobile.setError("Enter Valid Mobile Number");
            etMobile.requestFocus();
            return;
        }

        if (spDepartment.getSelectedItemPosition() == 0) {
            Toast.makeText(getContext(),
                    "Please select a department",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (rgGender.getCheckedRadioButtonId() == -1) {
            Toast.makeText(getContext(),
                    "Please select Gender",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (rgPriority.getCheckedRadioButtonId() == -1) {
            Toast.makeText(getContext(),
                    "Please select Priority",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(symptoms)) {
            etSymptoms.setError("Enter Symptoms");
            etSymptoms.requestFocus();
            return;
        }

        RadioButton genderButton =
                requireView().findViewById(rgGender.getCheckedRadioButtonId());

        RadioButton priorityButton =
                requireView().findViewById(rgPriority.getCheckedRadioButtonId());

        String gender = genderButton.getText().toString();
        String priority = priorityButton.getText().toString();
        String department = spDepartment.getSelectedItem().toString();

        String currentDate =
                new SimpleDateFormat(
                        "dd-MM-yyyy",
                        Locale.getDefault())
                        .format(new Date());

        String currentTime =
                new SimpleDateFormat(
                        "hh:mm a",
                        Locale.getDefault())
                        .format(new Date());

        PatientModel patient = new PatientModel();

        patient.setPatientName(patientName);
        patient.setAge(age);
        patient.setGender(gender);
        patient.setMobile(mobile);
        patient.setDepartment(department);
        patient.setSymptoms(symptoms);
        patient.setPriority(priority);

        patient.setRegisteredBy("Staff");
        patient.setRegistrationDate(currentDate);
        patient.setRegistrationTime(currentTime);

        patient.setCompleted(false);

        registerPatient(patient);
    }
    private void registerPatient(PatientModel patient) {

        btnRegisterPatient.setEnabled(false);

        firestoreManager.registerPatient(patient,
                new FirestoreManager.RegisterCallback() {

                    @Override
                    public void onSuccess(String token) {

                        btnRegisterPatient.setEnabled(true);

                        Bundle bundle = new Bundle();

                        bundle.putString("token", token);
                        bundle.putString("patientName", patient.getPatientName());
                        bundle.putString("department", patient.getDepartment());
                        bundle.putString("status", "Waiting");

                        PatientTokenFragment fragment = new PatientTokenFragment();
                        fragment.setArguments(bundle);

                        requireActivity()
                                .getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.frameLayout, fragment)
                                .addToBackStack(null)
                                .commit();

                        clearFields();

                    }

                    @Override
                    public void onFailure(String message) {

                        btnRegisterPatient.setEnabled(true);

                        Toast.makeText(
                                requireContext(),
                                "Registration Failed\n" + message,
                                Toast.LENGTH_LONG
                        ).show();

                    }
                });

    }
    private void clearFields() {

        etPatientName.setText("");
        etAge.setText("");
        etMobile.setText("");
        etSymptoms.setText("");

        spDepartment.setSelection(0);

        rgGender.clearCheck();
        rgPriority.clearCheck();

        etPatientName.requestFocus();
    }

}