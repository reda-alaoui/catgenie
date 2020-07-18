package com.reda_alaoui.catgenie;

/** @author Réda Housni Alaoui */
public class CatGenieException extends RuntimeException {

  CatGenieException(String message) {
    super(message);
  }

  CatGenieException(Throwable cause) {
    super(cause);
  }
}
