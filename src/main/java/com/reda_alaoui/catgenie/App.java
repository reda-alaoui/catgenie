package com.reda_alaoui.catgenie;

/** @author Réda Housni Alaoui */
public class App {

  public static void main(String[] args) {
    Configuration configuration = Configuration.readFromUserHome();

    IftttWebhookClient iftttWebhookClient = new IftttWebhookClient(configuration.iftttWebhookKey());
    Error2Webhook error2Webhook = new Error2Webhook(iftttWebhookClient);

    CatGenieAudioStream.openSystemAudioInput().read(error2Webhook);
  }
}
