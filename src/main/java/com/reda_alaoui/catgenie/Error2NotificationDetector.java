package com.reda_alaoui.catgenie;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import java.util.Deque;
import java.util.LinkedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** @author Réda Housni Alaoui */
class Error2NotificationDetector implements PitchDetectionHandler {

  private static final Logger LOG = LoggerFactory.getLogger(Error2NotificationDetector.class);

  private static final float BEEP_MIN_FREQUENCY_IN_HERTZ = 2600;

  private static final float APPROXIMATION = 0.05f;

  private static final float DURATION_BETWEEN_2ND_BEEP_START_AND_1ST_BEEP_START_IN_SECONDS = 4.4f;
  private static final float DURATION_BETWEEN_1ST_BEEP_START_AND_2ND_BEEP_START_IN_SECONDS = 0.6f;
  private static final float BEEP_DURATION = 0.4f;

  private final Error2Listener listener;

  private final Deque<Double> beepStartHistory = new LinkedList<>();

  Error2NotificationDetector(Error2Listener listener) {
    this.listener = listener;
  }

  @Override
  public void handlePitch(PitchDetectionResult pitchDetectionResult, AudioEvent audioEvent) {
    if (pitchDetectionResult.getPitch() < BEEP_MIN_FREQUENCY_IN_HERTZ) {
      return;
    }

    if (beepStartHistory.isEmpty()) {
      recordBeep(audioEvent);
      return;
    }

    if (audioEvent.getTimeStamp() - beepStartHistory.getLast()
        <= BEEP_DURATION * (1 + APPROXIMATION)) {
      // Same beep as the previous
      return;
    }

    recordBeep(audioEvent);

    if (beepStartHistory.size() != 3) {
      return;
    }

    double firstBeepStart = beepStartHistory.removeFirst();
    double secondBeepStart = beepStartHistory.removeFirst();
    double thirdBeepStart = beepStartHistory.getFirst();

    if (isBeepBeepPauseBeep(firstBeepStart, secondBeepStart, thirdBeepStart)) {
      LOG.debug(
          "Matched pattern 'Beep Beep Pause Beep' on '{} {} {}'",
          firstBeepStart,
          secondBeepStart,
          thirdBeepStart);
      fireErrorNotification();
    } else if (isBeepPauseBeepBeep(firstBeepStart, secondBeepStart, thirdBeepStart)) {
      LOG.debug(
          "Matched pattern 'Beep Pause Beep Beep' on '{} {} {}'",
          firstBeepStart,
          secondBeepStart,
          thirdBeepStart);
      fireErrorNotification();
    }
  }

  private void recordBeep(AudioEvent audioEvent) {
    LOG.debug("Beep at {}", audioEvent.getTimeStamp());
    beepStartHistory.add(audioEvent.getTimeStamp());
  }

  private boolean isBeepBeepPauseBeep(
      double firstBeepStart, double secondBeepStart, double thirdBeepStart) {
    return isBeepBeep(firstBeepStart, secondBeepStart)
        && isBeepPauseBeep(secondBeepStart, thirdBeepStart);
  }

  private boolean isBeepPauseBeepBeep(
      double firstBeepStart, double secondBeepStart, double thirdBeepStart) {
    return isBeepPauseBeep(firstBeepStart, secondBeepStart)
        && isBeepBeep(secondBeepStart, thirdBeepStart);
  }

  private boolean isBeepBeep(double firstBeepStart, double secondBeepStart) {
    double firstToSecondDuration = secondBeepStart - firstBeepStart;
    return DURATION_BETWEEN_1ST_BEEP_START_AND_2ND_BEEP_START_IN_SECONDS * (1 - APPROXIMATION)
            <= firstToSecondDuration
        && firstToSecondDuration
            <= DURATION_BETWEEN_1ST_BEEP_START_AND_2ND_BEEP_START_IN_SECONDS * (1 + APPROXIMATION);
  }

  private boolean isBeepPauseBeep(double firstBeepStart, double secondBeepStart) {
    double firstToSecondDuration = secondBeepStart - firstBeepStart;
    return DURATION_BETWEEN_2ND_BEEP_START_AND_1ST_BEEP_START_IN_SECONDS * (1 - APPROXIMATION)
            <= firstToSecondDuration
        && firstToSecondDuration
            <= DURATION_BETWEEN_2ND_BEEP_START_AND_1ST_BEEP_START_IN_SECONDS * (1 + APPROXIMATION);
  }

  private void fireErrorNotification() {
    try {
      listener.onErrorNotification();
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
  }
}
