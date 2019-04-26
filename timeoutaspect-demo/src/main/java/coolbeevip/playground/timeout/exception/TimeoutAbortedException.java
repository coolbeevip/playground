package coolbeevip.playground.timeout.exception;

public class TimeoutAbortedException extends RuntimeException {

  public TimeoutAbortedException() {
  }

  public TimeoutAbortedException(String message) {
    super(message);
  }

  public TimeoutAbortedException(String message, Throwable cause) {
    super(message, cause);
  }

  public TimeoutAbortedException(Throwable cause) {
    super(cause);
  }

  public TimeoutAbortedException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
