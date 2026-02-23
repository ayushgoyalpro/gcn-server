package com.ayush.googlecalendarnotifications.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Alert {
    private String title;
    private AlertType type;
}
