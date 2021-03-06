package com.reda_alaoui.catgenie;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** @author Réda Housni Alaoui */
public class App {

  private static final Logger LOG = LoggerFactory.getLogger(App.class);

  public static void main(String[] args) {
    Configuration configuration = Configuration.readFromUserHome();
    CatGenieAudioStream catGenieAudioStream =
        CatGenieAudioStream.openAudioInput(configuration.audioInputNameRegex());

    List<Error2Listener> error2Listeners = new ArrayList<>();

    String iftttWebhookKey = configuration.iftttWebhookKey();
    if (StringUtils.isNotBlank(iftttWebhookKey)) {
      LOG.info("Registering error 2 IFTTT webhook");
      error2Listeners.add(new Error2IftttWebhook(iftttWebhookKey));
    } else {
      LOG.info("No IFTTT webhook key found. Skipping error 2 IFTTT webhook registration.");
    }

    String switchBotStartPauseBluetoothMacAddress =
        configuration.switchBotStartPauseBluetoothMacAddress();
    if (StringUtils.isNotBlank(switchBotStartPauseBluetoothMacAddress)) {
      LOG.info(
          "Registering error 2 SwitchBot Start/Pause trigger for bluetooth mac address {}",
          switchBotStartPauseBluetoothMacAddress);
      error2Listeners.add(
          new Error2SwitchBotStartPauseTrigger(switchBotStartPauseBluetoothMacAddress));
    } else {
      LOG.info(
          "No SwitchBot Start/Pause bluetooth mac address found. Skipping error 2 SwitchBot Start/Pause trigger registration.");
    }

    catGenieAudioStream.read(
        new Error2DelayingListener(
            new CompositeError2Listener(error2Listeners), Duration.of(2, ChronoUnit.MINUTES)));
  }
}
