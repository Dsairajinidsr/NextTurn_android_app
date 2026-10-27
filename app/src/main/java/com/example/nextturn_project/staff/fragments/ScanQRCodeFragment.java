package com.example.nextturn_project.staff.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.nextturn_project.R;
import com.example.nextturn_project.manager.FirestoreManager;
import com.example.nextturn_project.staff.model.PatientModel;
import com.google.android.material.button.MaterialButton;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;

@ExperimentalGetImage
public class ScanQRCodeFragment extends Fragment {

    private PreviewView previewView;

    private TextView tvToken;
    private TextView tvPatient;

    private MaterialButton btnVerify;

    private ActivityResultLauncher<String> cameraPermissionLauncher;

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    private BarcodeScanner barcodeScanner;

    private FirestoreManager firestoreManager;

    private boolean isScanning = false;

    public ScanQRCodeFragment() {
        // Required empty constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_scan_q_r_code,
                container,
                false);

        previewView = view.findViewById(R.id.previewView);
        tvToken = view.findViewById(R.id.tvToken);
        tvPatient = view.findViewById(R.id.tvPatient);
        btnVerify = view.findViewById(R.id.btnVerify);

        btnVerify.setEnabled(false);

        barcodeScanner = BarcodeScanning.getClient();

        firestoreManager = new FirestoreManager();

        btnVerify.setOnClickListener(v -> {
            // Navigation happens automatically after a successful scan.
        });

        startCameraWithPermission();

        return view;
    }

    // =====================================================
    // CAMERA PERMISSION
    // =====================================================

    private void startCameraWithPermission() {

        cameraPermissionLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.RequestPermission(),
                        isGranted -> {

                            if (isGranted) {

                                startCamera();

                            } else {

                                Toast.makeText(
                                        requireContext(),
                                        "Camera Permission Required",
                                        Toast.LENGTH_SHORT
                                ).show();

                            }

                        });

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {

            startCamera();

        } else {

            cameraPermissionLauncher.launch(
                    Manifest.permission.CAMERA);

        }

    }

    // =====================================================
    // START CAMERA
    // =====================================================

    private void startCamera() {

        cameraProviderFuture =
                ProcessCameraProvider.getInstance(requireContext());

        cameraProviderFuture.addListener(() -> {

            try {

                ProcessCameraProvider cameraProvider =
                        cameraProviderFuture.get();

                Preview preview =
                        new Preview.Builder().build();

                ImageAnalysis imageAnalysis =
                        new ImageAnalysis.Builder()
                                .setBackpressureStrategy(
                                        ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                .build();

                preview.setSurfaceProvider(
                        previewView.getSurfaceProvider());

                imageAnalysis.setAnalyzer(
                        ContextCompat.getMainExecutor(requireContext()),
                        this::processImage);

                CameraSelector cameraSelector =
                        CameraSelector.DEFAULT_BACK_CAMERA;

                cameraProvider.unbindAll();

                cameraProvider.bindToLifecycle(
                        getViewLifecycleOwner(),
                        cameraSelector,
                        preview,
                        imageAnalysis);

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(
                        requireContext(),
                        e.getMessage(),
                        Toast.LENGTH_SHORT
                ).show();

            }

        }, ContextCompat.getMainExecutor(requireContext()));

    }

    // =====================================================
    // PROCESS QR IMAGE
    // =====================================================

    private void processImage(ImageProxy imageProxy) {

        if (isScanning) {

            imageProxy.close();
            return;

        }

        if (imageProxy.getImage() == null) {

            imageProxy.close();
            return;

        }

        InputImage image =
                InputImage.fromMediaImage(
                        imageProxy.getImage(),
                        imageProxy.getImageInfo().getRotationDegrees());

        barcodeScanner.process(image)

                .addOnSuccessListener(barcodes -> {

                    if (!barcodes.isEmpty()) {

                        isScanning = true;

                        String rawValue =
                                barcodes.get(0).getRawValue();

                        if (rawValue != null) {

                            parseQRCode(rawValue);

                        }

                    }

                })

                .addOnCompleteListener(task ->
                        imageProxy.close());

    }

    // =====================================================
    // PARSE QR
    // =====================================================

    private void parseQRCode(String qrData) {

        String patientId = "";
        String token = "";
        String name = "";
        String department = "";

        try {

            JSONObject object = new JSONObject(qrData);

            patientId = object.getString("patientId");
            token = object.getString("token");
            name = object.getString("patientName");
            department = object.getString("department");

        } catch (Exception e) {

            Toast.makeText(
                    requireContext(),
                    "Invalid QR Code",
                    Toast.LENGTH_SHORT
            ).show();

            isScanning = false;
            return;
        }

        if (patientId.isEmpty()) {

            Toast.makeText(
                    requireContext(),
                    "Patient ID not found in QR",
                    Toast.LENGTH_SHORT
            ).show();

            isScanning = false;
            return;
        }
        tvToken.setText(token);

        firestoreManager.searchPatientById(
                patientId,
                new FirestoreManager.SearchCallback() {

                    @Override
                    public void onSuccess(PatientModel patient) {
                        tvToken.setText(patient.getTokenNumber());
                        tvPatient.setText(patient.getPatientName());

                        btnVerify.setEnabled(true);

                        Bundle bundle = new Bundle();

                        bundle.putSerializable("patient", patient);

                        PatientDetailsFragment fragment =
                                new PatientDetailsFragment();

                        fragment.setArguments(bundle);

                        requireActivity()
                                .getSupportFragmentManager()
                                .beginTransaction()
                                .replace(
                                        R.id.frameLayout,
                                        fragment)
                                .addToBackStack(null)
                                .commit();
                        isScanning = false;

                    }

                    @Override
                    public void onFailure(String message) {

                        tvPatient.setText("Patient Not Found");

                        btnVerify.setEnabled(false);

                        isScanning = false;

                        Toast.makeText(
                                requireContext(),
                                message,
                                Toast.LENGTH_SHORT
                        ).show();

                    }

                });

    }

}