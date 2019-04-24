package coolbeevip.playground.timeout.annotations;

import coolbeevip.playground.timeout.exception.TimeoutAspectException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;
import jdk.Exported;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Timeout {
  TimeUnit unit() default TimeUnit.MILLISECONDS;
  long value();
}
