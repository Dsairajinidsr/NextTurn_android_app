package com.example.nextturn_project.manager.model;

public class ReportModel {

    private String tokenNumber;
    private String name;
    private String department;
    private String queueStatus;
    private String visitDate;

    public ReportModel() {
    }

    public ReportModel(String tokenNumber,
                       String name,
                       String department,
                       String queueStatus,
                       String visitDate) {

        this.tokenNumber = tokenNumber;
        this.name = name;
        this.department = department;
        this.queueStatus = queueStatus;
        this.visitDate = visitDate;
    }

    public String getTokenNumber() {
        return tokenNumber;
    }

    public void setTokenNumber(String tokenNumber) {
        this.tokenNumber = tokenNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(String visitDate) {
        this.visitDate = visitDate;
    }
}