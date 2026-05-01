package com.data_management;

import java.util.List;

public class HospitalPatient {
    private int patientId;
    private Patient patient;
    private List<Staff> staff;

    public HospitalPatient(int patientId, Patient patient, List<Staff> staff) {
        this.patientId = patientId;
        this.patient = patient;
        this.staff = staff;
    }
}
