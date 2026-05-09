package com.alerts.decorators;

import com.alerts.AlertManager;
import com.alerts.alerts.Alert;
import com.data_management.Staff;

import java.util.List;

/**
 * Decorator that repeatedly dispatches an alert over a set interval.
 *
 * <p>Calls {@link AlertManager#dispatchAlert} a specified number of times,
 * pausing for a given interval in milliseconds between each dispatch.</p>
 */
public class RepeatedAlertDecorator extends AlertDecorator {
    private int repeatCount;
    private long intervalMs;

    /**
     * Constructs a RepeatedAlertDecorator with the given repeat settings.
     *
     * @param decoratedAlert the alert to wrap
     * @param repeatCount    the number of times to repeat the alert
     * @param intervalMs     the pause in milliseconds between each repeat
     */
    public RepeatedAlertDecorator(Alert decoratedAlert, int repeatCount, long intervalMs) {
        super(decoratedAlert);
        this.repeatCount = repeatCount;
        this.intervalMs = intervalMs;
    }

    /**
     * Repeatedly dispatches the wrapped alert to the given staff members,
     * waiting {@code intervalMs} milliseconds between each dispatch.
     *
     * @param alertManager the alert manager used to dispatch the alert
     * @param staff        the list of staff members to notify
     */
    public void checkAndRepeat(AlertManager alertManager, List<Staff> staff) {
        for (int i = 0; i < repeatCount; i++) {
            alertManager.dispatchAlert(decoratedAlert, staff);
            try {
                Thread.sleep(intervalMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    /**
     * Returns the number of times the alert will be repeated.
     *
     * @return the repeat count
     */
    public int getRepeatCount() {
        return repeatCount;
    }

    /**
     * Returns the interval in milliseconds between each repeat.
     *
     * @return the interval in milliseconds
     */
    public long getIntervalMs() {
        return intervalMs;
    }
}
