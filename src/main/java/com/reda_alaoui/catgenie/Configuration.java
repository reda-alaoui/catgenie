package com.reda_alaoui.catgenie;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** @author Réda Housni Alaoui */
public class Configuration {

  private static final Logger LOG = LoggerFactory.getLogger(Configuration.class);

  private final Properties properties;

  public Configuration(Properties properties) {
    this.properties = properties;
  }

  public static Configuration readFromUserHome() {
    String userHome = System.getProperty("user.home");
    Path propertiesPath = Paths.get(userHome).resolve("configuration.properties");
    LOG.info("Reading configuration from '{}'", propertiesPath);

    Properties properties = new Properties();
    try (InputStream propertiesInputStream = Files.newInputStream(propertiesPath)) {
      properties.load(propertiesInputStream);
    } catch (IOException e) {
      throw new CatGenieException(e);
    }
    return new Configuration(properties);
  }

  public String iftttWebhookKey() {
    return properties.getProperty("ifttt.webhook.key");
  }
}
