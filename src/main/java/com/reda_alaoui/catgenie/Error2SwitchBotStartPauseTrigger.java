package com.reda_alaoui.catgenie;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** @author Réda Housni Alaoui */
public class Error2SwitchBotStartPauseTrigger implements Error2Listener {

  private static final Logger LOG = LoggerFactory.getLogger(Error2SwitchBotStartPauseTrigger.class);

  private final String bluetoothMacAddress;

  public Error2SwitchBotStartPauseTrigger(String bluetoothMacAddress) {
    this.bluetoothMacAddress = requireNonNull(bluetoothMacAddress);
  }

  @Override
  public void onErrorNotification() {
    try {
      pressButton();
    } catch (IOException e) {
      throw new CatGenieException(e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new CatGenieException(e);
    }
  }

  private void pressButton() throws IOException, InterruptedException {
    String[] command =
        new String[] {
          "gatttool",
          "-t",
          "random",
          "-b",
          bluetoothMacAddress,
          "--char-write-req",
          "-a",
          "0x0016",
          "-n",
          "570100"
        };

    String userFriendlyCommand = StringUtils.join(command, StringUtils.SPACE);
    LOG.info("Executing '{}'", userFriendlyCommand);

    Process process =
        new ProcessBuilder()
            .command(command)
            .redirectInput(ProcessBuilder.Redirect.INHERIT)
            .start();

    String output =
        IOUtils.toString(process.getInputStream(), StandardCharsets.UTF_8).trim()
            + IOUtils.toString(process.getErrorStream(), StandardCharsets.UTF_8).trim();

    int exitCode = process.waitFor();
    if (exitCode != 0) {
      throw new CatGenieException(
          userFriendlyCommand
              + " failed with exit code "
              + exitCode
              + " and output '"
              + output
              + "'");
    }

    LOG.info("Command successfully executed with output '{}'", output);
  }
}
