package com.alerts;

import com.data_management.Staff;

import java.util.ArrayList;
import java.util.List;

public class AlertManager {

    public void dispatchAlert(Alert alert, List<Staff> staffMembers) {
        for (Staff staff : staffMembers) {
            staff.addAlert(alert);
        }
    }
}
