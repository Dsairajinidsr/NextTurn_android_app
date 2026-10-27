package com.example.nextturn_project.staff.model;

import java.io.Serializable;

public class PatientModel implements Serializable {

    private String patientId;
    private String patientName;
    private String age;
    private String gender;
    private String mobile;
    private String department;
    private String symptoms;
    private String priority;

    private String tokenNumber;
    private String queueStatus;

    private String registeredBy;
    private String registrationDate;
    private String registrationTime;

    private boolean isCompleted;

    // Empty constructor required for Firestore
    public PatientModel() {
    }

    public PatientModel(String patientId,
                        String patientName,
                        String age,
                        String gender,
                        String mobile,
                        String department,
                        String symptoms,
                        String priority,
                        String tokenNumber,
                        String queueStatus,
                        String registeredBy,
                        String registrationDate,
                        String registrationTime,
                        boolean isCompleted) {

        this.patientId = patientId;
        this.patientName = patientName;
        this.age = age;
        this.gender = gender;
        this.mobile = mobile;
        this.department = department;
        this.symptoms = symptoms;
        this.priority = priority;
        this.tokenNumber = tokenNumber;
        this.queueStatus = queueStatus;
        this.registeredBy = registeredBy;
        this.registrationDate = registrationDate;
        this.registrationTime = registrationTime;
        this.isCompleted = isCompleted;
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

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getTokenNumber() {
        return tokenNumber;
    }

    public void setTokenNumber(String tokenNumber) {
        this.tokenNumber = tokenNumber;
    }

    public String getQueueStatus() {
        return queueStatus;
    }

    public void setQueueStatus(String queueStatus) {
        this.queueStatus = queueStatus;
    }

    public String getRegisteredBy() {
        return registeredBy;
    }

    public void setRegisteredBy(String registeredBy) {
        this.registeredBy = registeredBy;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getRegistrationTime() {
        return registrationTime;
    }

    public void setRegistrationTime(String registrationTime) {
        this.registrationTime = registrationTime;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}