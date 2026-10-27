package com.example.nextturn_project.patient.fragments;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.nextturn_project.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

public class PatientQRFragment extends Fragment {

    private TextView tvPatientName, tvToken, tvDepartment;
    private ImageView imgQRCode;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public PatientQRFragment() {
        // Required empty constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_patient_q_r,
                container,
                false);

        tvPatientName = view.findViewById(R.id.tvPatientName);
        tvToken = view.findViewById(R.id.tvToken);
        tvDepartment = view.findViewById(R.id.tvDepartment);
        imgQRCode = view.findViewById(R.id.imgQRCode);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loadPatientDetails();

        return view;
    }

    private void loadPatientDetails() {

        if (mAuth.getCurrentUser() == null)
            return;

        String uid = mAuth.getCurrentUser().getUid();

        db.collection("patients")
                .document(uid)
                .get()
                .addOnSuccessListener(this::showPatientDetails)
                .addOnFailureListener(e ->
                        Toast.makeText(
                                getContext(),
                                e.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show());
    }

    private void showPatientDetails(DocumentSnapshot document) {

        if (!document.exists())
            return;

        String uid = mAuth.getCurrentUser().getUid();
        String name = document.getString("name");
        String token = document.getString("tokenNumber");
        String department = document.getString("department");

        if (name != null)
            tvPatientName.setText(name);

        if (token != null)
            tvToken.setText("Token : " + token);
        else
            tvToken.setText("Token : Not Assigned");

        if (department != null)
            tvDepartment.setText("Department : " + department);
        else
            tvDepartment.setText("Department : Not Assigned");

        generateQRCode(uid, name, token, department);
    }

    private void generateQRCode(String uid,
                                String name,
                                String token,
                                String department) {

        try {

            String qrData =
                    "{"
                            + "\"patientId\":\"" + uid + "\","
                            + "\"token\":\"" + token + "\","
                            + "\"patientName\":\"" + name + "\","
                            + "\"department\":\"" + department + "\""
                            + "}";

            BitMatrix bitMatrix =
                    new MultiFormatWriter().encode(
                            qrData,
                            BarcodeFormat.QR_CODE,
                            700,
                            700);

            Bitmap bitmap = Bitmap.createBitmap(
                    700,
                    700,
                    Bitmap.Config.RGB_565);

            for (int x = 0; x < 700; x++) {

                for (int y = 0; y < 700; y++) {

                    bitmap.setPixel(
                            x,
                            y,
                            bitMatrix.get(x, y)
                                    ? Color.BLACK
                                    : Color.WHITE);

                }

            }

            imgQRCode.setImageBitmap(bitmap);

        } catch (Exception e) {

            e.printStackTrace();

            Toast.makeText(
                    getContext(),
                    "QR Code Generation Failed",
                    Toast.LENGTH_SHORT
            ).show();

        }

    }

}