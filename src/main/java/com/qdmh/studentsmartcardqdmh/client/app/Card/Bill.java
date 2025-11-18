package com.qdmh.studentsmartcardqdmh.client.app.Card;

public class Bill {
    String BillId;
    String PatientId;
    String MedicalId;
    String BillCost;
    String PaymentDate;

    public String getBillId() {
        return BillId;
    }

    public void setBillId(String billId) {
        BillId = billId;
    }

    public String getBillCost() {
        return BillCost;
    }

    public void setBillCost(String billCost) {
        BillCost = billCost;
    }

    public String getPaymentDate() {
        return PaymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        PaymentDate = paymentDate;
    }

    public String getMedicalId() {
        return MedicalId;
    }

    public void setMedicalId(String medicalId) {
        MedicalId = medicalId;
    }

    public String getPatientId() {
        return PatientId;
    }

    public void setPatientId(String patientId) {
        PatientId = patientId;
    }
}
