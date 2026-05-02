package com.data_management;

import com.alerts.Alert;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a staff member who can receive and hold patient-related {@link Alert} notifications.
 *
 * <p>Each staff member is identified by an ID and maintains a list of alerts
 * that have been assigned to them.</p>
 */
public class Staff {
    private int staffId;
    private List<Alert> alerts= new ArrayList<>();

    /**
     * Constructs a {@code Staff} instance with the given staff ID.
     *
     * @param staffId the identifier for this staff member
     */
    public Staff(int staffId) {
        this.staffId = staffId;
    }

    /**
     * Adds an alert to this staff member's list of received alerts.
     *
     * @param alert the {@link Alert} to assign to this staff member
     */
    public void addAlert(Alert alert) {
        alerts.add(alert);
    }

    public int getStaffId() {return staffId;}

    public List<Alert> getAlerts() {return alerts;}
}
