package com.ayush.googlecalendarnotifications.service;

import com.ayush.googlecalendarnotifications.dto.AlertTask;
import com.ayush.googlecalendarnotifications.dto.AlertType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
@EnableScheduling
public class AlertDispatcher {

    @Value("${ntfy.topic}")
    private String ntfyTopic;
    private final AlertStorage storage;
    private final SimpMessagingTemplate messagingTemplate;

    public AlertDispatcher(AlertStorage storage, SimpMessagingTemplate messagingTemplate) {
        this.storage = storage;
        this.messagingTemplate = messagingTemplate;
    }

    @Scheduled(cron = "0 * * * * *") // Runs exactly at the start of every minute
    public void dispatch() {
        long currentMinuteEpoch = Instant.now().truncatedTo(ChronoUnit.MINUTES).getEpochSecond() / 60;
        List<AlertTask> alerts = storage.getAlertsForMinute(currentMinuteEpoch);
        alerts.forEach(alert -> {
            log.info("[{}] {}: {}", Instant.now(), alert.getType(), alert.getTitle());
            ntfy(alert.getTitle(), alert.getType());
            wsNotify(alert);
        });
    }

    private void wsNotify(AlertTask alert) {
        messagingTemplate.convertAndSend("/topic/meetings", alert);
    }

    private void ntfy(String title, AlertType type) {
        String message = type + ": " + title;
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://ntfy.sh/" + ntfyTopic))
                .POST(HttpRequest.BodyPublishers.ofString(message))
                .header("Title", "Calendar Alert")
                .header("Priority", "5") // Makes it pop up immediately
                .header("Tags", "calendar,bell") // Adds emojis to the notification
                .build();

            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            log.error("Failed to send notification: {}", e.getMessage(), e);
        }
    }
}

