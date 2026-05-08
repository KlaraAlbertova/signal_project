package com.alerts.factories;

import com.alerts.alerts.Alert;
import com.alerts.alerts.BloodOxygenAlert;

public class BloodOxygenAlertFactory extends AlertFactory {
    @Override
    public Alert createAlert(String patientId, String condition, long timestamp){
        return new BloodOxygenAlert(patientId, condition, timestamp);
    }
}

