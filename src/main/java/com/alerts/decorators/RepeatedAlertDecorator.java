package com.alerts.decorators;


import com.alerts.AlertManager;
import com.alerts.alerts.Alert;
import com.data_management.Staff;

import java.util.List;

public class RepeatedAlertDecorator extends AlertDecorator {
    private int repeatCount;
    private long intervalMs;

    public RepeatedAlertDecorator(Alert decoratedAlert, int repeatCount, long intervalMs) {
        super(decoratedAlert);
        this.repeatCount = repeatCount;
        this.intervalMs = intervalMs;
    }

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

    @Override
    public String getCondition() {
        return decoratedAlert.getCondition() + " [Repeated " + repeatCount + " times every " + intervalMs + "ms]";
    }

    public int getRepeatCount() {
        return repeatCount;
    }

    public long getIntervalMs() {
        return intervalMs;
    }
}
