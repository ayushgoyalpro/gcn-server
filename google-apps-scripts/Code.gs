function syncDailyMeetings() {
  const SERVER_URL = "https://google-calendar-notifications.onrender.com/api/sync";

  const beginingOfDay = new Date();
  beginingOfDay.setHours(00, 00, 00, 000)
  const endOfDay = new Date();
  endOfDay.setHours(23, 59, 59, 999);

  // 1. Get all events from today
  const events = CalendarApp.getDefaultCalendar().getEvents(beginingOfDay, endOfDay);

  // 2. Map them to a clean data structure
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

  // 3. POST the data to your endpoint
  const options = {
    method: 'post',
    contentType: 'application/json',
    payload: JSON.stringify(payload),
    muteHttpExceptions: true,
    headers: {
      "ngrok-skip-browser-warning": "true" // for local testing with ngrok
    }
  };

  try {
    const response = UrlFetchApp.fetch(SERVER_URL, options);
    console.log("Status Code: " + response.getResponseCode());
    console.log("Server Response: " + response.getContentText());
  } catch (e) {
    console.error("Failed to hit local API: " + e.toString());
  }
}