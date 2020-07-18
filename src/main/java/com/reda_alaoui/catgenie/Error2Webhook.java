package com.reda_alaoui.catgenie;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** @author Réda Housni Alaoui */
public class Error2Webhook implements Error2Listener {

  private static final Logger LOG = LoggerFactory.getLogger(Error2Webhook.class);

  private static final Duration MIN_DURATION_BETWEEN_TWO_NOTIFICATIONS =
      Duration.of(2, ChronoUnit.MINUTES);

  private final IftttWebhookClient client;

  private LocalDateTime lastNotification;

  private Error2Webhook(IftttWebhookClient client) {
    this.client = client;
  }

  public static void run(Configuration configuration, CatGenieAudioStream catGenieAudioStream) {
    LOG.info("Starting webhook");
    IftttWebhookClient iftttWebhookClient = new IftttWebhookClient(configuration.iftttWebhookKey());
    Error2Webhook error2Webhook = new Error2Webhook(iftttWebhookClient);
    catGenieAudioStream.read(error2Webhook);
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
    LOG.info("Firing notification");
    client.fireEvent("catgenie_error_2");
    LOG.info("Fired notification");
  }
}
