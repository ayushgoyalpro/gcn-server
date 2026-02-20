package com.ayush.googlecalendarnotifications.controller;

import com.ayush.googlecalendarnotifications.dto.Meeting;
import com.ayush.googlecalendarnotifications.service.AlertStorage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class GoogleAppScript {

    private final AlertStorage storage;

    public GoogleAppScript(AlertStorage storage) {
        this.storage = storage;
    }

    @PostMapping("/sync")
    public ResponseEntity<String> sync(@RequestBody List<Meeting> meetings) {
        storage.rebuildSchedule(meetings);
        return ResponseEntity.ok("Synced " + meetings.size() + " meetings.");
    }
}
