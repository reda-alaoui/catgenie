package com.reda_alaoui.catgenie;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** @author Réda Housni Alaoui */
class Error2NotificationDetectorTest {

  private PitchDetectionResult compatiblePitch;

  @BeforeEach
  public void beforeEach() {
    compatiblePitch = new PitchDetectionResult();
    compatiblePitch.setPitch(2600);
    compatiblePitch.setProbability(1);
    compatiblePitch.setPitched(true);
  }

  @Test
  @DisplayName("Zero notification for far distanced beep")
  public void test1() {
    AtomicInteger numberOfNotification = new AtomicInteger();
    Error2NotificationDetector detector =
        new Error2NotificationDetector(numberOfNotification::incrementAndGet);

    detector.handlePitch(compatiblePitch, createBeep(3172.681640625));
    detector.handlePitch(compatiblePitch, createBeep(39861.578125));

    assertThat(numberOfNotification.get()).isEqualTo(0);
  }

  @Test
  public void test2() {
    AtomicInteger numberOfNotification = new AtomicInteger();
    Error2NotificationDetector detector =
        new Error2NotificationDetector(numberOfNotification::incrementAndGet);

    detector.handlePitch(compatiblePitch, createBeep(3172.681640625));
    detector.handlePitch(compatiblePitch, createBeep(39861.578125));
    detector.handlePitch(compatiblePitch, createBeep(39865.96875));
    detector.handlePitch(compatiblePitch, createBeep(39870.42578125));
    detector.handlePitch(compatiblePitch, createBeep(39871.0078125));
    detector.handlePitch(compatiblePitch, createBeep(39875.39453125));
    detector.handlePitch(compatiblePitch, createBeep(39876.0));

    assertThat(numberOfNotification.get()).isEqualTo(2);
  }

  private AudioEvent createBeep(double timestamp) {
    AudioEvent beep = mock(AudioEvent.class);
    when(beep.getTimeStamp()).thenReturn(timestamp);
    return beep;
  }
}
