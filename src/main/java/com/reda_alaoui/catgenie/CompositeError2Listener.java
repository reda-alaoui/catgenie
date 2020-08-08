package com.reda_alaoui.catgenie;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** @author Réda Housni Alaoui */
public class CompositeError2Listener implements Error2Listener {

  private static final Logger LOG = LoggerFactory.getLogger(CompositeError2Listener.class);

  private final List<Error2Listener> listeners;

  public CompositeError2Listener(List<Error2Listener> listeners) {
    this.listeners = new ArrayList<>(listeners);
  }

  @Override
  public void onErrorNotification() {
    listeners.forEach(
        listener -> {
          try {
            listener.onErrorNotification();
          } catch (Exception e) {
            LOG.error(e.getMessage(), e);
          }
        });
  }
}
