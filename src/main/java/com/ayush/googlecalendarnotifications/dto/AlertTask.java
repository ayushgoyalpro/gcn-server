package com.ayush.googlecalendarnotifications.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AlertTask {
    private String title;
    private AlertType type;
}
