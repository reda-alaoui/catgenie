package com.reda_alaoui.catgenie;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** @author Réda Housni Alaoui */
class Error2NotificationDetector implements PitchDetectionHandler {

  private static final Logger LOG = LoggerFactory.getLogger(Error2NotificationDetector.class);

  private static final float BEEP_MIN_FREQUENCY_IN_HERTZ = 2600;

  private static final float MIN_DURATION_BETWEEN_BEEP_END_AND_SUBSEQUENT_BEEP_END_IN_SECONDS =
      0.3f;
  private static final float MAX_DURATION_BETWEEN_BEEP_END_AND_SUBSEQUENT_BEEP_END_IN_SECONDS =
      0.6f;

  private static final float MIN_DURATION_BURST_END_AND_SUBSEQUENT_BURST_START_IN_SECONDS = 4;
  private static final float MAX_DURATION_BURST_END_AND_SUBSEQUENT_BURST_START_IN_SECONDS = 4.4f;

  private static final float
      MAX_DURATION_BETWEEN_SECOND_BEEP_START_AND_FIRST_BEEP_START_IN_SECONDS = 4.45f;

  private final Error2Listener listener;

  private Double lastCompatiblePitchTimestamp;
  private Double lastSecondBeepStartTimestamp;

  Error2NotificationDetector(Error2Listener listener) {
    this.listener = listener;
  }

  @Override
  public void handlePitch(PitchDetectionResult pitchDetectionResult, AudioEvent audioEvent) {
    if (pitchDetectionResult.getPitch() < BEEP_MIN_FREQUENCY_IN_HERTZ) {
      return;
    }

    double elapsedTimeSinceLastPitch;
    if (lastCompatiblePitchTimestamp == null) {
      elapsedTimeSinceLastPitch = MIN_DURATION_BURST_END_AND_SUBSEQUENT_BURST_START_IN_SECONDS;
    } else {
      elapsedTimeSinceLastPitch = audioEvent.getTimeStamp() - lastCompatiblePitchTimestamp;
    }

    if (MIN_DURATION_BURST_END_AND_SUBSEQUENT_BURST_START_IN_SECONDS <= elapsedTimeSinceLastPitch
        && elapsedTimeSinceLastPitch
            <= MAX_DURATION_BURST_END_AND_SUBSEQUENT_BURST_START_IN_SECONDS) {
      if (lastSecondBeepStartTimestamp != null
          && audioEvent.getTimeStamp() - lastCompatiblePitchTimestamp
              <= MAX_DURATION_BETWEEN_SECOND_BEEP_START_AND_FIRST_BEEP_START_IN_SECONDS) {
        LOG.debug("Previous sequence was a valid burst\n");
        listener.onErrorNotification();
      }
      lastSecondBeepStartTimestamp = null;
      LOG.debug("First beep start -> {}", audioEvent.getTimeStamp());
    } else if (MIN_DURATION_BETWEEN_BEEP_END_AND_SUBSEQUENT_BEEP_END_IN_SECONDS
            <= elapsedTimeSinceLastPitch
        && elapsedTimeSinceLastPitch
            <= MAX_DURATION_BETWEEN_BEEP_END_AND_SUBSEQUENT_BEEP_END_IN_SECONDS) {
      lastSecondBeepStartTimestamp = audioEvent.getTimeStamp();
      LOG.debug("Second beep start -> {}\n", audioEvent.getTimeStamp());
    }

    lastCompatiblePitchTimestamp = audioEvent.getTimeStamp();
  }
}
