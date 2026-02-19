package com.ayush.googlecalendarnotifications;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SyncController {

    private final AlertStorage storage;

    public SyncController(AlertStorage storage) {
        this.storage = storage;
    }

    @PostMapping("/sync")
    public ResponseEntity<String> sync(@RequestBody List<MeetingDto> meetings) {
        storage.rebuildSchedule(meetings);
        return ResponseEntity.ok("Synced " + meetings.size() + " meetings.");
    }
}
