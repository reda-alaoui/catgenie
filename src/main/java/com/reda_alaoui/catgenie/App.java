package com.reda_alaoui.catgenie;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** @author Réda Housni Alaoui */
public class App {

  private static final Logger LOG = LoggerFactory.getLogger(App.class);

  public static void main(String[] args) throws LineUnavailableException {
    float sampleRate = 44100;
    AudioFormat audioFormat = new AudioFormat(sampleRate, 16, 1, true, true);

    DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
    if (!AudioSystem.isLineSupported(dataLineInfo)) {
      LOG.error("Line not supported");
      System.exit(1);
    }

    int bufferSize = 512;

    TargetDataLine line = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
    line.open(audioFormat, bufferSize);
    line.start();

    new CatgenieAudioStream(new AudioInputStream(line)).read(() -> LOG.info("Error 2 notified"));
  }
}
