package com.reda_alaoui.catgenie;

/** @author Réda Housni Alaoui */
public class App {

  public static void main(String[] args) {
    Configuration configuration = Configuration.readFromUserHome();
    CatGenieAudioStream catGenieAudioStream =
        CatGenieAudioStream.openAudioInput(configuration.audioInputNameRegex());

    Error2Webhook.run(configuration, catGenieAudioStream);
  }
}
