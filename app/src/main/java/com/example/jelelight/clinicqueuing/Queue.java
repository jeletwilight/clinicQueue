package com.example.jelelight.clinicqueuing;

public class Queue {
    private String patientID;
    private String patientCase;
    private String status;
    private String patientName;

    public Queue(){
    }

    public Queue(String patientID,String patientName,String patientCase,String status){
        this.patientID = patientID;
        this.patientName = patientName;
        this.patientCase = patientCase;
        this.status = status;
    }

    public String getPatientCase() {
        return patientCase;
    }

    public void setPatientCase(String patientCase) {
        this.patientCase = patientCase;
    }

    public String getPatientID() {
        return patientID;
    }

    public void setPatientID(String patientID) {
        this.patientID = patientID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }
}
