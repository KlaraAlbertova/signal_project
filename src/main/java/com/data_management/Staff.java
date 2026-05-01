package com.data_management;

import com.alerts.Alert;

import java.util.ArrayList;
import java.util.List;

public class Staff {
    private int staffId;
    private String role;
    private List<Alert> alerts= new ArrayList<>();

    public Staff(int staffId, String role) {
        this.staffId = staffId;
        this.role = role;
    }

    public void addAlert(Alert alert) {
        alerts.add(alert);
    }

    public int getStaffId() {return staffId;}
}
