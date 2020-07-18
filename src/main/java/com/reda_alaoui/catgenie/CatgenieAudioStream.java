package com.reda_alaoui.catgenie;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.io.jvm.JVMAudioInputStream;
import be.tarsos.dsp.pitch.PitchProcessor;
import javax.sound.sampled.AudioInputStream;

/** @author Réda Housni Alaoui */
public class CatgenieAudioStream {

  private final AudioInputStream stream;
  private final float sampleRate;

  public CatgenieAudioStream(AudioInputStream stream) {
    this.stream = stream;
    sampleRate = stream.getFormat().getSampleRate();
  }

  public void read(Error2Listener error2Listener) {
    AudioDispatcher audioDispatcher = new AudioDispatcher(new JVMAudioInputStream(stream), 1024, 0);
    audioDispatcher.addAudioProcessor(
        new PitchProcessor(
            PitchProcessor.PitchEstimationAlgorithm.YIN,
            sampleRate,
            1024,
            new Error2NotificationDetector(error2Listener)));
    audioDispatcher.run();
  }

}
