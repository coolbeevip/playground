package coolbeevip.playground.timeout.exception;

public class TimeoutAbortedFailureException extends RuntimeException {

  public TimeoutAbortedFailureException() {
  }

  public TimeoutAbortedFailureException(String message) {
    super(message);
  }

  public TimeoutAbortedFailureException(String message, Throwable cause) {
    super(message, cause);
  }

  public TimeoutAbortedFailureException(Throwable cause) {
    super(cause);
  }

  public TimeoutAbortedFailureException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
