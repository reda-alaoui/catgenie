package com.reda_alaoui.catgenie;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;

/** @author Réda Housni Alaoui */
public class IftttWebhookClient {

  private final String apiKey;

  public IftttWebhookClient(String apiKey) {
    this.apiKey = apiKey;
  }

  public void fireEvent(String event) {
    try {
      doFireEvent(event);
    } catch (IOException e) {
      throw new CatGenieException(e);
    }
  }

  private void doFireEvent(String event) throws IOException {
    URL url = new URL("https://maker.ifttt.com/trigger/" + event + "/with/key/" + apiKey);
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("POST");

    int responseCode = connection.getResponseCode();
    if (200 <= responseCode && responseCode < 400) {
      return;
    }

    String responseBody = IOUtils.toString(connection.getErrorStream(), StandardCharsets.UTF_8);

    throw new CatGenieException(
        "HTTP request to IFTTT webhook service failed with code "
            + responseCode
            + " and response body '"
            + responseBody
            + "'");
  }
}
