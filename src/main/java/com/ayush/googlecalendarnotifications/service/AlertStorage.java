package com.ayush.googlecalendarnotifications.service;

import com.ayush.googlecalendarnotifications.dto.AlertTask;
import com.ayush.googlecalendarnotifications.dto.AlertType;
import com.ayush.googlecalendarnotifications.dto.Meeting;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AlertStorage {
    // Key: The specific Minute (Epoch Minute), Value: List of Alerts to fire then
    private final Map<Long, List<AlertTask>> alertSchedule = new ConcurrentHashMap<>();

    public void rebuildSchedule(List<Meeting> meetings) {
        alertSchedule.clear();
        for (Meeting meeting : meetings) {
            Instant start = Instant.parse(meeting.getStartTime());

            // Generate the 3 "Alert Moments" for this meeting
            createAlert(meeting, start.minus(15, ChronoUnit.MINUTES), AlertType.FIFTEEN_MINUTES_BEFORE);
            createAlert(meeting, start.minus(5, ChronoUnit.MINUTES), AlertType.FIVE_MINUTES_BEFORE);
            createAlert(meeting, start, AlertType.STARTING_NOW);
        }
    }

    private void createAlert(Meeting meeting, Instant alertTime, AlertType type) {
        // Only schedule if the alert time is in the future
        if (alertTime.isAfter(Instant.now())) {
            long minuteEpoch = alertTime.truncatedTo(ChronoUnit.MINUTES).getEpochSecond() / 60;
            alertSchedule.computeIfAbsent(minuteEpoch, k -> Collections.synchronizedList(new ArrayList<>()))
                .add(new AlertTask(meeting.getTitle(), type));
        }
    }

    public List<AlertTask> getAlertsForMinute(long minuteEpoch) {
        return alertSchedule.getOrDefault(minuteEpoch, Collections.emptyList());
    }
}