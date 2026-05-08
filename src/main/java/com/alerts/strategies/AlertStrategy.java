package com.alerts.strategies;

import com.alerts.AlertManager;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.List;

interface AlertStrategy {
     void checkAlert(Patient patient, List<PatientRecord> records, AlertManager alertManager);
}
