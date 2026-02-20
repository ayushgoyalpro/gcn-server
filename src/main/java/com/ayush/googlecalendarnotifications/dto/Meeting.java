package com.ayush.googlecalendarnotifications.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object representing a Google Calendar Event.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Meeting {
    private String id;
    private String title;

    // Google Apps Script sends these as ISO 8601 Strings
    // Example: "2026-02-18T15:00:00.000Z"
    private String startTime;
    private String endTime;

    private String location;
    private String description;
    private String status;

    // Useful if you decide to use the MD5 hash logic later
    private Long lastUpdated;
}
