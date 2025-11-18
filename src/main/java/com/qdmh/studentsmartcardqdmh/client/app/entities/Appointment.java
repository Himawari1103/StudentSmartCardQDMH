package com.qdmh.studentsmartcardqdmh.client.app.entities;

import javax.persistence.*;

@Entity
@Table(name = "appointment")
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    Integer patientId;
    String code;
    String patientName;
    String date;
    String description;
    String name;
    Boolean status;
    int cost;

    public Appointment() {
    }
    public Appointment(Integer id, Integer patientId, String patientName, String date, String description, String name, Boolean status, int cost) {
        this.id = id;
        this.patientId = patientId;
        this.patientName = patientName;
        this.date = date;
        this.description = description;
        this.name = name;
        this.status = status;
        this.cost = cost;
    }
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public Integer getPatientId() {
        return patientId;
    }

    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }
    public void generateAppointmentCode() {
        this.code =  String.format("DK%03d", id);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
