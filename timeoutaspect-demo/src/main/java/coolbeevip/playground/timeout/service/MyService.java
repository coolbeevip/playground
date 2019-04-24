package coolbeevip.playground.timeout.service;

import coolbeevip.playground.timeout.annotations.Timeout;
import coolbeevip.playground.timeout.exception.TimeoutAspectException;
import coolbeevip.playground.timeout.jpa.User;
import coolbeevip.playground.timeout.jpa.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
//import javax.transaction.Transactional;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class MyService {

  @Autowired
  private UserRepository userRepository;

  @SneakyThrows
  @Timeout(value = 3000)
  @Transactional
  public void save(List<User> users, long simulate_time) {
    try{
      log.info("Transaction begin");
      log.info("execution...");
      for(User user : users){
        userRepository.save(user);
      }
      Thread.sleep(simulate_time);
      log.info("Transaction commit");
    }catch (Throwable e){
      log.info("Transaction rollback");
      throw e;
    }
  }

  public Optional<User> find(Integer id){
    return userRepository.findById(id);
  }

  public long count(){
    return userRepository.count();
  }

  public void clear(){
    userRepository.deleteAll();
  }
}
