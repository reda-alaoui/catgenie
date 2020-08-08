package com.reda_alaoui.catgenie;

import java.util.ArrayList;
import java.util.List;

/** @author Réda Housni Alaoui */
public class CompositeError2Listener implements Error2Listener {

  private final List<Error2Listener> listeners;

  public CompositeError2Listener(List<Error2Listener> listeners) {
    this.listeners = new ArrayList<>(listeners);
  }

  @Override
  public void onErrorNotification() {
    listeners.forEach(Error2Listener::onErrorNotification);
  }
}
