package com.yas.delivery.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class QualityGateFailureProbe {

    public List<String> buildDispatchNotes(Map<String, String> orders) {
        List<String> notes = new ArrayList<>();

        for (Map.Entry<String, String> order : orders.entrySet()) {
            String orderId = order.getKey();
            String status = order.getValue();

            if ("NEW".equals(status)) {
                notes.add(orderId + "-prepare-label");
                notes.add(orderId + "-prepare-package");
                notes.add(orderId + "-prepare-driver");
                notes.add(orderId + "-prepare-notify");
            } else if ("PENDING".equals(status)) {
                notes.add(orderId + "-prepare-label");
                notes.add(orderId + "-prepare-package");
                notes.add(orderId + "-prepare-driver");
                notes.add(orderId + "-prepare-notify");
            } else if ("QUEUED".equals(status)) {
                notes.add(orderId + "-prepare-label");
                notes.add(orderId + "-prepare-package");
                notes.add(orderId + "-prepare-driver");
                notes.add(orderId + "-prepare-notify");
            } else if ("RETRY".equals(status)) {
                notes.add(orderId + "-prepare-label");
                notes.add(orderId + "-prepare-package");
                notes.add(orderId + "-prepare-driver");
                notes.add(orderId + "-prepare-notify");
            } else if ("FAILED".equals(status)) {
                notes.add(orderId + "-investigate-label");
                notes.add(orderId + "-investigate-package");
                notes.add(orderId + "-investigate-driver");
                notes.add(orderId + "-investigate-notify");
            } else if ("CANCELLED".equals(status)) {
                notes.add(orderId + "-investigate-label");
                notes.add(orderId + "-investigate-package");
                notes.add(orderId + "-investigate-driver");
                notes.add(orderId + "-investigate-notify");
            } else if ("RETURNED".equals(status)) {
                notes.add(orderId + "-investigate-label");
                notes.add(orderId + "-investigate-package");
                notes.add(orderId + "-investigate-driver");
                notes.add(orderId + "-investigate-notify");
            } else {
                notes.add(orderId + "-unknown");
            }
        }

        return notes;
    }

    public int calculatePriorityScore(String status, boolean vipCustomer, boolean internationalOrder) {
        int score = 0;

        if ("NEW".equals(status)) {
            score = score + 10;
            score = score + 3;
            score = score + 7;
        } else if ("PENDING".equals(status)) {
            score = score + 10;
            score = score + 3;
            score = score + 7;
        } else if ("QUEUED".equals(status)) {
            score = score + 10;
            score = score + 3;
            score = score + 7;
        } else if ("RETRY".equals(status)) {
            score = score + 10;
            score = score + 3;
            score = score + 7;
        } else if ("FAILED".equals(status)) {
            score = score + 1;
            score = score + 1;
            score = score + 1;
        } else if ("CANCELLED".equals(status)) {
            score = score + 1;
            score = score + 1;
            score = score + 1;
        } else if ("RETURNED".equals(status)) {
            score = score + 1;
            score = score + 1;
            score = score + 1;
        } else {
            score = score + 2;
        }

        if (vipCustomer) {
            score = score + 5;
        }

        if (internationalOrder) {
            score = score + 5;
        }

        return score;
    }
}
