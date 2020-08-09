package com.reda_alaoui.catgenie;

import static java.util.Objects.requireNonNull;

import java.time.Duration;
import java.time.LocalDateTime;

/** @author Réda Housni Alaoui */
public class Error2DelayingListener implements Error2Listener {

  private final Error2Listener delegate;
  private final Duration minDurationBetweenTwoNotifications;

  private LocalDateTime lastNotification;

  public Error2DelayingListener(
      Error2Listener delegate, Duration minDurationBetweenTwoNotifications) {
    this.delegate = requireNonNull(delegate);
    this.minDurationBetweenTwoNotifications = requireNonNull(minDurationBetweenTwoNotifications);
  }

  @Override
  public void onErrorNotification() {
    if (lastNotification != null
        && lastNotification.plus(minDurationBetweenTwoNotifications).isAfter(LocalDateTime.now())) {
      return;
    }
    lastNotification = LocalDateTime.now();
    delegate.onErrorNotification();
  }
}
