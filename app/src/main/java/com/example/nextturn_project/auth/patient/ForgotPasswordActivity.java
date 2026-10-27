package com.example.nextturn_project.auth.patient;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nextturn_project.R;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.TimeUnit;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText etPhone;
    private EditText etOTP;
    private EditText etNewPassword;
    private EditText etConfirmPassword;

    private Button btnSendOTP;
    private Button btnResetPassword;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private String verificationId;

    private PhoneAuthProvider.ForceResendingToken resendToken;

    private CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        initViews();

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        btnSendOTP.setOnClickListener(v -> sendOTP());

        btnResetPassword.setOnClickListener(v -> verifyOTP());

    }

    private void initViews() {

        etPhone = findViewById(R.id.etPhone);
        etOTP = findViewById(R.id.etOTP);

        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        btnSendOTP = findViewById(R.id.btnSendOTP);
        btnResetPassword = findViewById(R.id.btnResetPassword);

    }

    //==================================================
    // SEND OTP
    //==================================================

    private void sendOTP() {

        String phone = etPhone.getText().toString().trim();

        if (phone.isEmpty()) {

            etPhone.setError("Enter Phone Number");
            return;

        }

        if (phone.length() != 10) {

            etPhone.setError("Enter Valid Phone Number");
            return;

        }

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+91" + phone)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(callbacks)
                        .build();

        PhoneAuthProvider.verifyPhoneNumber(options);

        Toast.makeText(
                this,
                "OTP Sent Successfully",
                Toast.LENGTH_SHORT
        ).show();

        startTimer();

    }
    //==================================================
// VERIFY OTP
//==================================================

    private void verifyOTP() {

        String otp = etOTP.getText().toString().trim();

        String newPassword = etNewPassword.getText().toString().trim();

        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (otp.isEmpty()) {

            etOTP.setError("Enter OTP");
            return;

        }

        if (otp.length() != 6) {

            etOTP.setError("Enter Valid OTP");
            return;

        }

        if (newPassword.isEmpty()) {

            etNewPassword.setError("Enter New Password");
            return;

        }

        if (newPassword.length() < 6) {

            etNewPassword.setError("Password must be at least 6 characters");
            return;

        }

        if (confirmPassword.isEmpty()) {

            etConfirmPassword.setError("Confirm Password");
            return;

        }

        if (!newPassword.equals(confirmPassword)) {

            etConfirmPassword.setError("Passwords do not match");
            return;

        }

        PhoneAuthCredential credential =
                PhoneAuthProvider.getCredential(
                        verificationId,
                        otp
                );

        verifyCredential(credential);

    }
    //==================================================
// VERIFY FIREBASE CREDENTIAL
//==================================================

    private void verifyCredential(PhoneAuthCredential credential) {

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        Toast.makeText(
                                ForgotPasswordActivity.this,
                                "OTP Verified Successfully",
                                Toast.LENGTH_SHORT
                        ).show();

                        updatePassword();

                    } else {

                        Toast.makeText(
                                ForgotPasswordActivity.this,
                                "Invalid OTP",
                                Toast.LENGTH_SHORT
                        ).show();

                    }

                });

    }
    //==================================================
// UPDATE PASSWORD IN FIRESTORE
//==================================================

    private void updatePassword() {

        String phone = etPhone.getText().toString().trim();

        String newPassword = etNewPassword.getText().toString().trim();

        db.collection("patients")
                .whereEqualTo("phone", phone)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    if (queryDocumentSnapshots.isEmpty()) {

                        Toast.makeText(
                                ForgotPasswordActivity.this,
                                "Phone Number Not Registered",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;

                    }

                    String documentId =
                            queryDocumentSnapshots
                                    .getDocuments()
                                    .get(0)
                                    .getId();

                    db.collection("patients")
                            .document(documentId)
                            .update("password", newPassword)
                            .addOnSuccessListener(unused -> {

                                Toast.makeText(
                                        ForgotPasswordActivity.this,
                                        "Password Reset Successfully",
                                        Toast.LENGTH_LONG
                                ).show();

                                Intent intent = new Intent(
                                        ForgotPasswordActivity.this,
                                        PatientLoginActivity.class
                                );

                                intent.setFlags(
                                        Intent.FLAG_ACTIVITY_NEW_TASK
                                                | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                );

                                startActivity(intent);

                                finish();

                            })

                            .addOnFailureListener(e ->

                                    Toast.makeText(
                                            ForgotPasswordActivity.this,
                                            e.getMessage(),
                                            Toast.LENGTH_LONG
                                    ).show());

                })

                .addOnFailureListener(e ->

                        Toast.makeText(
                                ForgotPasswordActivity.this,
                                e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show());

    }

    //==================================================
    // TIMER
    //==================================================

    private void startTimer() {

        if (timer != null)
            timer.cancel();

        timer = new CountDownTimer(30000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

                btnSendOTP.setEnabled(false);

                btnSendOTP.setText(
                        "Resend (" +
                                millisUntilFinished / 1000 +
                                "s)"
                );

            }

            @Override
            public void onFinish() {

                btnSendOTP.setEnabled(true);

                btnSendOTP.setText("Send OTP");

            }

        }.start();

    }

    //==================================================
    // CALLBACKS
    //==================================================

    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                @Override
                public void onVerificationCompleted(
                        @NonNull PhoneAuthCredential credential) {

                    Toast.makeText(
                            ForgotPasswordActivity.this,
                            "OTP Auto Verified",
                            Toast.LENGTH_SHORT
                    ).show();

                }

                @Override
                public void onVerificationFailed(
                        @NonNull FirebaseException e) {

                    Toast.makeText(
                            ForgotPasswordActivity.this,
                            e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();

                }

                @Override
                public void onCodeSent(
                        @NonNull String id,
                        @NonNull PhoneAuthProvider.ForceResendingToken token) {

                    verificationId = id;

                    resendToken = token;

                }

            };

    @Override
    protected void onDestroy() {

        super.onDestroy();

        if (timer != null)
            timer.cancel();

    }

}