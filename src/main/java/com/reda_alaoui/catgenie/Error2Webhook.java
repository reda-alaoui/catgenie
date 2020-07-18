package com.reda_alaoui.catgenie;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/** @author Réda Housni Alaoui */
public class Error2Webhook implements Error2Listener {

  private static final Duration MIN_DURATION_BETWEEN_TWO_NOTIFICATIONS =
      Duration.of(2, ChronoUnit.MINUTES);

  private final IftttWebhookClient client;

  private LocalDateTime lastNotification;

  public Error2Webhook(IftttWebhookClient client) {
    this.client = client;
  }

  @Override
  public void onErrorNotification() {
    if (lastNotification != null
        && lastNotification
            .plus(MIN_DURATION_BETWEEN_TWO_NOTIFICATIONS)
            .isAfter(LocalDateTime.now())) {
      return;
    }
    lastNotification = LocalDateTime.now();
    client.fireEvent("catgenie_error_2");
  }
}
