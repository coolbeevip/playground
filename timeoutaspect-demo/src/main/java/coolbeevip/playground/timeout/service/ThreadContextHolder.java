package coolbeevip.playground.timeout.service;

import org.springframework.stereotype.Component;

@Component
public class ThreadContextHolder {
  private ThreadLocal<String> threadLocal = new ThreadLocal<>();

  public String get(){
    return threadLocal.get();
  }

  public void set(String val){
    this.threadLocal.set(val);
  }
}
