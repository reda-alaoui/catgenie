package com.reda_alaoui.catgenie;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** @author Réda Housni Alaoui */
public class Error2IftttWebhook implements Error2Listener {

  private static final Logger LOG = LoggerFactory.getLogger(Error2IftttWebhook.class);

  private static final Duration MIN_DURATION_BETWEEN_TWO_NOTIFICATIONS =
      Duration.of(2, ChronoUnit.MINUTES);

  private final IftttWebhookClient client;

  private LocalDateTime lastNotification;

  public Error2IftttWebhook(String iftttWebhookKey) {
    this.client = new IftttWebhookClient(iftttWebhookKey);
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
    LOG.info("Firing error 2 notification");
    client.fireEvent("catgenie_error_2");
    LOG.info("Error 2 notification fired with success");
  }
}
