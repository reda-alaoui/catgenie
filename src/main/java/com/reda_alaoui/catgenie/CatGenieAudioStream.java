package com.reda_alaoui.catgenie;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.io.jvm.JVMAudioInputStream;
import be.tarsos.dsp.pitch.PitchProcessor;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** @author Réda Housni Alaoui */
public class CatGenieAudioStream {

  private static final Logger LOG = LoggerFactory.getLogger(CatGenieAudioStream.class);

  private final AudioInputStream stream;
  private final float sampleRate;

  public CatGenieAudioStream(AudioInputStream stream) {
    this.stream = stream;
    sampleRate = stream.getFormat().getSampleRate();
  }

  public static CatGenieAudioStream openSystemAudioInput() {
    AudioFormat audioFormat = new AudioFormat(44100, 16, 1, true, true);

    DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
    if (!AudioSystem.isLineSupported(dataLineInfo)) {
      throw new CatGenieException("Line not supported");
    }

    TargetDataLine line;
    try {
      line = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
      line.open(audioFormat, 1024);
    } catch (LineUnavailableException e) {
      throw new CatGenieException(e);
    }
    line.start();

    return new CatGenieAudioStream(new AudioInputStream(line));
  }

  public void read(Error2Listener error2Listener) {
    AudioDispatcher audioDispatcher = new AudioDispatcher(new JVMAudioInputStream(stream), 1024, 0);
    audioDispatcher.addAudioProcessor(
        new PitchProcessor(
            PitchProcessor.PitchEstimationAlgorithm.YIN,
            sampleRate,
            1024,
            new Error2NotificationDetector(error2Listener)));

    LOG.info("Reading stream");
    audioDispatcher.run();
  }
}
