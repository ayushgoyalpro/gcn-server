function syncDailyMeetingsV2() {
  const SERVER_URL = "https://google-calendar-notifications.onrender.com/api/sync";
  const scriptCache = CacheService.getScriptCache();

  // 1. Get full day range
  const startOfDay = new Date();
  startOfDay.setHours(0, 0, 0, 0);
  const endOfDay = new Date();
  endOfDay.setHours(23, 59, 59, 999);

  const events = CalendarApp.getDefaultCalendar().getEvents(startOfDay, endOfDay);

  // 2. Map data
  const payload = events.map(event => {
    return {
      id: event.getId(),
      title: event.getTitle(),
      startTime: event.getStartTime().toISOString(),
      endTime: event.getEndTime().toISOString(),
      location: event.getLocation(),
      description: event.getDescription(),
      status: event.getMyStatus().toString()
    };
  });

  // 3. Hacky Change Detection: Hash the payload
  const currentDataString = JSON.stringify(payload);
  const currentHash = Utilities.base64Encode(Utilities.computeDigest(Utilities.DigestAlgorithm.MD5, currentDataString));
  const oldHash = scriptCache.get("last_calendar_hash");

  if (currentHash === oldHash) {
    console.log("No changes detected. Skipping API call.");
    return;
  }

  // 4. Send if changed
  const options = {
    method: 'post',
    contentType: 'application/json',
    payload: currentDataString,
    headers: { "ngrok-skip-browser-warning": "true" }
  };

  try {
    const response = UrlFetchApp.fetch(SERVER_URL, options);
    console.log("Status Code: " + response.getResponseCode());
    console.log("Server Response: " + response.getContentText());
    scriptCache.put("last_calendar_hash", currentHash, 21600); // Cache for 6 hours
    console.log("Cache saved");
  } catch (e) {
    console.error("Sync failed: " + e.toString());
  }
}