package com.ayush.googlecalendarnotifications;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AlertTask {
    private String title;
    private AlertType type;
}
