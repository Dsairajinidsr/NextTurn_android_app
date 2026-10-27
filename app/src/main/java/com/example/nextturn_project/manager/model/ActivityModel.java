package com.example.nextturn_project.manager.model;

public class ActivityModel {

    private String tokenNumber;
    private String name;
    private String queueStatus;

    public ActivityModel() {
    }

    public ActivityModel(String tokenNumber, String name, String queueStatus) {
        this.tokenNumber = tokenNumber;
        this.name = name;
        this.queueStatus = queueStatus;
    }

    public String getTokenNumber() {
        return tokenNumber;
    }

    public String getName() {
        return name;
    }

    public String getQueueStatus() {
        return queueStatus;
    }

    public void setTokenNumber(String tokenNumber) {
        this.tokenNumber = tokenNumber;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setQueueStatus(String queueStatus) {
        this.queueStatus = queueStatus;
    }
}