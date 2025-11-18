package com.qdmh.studentsmartcardqdmh.client.app.Card;

public class MedicalHistory {
    String PatientId;
    String MedicalId;
    String MedicalHistoryDate;
    String MedicalDescription;
    String MedicalName;
    String MedicalStatus;
    String MedicalCost;

    public MedicalHistory(String patientId, String medicalId, String medicalHistoryDate, String medicalName, String medicalDescription, String medicalStatus, String medicalCost) {
        PatientId = patientId;
        MedicalId = medicalId;
        MedicalHistoryDate = medicalHistoryDate;
        MedicalName = medicalName;
        MedicalDescription = medicalDescription;
        MedicalStatus = medicalStatus;
        MedicalCost = medicalCost;
    }

    public String getMedicalId() {
        return MedicalId;
    }

    public void setMedicalId(String medicalId) {
        MedicalId = medicalId;
    }

    public String getMedicalDescription() {
        return MedicalDescription;
    }

    public void setMedicalDescription(String medicalDescription) {
        MedicalDescription = medicalDescription;
    }

    public String getMedicalHistoryDate() {
        return MedicalHistoryDate;
    }

    public void setMedicalHistoryDate(String medicalHistoryDate) {
        MedicalHistoryDate = medicalHistoryDate;
    }

    public String getMedicalCost() {
        return MedicalCost;
    }

    public void setMedicalCost(String medicalCost) {
        MedicalCost = medicalCost;
    }

    public String getMedicalStatus() {
        return MedicalStatus;
    }

    public void setMedicalStatus(String medicalStatus) {
        MedicalStatus = medicalStatus;
    }
}
