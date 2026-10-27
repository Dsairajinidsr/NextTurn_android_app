package com.example.nextturn_project.staff.model;

import java.io.Serializable;

public class QueueModel implements Serializable {

    private String patientId;
    private String patientName;
    private String tokenNumber;
    private String department;
    private String queueStatus;

    public QueueModel() {
        // Required for Firestore
    }

    public QueueModel(String patientId,
                      String patientName,
                      String tokenNumber,
                      String department,
                      String queueStatus) {

        this.patientId = patientId;
        this.patientName = patientName;
        this.tokenNumber = tokenNumber;
        this.department = department;
        this.queueStatus = queueStatus;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getTokenNumber() {
        return tokenNumber;
    }

    public void setTokenNumber(String tokenNumber) {
        this.tokenNumber = tokenNumber;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getQueueStatus() {
        return queueStatus;
    }

    public void setQueueStatus(String queueStatus) {
        this.queueStatus = queueStatus;
    }
}