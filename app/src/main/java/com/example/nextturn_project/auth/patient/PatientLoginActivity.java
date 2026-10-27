package com.example.nextturn_project.auth.patient;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nextturn_project.R;
import com.example.nextturn_project.patient.PatientDashboardActivity;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.TimeUnit;

public class PatientLoginActivity extends AppCompatActivity {

    private RadioButton rbPassword, rbOTP;
    private LinearLayout passwordLayout, otpLayout;

    private EditText etPhone, etPassword, etOTP;

    private Button btnLogin, btnSendOTP, btnVerifyOTP;

    private TextView txtRegister, txtForgot;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private String verificationId;

    private PhoneAuthProvider.ForceResendingToken resendToken;

    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean isLoggedIn = getSharedPreferences(
                "PatientSession",
                MODE_PRIVATE)
                .getBoolean("isLoggedIn", false);

        if (isLoggedIn) {

            Intent intent = new Intent(
                    PatientLoginActivity.this,
                    PatientDashboardActivity.class);

            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_patient_login);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        rbPassword = findViewById(R.id.rbPassword);
        rbOTP = findViewById(R.id.rbOTP);

        passwordLayout = findViewById(R.id.passwordLayout);
        otpLayout = findViewById(R.id.otpLayout);

        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        etOTP = findViewById(R.id.etOTP);

        btnLogin = findViewById(R.id.btnLogin);
        btnSendOTP = findViewById(R.id.btnSendOTP);
        btnVerifyOTP = findViewById(R.id.btnVerifyOTP);

        txtRegister = findViewById(R.id.txtRegister);
        txtForgot = findViewById(R.id.txtForgot);

        rbPassword.setChecked(true);
        passwordLayout.setVisibility(View.VISIBLE);
        otpLayout.setVisibility(View.GONE);

        rbPassword.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked) {

                passwordLayout.setVisibility(View.VISIBLE);
                otpLayout.setVisibility(View.GONE);

            }

        });

        rbOTP.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked) {

                passwordLayout.setVisibility(View.GONE);
                otpLayout.setVisibility(View.VISIBLE);

            }

        });

        txtRegister.setOnClickListener(v -> {

            startActivity(new Intent(
                    PatientLoginActivity.this,
                    PatientRegisterActivity.class));

        });

        txtForgot.setOnClickListener(v -> {

            Intent intent = new Intent(
                    PatientLoginActivity.this,
                    ForgotPasswordActivity.class);

            startActivity(intent);

        });

        btnLogin.setOnClickListener(v -> loginWithPassword());

        btnSendOTP.setOnClickListener(v -> sendOTP());

        btnVerifyOTP.setOnClickListener(v -> verifyOTP());

    }

    //=====================================================
    // PHONE + PASSWORD LOGIN
    //=====================================================

    private void loginWithPassword() {

        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (phone.isEmpty()) {

            etPhone.setError("Enter Phone Number");
            return;

        }

        if (phone.length() != 10) {

            etPhone.setError("Enter Valid Phone Number");
            return;

        }

        if (password.isEmpty()) {

            etPassword.setError("Enter Password");
            return;

        }

        db.collection("patients")
                .whereEqualTo("phone", phone)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    if (queryDocumentSnapshots.isEmpty()) {

                        Toast.makeText(
                                PatientLoginActivity.this,
                                "Phone Number Not Registered",
                                Toast.LENGTH_SHORT).show();

                        return;

                    }

                    String dbPassword = queryDocumentSnapshots
                            .getDocuments()
                            .get(0)
                            .getString("password");

                    if (password.equals(dbPassword)) {

                        Toast.makeText(
                                PatientLoginActivity.this,
                                "Login Successful",
                                Toast.LENGTH_SHORT).show();

                        // Save Login Session
                        getSharedPreferences("PatientSession", MODE_PRIVATE)
                                .edit()
                                .putBoolean("isLoggedIn", true)
                                .apply();

                        Intent intent = new Intent(
                                PatientLoginActivity.this,
                                PatientDashboardActivity.class);

                        startActivity(intent);
                        finish();

                    } else {

                        Toast.makeText(
                                PatientLoginActivity.this,
                                "Incorrect Password",
                                Toast.LENGTH_SHORT).show();

                    }

                })
                .addOnFailureListener(e ->

                        Toast.makeText(
                                PatientLoginActivity.this,
                                e.getMessage(),
                                Toast.LENGTH_LONG).show());

    }

    //=====================================================
    // SEND OTP
    //=====================================================

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
                "OTP Sent",
                Toast.LENGTH_SHORT).show();

        startTimer();

    }

    //=====================================================
    // TIMER
    //=====================================================

    private void startTimer() {

        if (countDownTimer != null) {

            countDownTimer.cancel();

        }

        countDownTimer = new CountDownTimer(30000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

                btnSendOTP.setEnabled(false);

                btnSendOTP.setText(
                        "Resend (" +
                                millisUntilFinished / 1000 +
                                "s)");

            }

            @Override
            public void onFinish() {

                btnSendOTP.setEnabled(true);

                btnSendOTP.setText("Send OTP");

            }

        }.start();

    }

    //=====================================================
    // CALLBACKS
    //=====================================================

    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                @Override
                public void onVerificationCompleted(
                        @NonNull PhoneAuthCredential credential) {

                    signIn(credential);

                }

                @Override
                public void onVerificationFailed(
                        @NonNull FirebaseException e) {

                    Toast.makeText(
                            PatientLoginActivity.this,
                            e.getMessage(),
                            Toast.LENGTH_LONG).show();

                }

                @Override
                public void onCodeSent(
                        @NonNull String verificationId,
                        @NonNull PhoneAuthProvider.ForceResendingToken token) {

                    PatientLoginActivity.this.verificationId = verificationId;

                    resendToken = token;

                }

            };

    //=====================================================
    // VERIFY OTP
    //=====================================================

    private void verifyOTP() {

        String otp = etOTP.getText().toString().trim();

        if (otp.length() != 6) {

            etOTP.setError("Enter Valid OTP");

            return;

        }

        PhoneAuthCredential credential =
                PhoneAuthProvider.getCredential(
                        verificationId,
                        otp);

        signIn(credential);

    }

    //=====================================================
    // SIGN IN
    //=====================================================

    private void signIn(PhoneAuthCredential credential) {

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        Toast.makeText(
                                PatientLoginActivity.this,
                                "OTP Login Successful",
                                Toast.LENGTH_SHORT).show();

                        // Save Login Session
                        getSharedPreferences("PatientSession", MODE_PRIVATE)
                                .edit()
                                .putBoolean("isLoggedIn", true)
                                .apply();

                        Intent intent = new Intent(
                                PatientLoginActivity.this,
                                PatientDashboardActivity.class);

                        startActivity(intent);
                        finish();

                    } else {

                        Toast.makeText(
                                PatientLoginActivity.this,
                                "Invalid OTP",
                                Toast.LENGTH_SHORT).show();

                    }

                });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

}