package com.example.nextturn_project.manager;

import com.example.nextturn_project.firebase.FirebaseHelper;
import com.example.nextturn_project.staff.model.PatientModel;
import com.example.nextturn_project.utils.DepartmentTokenManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Transaction;

public class FirestoreManager {

    private final FirebaseFirestore db;

    public FirestoreManager() {
        db = FirebaseHelper.getFirestore();
    }

    // ==========================================
// Search Patient by UID
// ==========================================

    public void searchPatientById(String patientId,
                                  SearchCallback callback) {

        db.collection("patients")
                .document(patientId)
                .get()
                .addOnSuccessListener(document -> {

                    if (!document.exists()) {
                        callback.onFailure("Patient Not Found");
                        return;
                    }

                    PatientModel patient =
                            document.toObject(PatientModel.class);

                    callback.onSuccess(patient);

                })
                .addOnFailureListener(e ->
                        callback.onFailure(e.getMessage()));
    }

    // ===========================
    // Register Patient Callback
    // ===========================

    public interface RegisterCallback {
        void onSuccess(String token);
        void onFailure(String message);
    }

    // ===========================
    // Register Patient
    // ===========================

    public void registerPatient(PatientModel patient,
                                RegisterCallback callback) {

        DocumentReference departmentRef =
                db.collection("department_tokens")
                        .document(patient.getDepartment());

        db.runTransaction((Transaction.Function<String>) transaction -> {

                    Long lastToken =
                            transaction.get(departmentRef).getLong("lastToken");

                    if (lastToken == null)
                        lastToken = 0L;

                    long nextToken = lastToken + 1;

                    transaction.update(
                            departmentRef,
                            "lastToken",
                            nextToken
                    );

                    String generatedToken =
                            DepartmentTokenManager.generateToken(
                                    patient.getDepartment(),
                                    (int) nextToken
                            );

                    DocumentReference patientRef =
                            db.collection("patients")
                                    .document();

                    patient.setPatientId(patientRef.getId());
                    patient.setTokenNumber(generatedToken);
                    patient.setQueueStatus("Waiting");

                    transaction.set(patientRef, patient);

                    return generatedToken;

                }).addOnSuccessListener(callback::onSuccess)
                .addOnFailureListener(e ->
                        callback.onFailure(e.getMessage()));
    }

    // ===========================
    // Status Callback
    // ===========================

    public interface StatusCallback {
        void onSuccess();
        void onFailure(String message);
    }
    public interface SearchCallback {
        void onSuccess(PatientModel patient);
        void onFailure(String message);
    }

    // ===========================
    // Update Patient Status (call next)
    // ===========================

    public void updatePatientStatus(String patientId,
                                    String status,
                                    StatusCallback callback) {

        db.collection("patients")
                .document(patientId)
                .update("queueStatus", status)
                .addOnSuccessListener(unused ->
                        callback.onSuccess())
                .addOnFailureListener(e ->
                        callback.onFailure(e.getMessage()));
    }
    public void searchPatient(String keyword,
                              SearchCallback callback) {

        db.collection("patients")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    for (var document : queryDocumentSnapshots.getDocuments()) {

                        PatientModel patient =
                                document.toObject(PatientModel.class);

                        if (patient == null)
                            continue;

                        if ((patient.getTokenNumber() != null &&
                                patient.getTokenNumber().equalsIgnoreCase(keyword))
                                ||

                                (patient.getPatientName() != null &&
                                        patient.getPatientName().equalsIgnoreCase(keyword))
                                ||

                                (patient.getMobile() != null &&
                                        patient.getMobile().equals(keyword))) {

                            callback.onSuccess(patient);
                            return;
                        }

                    }

                    callback.onFailure("Patient Not Found");

                })
                .addOnFailureListener(e ->
                        callback.onFailure(e.getMessage()));

    }
    // ===========================
