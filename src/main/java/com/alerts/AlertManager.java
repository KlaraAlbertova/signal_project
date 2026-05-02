package com.alerts;

import com.data_management.Staff;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the dispatching and tracking of {@link Alert} objects to staff members.
 *
 * <p>When an alert is dispatched, it is forwarded to each provided {@link Staff} member
 * and recorded internally.</p>
 */
public class AlertManager {
    private List<Alert> dispatchedAlerts = new ArrayList<>();

    /**
     * Dispatches the given alert to all specified staff members and records it internally.
     *
     * @param alert        the alert to be dispatched
     * @param staffMembers the list of staff members who should receive the alert
     */
    public void dispatchAlert(Alert alert, List<Staff> staffMembers) {
        for (Staff staff : staffMembers) {
            staff.addAlert(alert);
        }
        dispatchedAlerts.add(alert);
    }

    public List<Alert> getDispatchedAlerts() {
        return dispatchedAlerts;
    }
}
