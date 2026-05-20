package com.alerts;

import com.alerts.alerts.Alert;
import com.alerts.decorators.PriorityAlertDecorator;
import com.alerts.decorators.RepeatedAlertDecorator;
import com.data_management.Staff;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the dispatching and tracking of {@link Alert} objects to staff members.
 *
 * <p>When an alert is dispatched, it is dynamically decorated based on its condition severity
 * to attach priority attributes or trigger repeat notifications before it is forwarded to each
 * provided {@link Staff} member and recorded internally.</p>
 */
public class AlertManager {
    private List<Alert> dispatchedAlerts = new ArrayList<>();

    /**
     * Dispatches the given alert to all specified staff members and records it internally.
     * The alert is dynamically wrapped using decorators if critical conditions are detected.
     *
     * @param alert        the alert to be dispatched
     * @param staffMembers the list of staff members who should receive the alert
     */
    public void dispatchAlert(Alert alert, List<Staff> staffMembers) {
        Alert alertToSend = alert;

        if (alert.getCondition().contains("bellow 92%") || alert.getCondition().contains("Rapid drop")) {
            alertToSend = new PriorityAlertDecorator(alertToSend, "HIGH");
        } else if (alert.getCondition().contains("Hypotensive Hypoxemia")) {
            alertToSend = new PriorityAlertDecorator(alertToSend, "CRITICAL");
        }

        if (alert.getCondition().contains("Hypotensive Hypoxemia") && !(alert instanceof RepeatedAlertDecorator)) {
            RepeatedAlertDecorator repeatedAlert = new RepeatedAlertDecorator(alertToSend, 3, 5000);
            repeatedAlert.checkAndRepeat(this, staffMembers);
        } else {
            for (Staff staff : staffMembers) {
                staff.addAlert(alertToSend);
            }
            dispatchedAlerts.add(alertToSend);
        }
    }

    public List<Alert> getDispatchedAlerts() {
        return dispatchedAlerts;
    }
}