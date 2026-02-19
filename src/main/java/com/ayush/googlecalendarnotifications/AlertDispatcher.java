package com.ayush.googlecalendarnotifications;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
            log.info("🚨 [{}] {}: {}", Instant.now(), alert.getType(), alert.getTitle());
            sendToIphone(alert.getTitle(), alert.getType());
            messagingTemplate.convertAndSend("/topic/meetings", alert);
        });
    }

    private void sendToIphone(String title, AlertType type) {
        String topic = "wise-ayush-cal-queshshsisksbxislansoalsb"; // Must match what you typed in the app
        String message = type + ": " + title;

        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://ntfy.sh/" + topic))
                .POST(HttpRequest.BodyPublishers.ofString(message))
                .header("Title", "Calendar Alert")
                .header("Priority", "5") // Makes it pop up immediately
                .header("Tags", "calendar,bell") // Adds emojis to the notification
                .build();

            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            System.err.println("Failed to send notification: " + e.getMessage());
        }
    }

    private void blastMacAlert(String title, AlertType type) {
        String meetLink = "https://meet.google.com/abc-defg-hij"; // Replace with actual link if needed
        // This script creates a central dialog box with a "Join" and "Dismiss" button
        String script = String.format(
            "tell app \"System Events\" to display dialog \"%s\" " +
                "with title \"Meeting Alert\" " +
                "buttons {\"Dismiss\", \"JOIN NOW\"} " +
                "default button \"JOIN NOW\" " +
                "with icon caution", // Adds the yellow warning triangle icon
            type,
            title
        );

        try {
            Process b = new ProcessBuilder("osascript", "-e", script).start();

            // Optional: Listen for the button click
            // If you want to open the link when "JOIN NOW" is clicked:
            new Thread(() -> {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(b.getInputStream()));
                    String response = reader.readLine();
                    if (response != null && response.contains("JOIN NOW")) {
                        Runtime.getRuntime().exec("open " + meetLink);
                    }
                } catch (Exception e) { e.printStackTrace(); }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

