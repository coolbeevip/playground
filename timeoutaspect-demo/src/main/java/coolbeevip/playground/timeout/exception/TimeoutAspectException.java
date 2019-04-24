package coolbeevip.playground.timeout.exception;

public class TimeoutAspectException extends RuntimeException {

  public TimeoutAspectException() {
  }

  public TimeoutAspectException(String message) {
    super(message);
  }

  public TimeoutAspectException(String message, Throwable cause) {
    super(message, cause);
  }

  public TimeoutAspectException(Throwable cause) {
    super(cause);
  }

  public TimeoutAspectException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
