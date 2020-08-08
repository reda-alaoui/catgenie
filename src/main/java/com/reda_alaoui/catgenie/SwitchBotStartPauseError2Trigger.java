package com.reda_alaoui.catgenie;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** @author Réda Housni Alaoui */
public class SwitchBotStartPauseError2Trigger implements Error2Listener {

  private static final Logger LOG = LoggerFactory.getLogger(SwitchBotStartPauseError2Trigger.class);

  private final String bluetoothMacAddress;

  public SwitchBotStartPauseError2Trigger(String bluetoothMacAddress) {
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

    LOG.debug(output);
  }

  public static void main(String[] args) throws IOException, InterruptedException {
    new SwitchBotStartPauseError2Trigger("E7:88:88:46:D3:F3").pressButton();
  }
}
