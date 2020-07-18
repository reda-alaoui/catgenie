package com.reda_alaoui.catgenie;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicInteger;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import org.junit.jupiter.api.Test;

/** @author Réda Housni Alaoui */
class CatGenieAudioStreamTest {

  @Test
  public void test() throws IOException, UnsupportedAudioFileException {
    AtomicInteger numberOfErrorNotifications = new AtomicInteger();
    try (InputStream resourceStream = getClass().getResourceAsStream("/catgenie_error_2.wav")) {
      AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(resourceStream);
      new CatGenieAudioStream(audioInputStream).read(numberOfErrorNotifications::incrementAndGet);
    }

    assertThat(numberOfErrorNotifications.get()).isEqualTo(2);
  }
}