// Get Patient by Token
// ===========================

    public void getPatientByToken(String token, SearchCallback callback) {

        db.collection("patients")
                .whereEqualTo("tokenNumber", token)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    if (queryDocumentSnapshots.isEmpty()) {
                        callback.onFailure("Patient Not Found");
                        return;
                    }

                    PatientModel patient =
                            queryDocumentSnapshots.getDocuments()
                                    .get(0)
                                    .toObject(PatientModel.class);

                    callback.onSuccess(patient);
                })
                .addOnFailureListener(e ->
                        callback.onFailure(e.getMessage()));
    }
    public void getNextPatient(StatusCallback callback) {

        db.collection("patients")
                .whereEqualTo("queueStatus", "Waiting")
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    if (!queryDocumentSnapshots.isEmpty()) {

                        var document = queryDocumentSnapshots.getDocuments().get(0);

                        String patientId = document.getId();

                        db.collection("patients")
                                .document(patientId)
                                .update("queueStatus", "Called")
                                .addOnSuccessListener(unused -> callback.onSuccess())
                                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));

                    } else {
                        callback.onFailure("No Waiting Patients");
                    }

                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }
    public void getAllWaitingPatients(SearchCallback callback) {

        db.collection("patients")
                .whereEqualTo("queueStatus", "Waiting")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    if (queryDocumentSnapshots.isEmpty()) {
                        callback.onFailure("No Patients in Queue");
                        return;
                    }

                    for (var document : queryDocumentSnapshots.getDocuments()) {

                        PatientModel patient =
                                document.toObject(PatientModel.class);

                        if (patient != null) {
                            callback.onSuccess(patient);
                        }
                    }
                })
                .addOnFailureListener(e ->
                        callback.onFailure(e.getMessage()));
    }
    // ==========================================
    // Get Patient by ID
    // ==========================================

    public void getPatientById(String patientId,
                               SearchCallback callback) {

        db.collection("patients")
                .document(patientId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (!documentSnapshot.exists()) {
                        callback.onFailure("Patient not found");
                        return;
                    }

                    PatientModel patient =
                            documentSnapshot.toObject(PatientModel.class);

                    callback.onSuccess(patient);

                })
                .addOnFailureListener(e ->
                        callback.onFailure(e.getMessage()));
    }

    // ==========================================
    // Update Patient
    // ==========================================

    public void updatePatient(PatientModel patient,
                              StatusCallback callback) {

        db.collection("patients")
                .document(patient.getPatientId())
                .set(patient)
                .addOnSuccessListener(unused ->
                        callback.onSuccess())
                .addOnFailureListener(e ->
                        callback.onFailure(e.getMessage()));
    }

    // ==========================================
    // Delete Patient
    // ==========================================

    public void deletePatient(String patientId,
                              StatusCallback callback) {

        db.collection("patients")
                .document(patientId)
                .delete()
                .addOnSuccessListener(unused ->
                        callback.onSuccess())
                .addOnFailureListener(e ->
                        callback.onFailure(e.getMessage()));
    }

    // ==========================================
    // Get All Patients
    // ==========================================

    public void getAllPatients(SearchCallback callback) {

        db.collection("patients")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    if (queryDocumentSnapshots.isEmpty()) {
                        callback.onFailure("No Patients Found");
                        return;
                    }

                    for (var document : queryDocumentSnapshots.getDocuments()) {

                        PatientModel patient =
                                document.toObject(PatientModel.class);

                        if (patient != null) {
                            callback.onSuccess(patient);
                        }
                    }

                })
                .addOnFailureListener(e ->
                        callback.onFailure(e.getMessage()));
    }

    // ==========================================
    // Get Completed Patients
    // ==========================================

    public void getCompletedPatients(SearchCallback callback) {

        db.collection("patients")
                .whereEqualTo("queueStatus", "Completed")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    if (queryDocumentSnapshots.isEmpty()) {
                        callback.onFailure("No Completed Patients");
                        return;
                    }

                    for (var document : queryDocumentSnapshots.getDocuments()) {

                        PatientModel patient =
                                document.toObject(PatientModel.class);

                        if (patient != null) {
                            callback.onSuccess(patient);
                        }
                    }

                })
                .addOnFailureListener(e ->
                        callback.onFailure(e.getMessage()));
    }

    // ==========================================
    // Get In Progress Patients
    // ==========================================

    public void getInProgressPatients(SearchCallback callback) {

        db.collection("patients")
                .whereEqualTo("queueStatus", "In Progress")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    if (queryDocumentSnapshots.isEmpty()) {
                        callback.onFailure("No Patients In Progress");
                        return;
                    }

                    for (var document : queryDocumentSnapshots.getDocuments()) {

                        PatientModel patient =
                                document.toObject(PatientModel.class);

                        if (patient != null) {
                            callback.onSuccess(patient);
                        }
                    }

                })
                .addOnFailureListener(e ->
                        callback.onFailure(e.getMessage()));
    }

}