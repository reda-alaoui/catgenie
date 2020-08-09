package com.reda_alaoui.catgenie;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** @author Réda Housni Alaoui */
public class Error2IftttWebhook implements Error2Listener {

  private static final Logger LOG = LoggerFactory.getLogger(Error2IftttWebhook.class);

  private static final String EVENT_NAME = "catgenie_error_2";

  private final IftttWebhookClient client;

  public Error2IftttWebhook(String iftttWebhookKey) {
    this.client = new IftttWebhookClient(iftttWebhookKey);
  }

  @Override
  public void onErrorNotification() {
    LOG.info("Firing IFTTT event '{}'", EVENT_NAME);
    client.fireEvent(EVENT_NAME);
    LOG.info("IFTTT event '{}' fired with success", EVENT_NAME);
  }
}
