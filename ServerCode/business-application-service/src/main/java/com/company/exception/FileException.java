package com.company.exception;

import java.io.IOException;

public class FileException extends RuntimeException {
  private IOException cause;

  public FileException(String message) {
    super(message);
  }

  public FileException(String message, IOException cause) {
    super(message);
    this.cause = cause;
  }

  @Override
  public IOException getCause() {
    return this.cause;
  }
}
