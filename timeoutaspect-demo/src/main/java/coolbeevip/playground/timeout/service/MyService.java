package coolbeevip.playground.timeout.service;

import coolbeevip.playground.timeout.annotations.Timeout;
import coolbeevip.playground.timeout.jpa.User;
import coolbeevip.playground.timeout.jpa.UserRepository;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class MyService {

  private final Lock lock = new ReentrantLock();

  @Autowired
  private UserRepository userRepository;

  @SneakyThrows
  @Timeout(value = 4000)
  @Transactional
  public void blockedOfWait(List<User> users, long simulate_time) {
    try {
      log.info("Transaction begin");
      log.info("execution...");
      for (User user : users) {
        userRepository.save(user);
      }
      if (simulate_time > 0) {
        Thread.currentThread().wait(simulate_time);
      }
      log.info("Transaction commit");
    } catch (Throwable e) {
      log.info("Transaction rollback");
      throw e;
    }
  }

  @SneakyThrows
  @Timeout(value = 4000)
  @Transactional
  public void blockedOfSleep(List<User> users, long simulate_time) {
    try {
      log.info("Transaction begin");
      log.info("execution...");
      for (User user : users) {
        userRepository.save(user);
      }
      if (simulate_time > 0) {
        Thread.sleep(simulate_time);
      }
      log.info("Transaction commit");
    } catch (Throwable e) {
      log.info("Transaction rollback");
      throw e;
    }
  }

  @SneakyThrows
  @Async
  @Timeout(value = 4000)
  @Transactional
  public void blockedOfAsync(List<User> users, long simulate_time) {
    try {
      log.info("Transaction begin");
      log.info("execution...");
      for (User user : users) {
        userRepository.save(user);
      }
      if (simulate_time > 0) {
        Thread.sleep(simulate_time);
      }
      log.info("Transaction commit");
    } catch (Throwable e) {
      log.info("Transaction rollback");
      throw e;
    }
  }

  @SneakyThrows
  @Timeout(value = 4000)
  @Transactional
  public void blockedOfBusyCPU(List<User> users) {
    try {
      log.info("Transaction begin");
      log.info("execution...");
      for (User user : users) {
        userRepository.save(user);
      }
      while(true){
        log.info("busy");
        Thread.sleep(1); //Ensure CPU IDLE through static code checking. In short, we need it
      }
    } catch (Throwable e) {
      log.info("Transaction rollback");
      throw e;
    }
  }

  @SneakyThrows
  @Timeout(value = 4000)
  @Transactional
  public void blockedOfThreadJoin(List<User> users, long simulate_time) {
    try {
      log.info("Transaction begin");
      log.info("execution...");
      for (User user : users) {
        userRepository.save(user);
      }

      Thread t1 = new Thread(new Runnable() {
        @Override
        public void run() {
          try{
            while (true){

            }
          }catch (Exception e){
            e.printStackTrace();
          }
        }
      });
      t1.start();
      t1.join();
      log.info("Transaction commit");
    } catch (Throwable e) {
      log.info("Transaction rollback");
      throw e;
    }
  }

  @SneakyThrows
  @Timeout(value = 4000)
  @Transactional
  public void blockedOfLock(List<User> users, long simulate_time) {
    lock.lockInterruptibly();
    try {
      log.info("Transaction begin");
      log.info("execution...");
      for (User user : users) {
        userRepository.save(user);
      }
      Thread.sleep(simulate_time);
      log.info("Transaction commit");
    } catch (Throwable e) {
      log.info("Transaction rollback");
      throw e;
    } finally {
      lock.unlock();
    }
  }

  @SneakyThrows
  @Timeout(value = 4000)
  @Transactional
  public void accessRejectionBySecurityManager(List<User> users, long simulate_time) {
    System.setSecurityManager(new MySM());
    try {
      log.info("Transaction begin");
      log.info("execution...");
      for (User user : users) {
        userRepository.save(user);
      }
      Thread.sleep(simulate_time);
      log.info("Transaction commit");
    } catch (Throwable e) {
      log.info("Transaction rollback");
      throw e;
    }
  }

  @SneakyThrows
  @Timeout(value = 4000)
  @Transactional
  public void blockedOfIO(List<User> users) {

    try {
      log.info("Transaction begin");
      log.info("execution...");
      for (User user : users) {
        userRepository.save(user);
      }
      String name = "delete.me";
      new File(name).deleteOnExit();
      RandomAccessFile raf = new RandomAccessFile(name, "rw");
      FileChannel fc = raf.getChannel();
      try{
        ByteBuffer buffer = ByteBuffer.wrap(new String("1").getBytes());
        while (true){
          fc.write(buffer);
        }
      }finally {
        if(fc!=null){
          fc.close();
        }
      }
    } catch (Throwable e) {
      log.info("Transaction rollback");
      throw e;
    }
  }

  @SneakyThrows
  @Async
  public void blockedOfLockLock() {
    lock.lockInterruptibly();
    try {
      log.info("Locked");
      while (true) {
        Thread.sleep(10);
      }
    } finally {
      lock.unlock();
    }
  }

  public Optional<User> find(Integer id) {
    return userRepository.findById(id);
  }

  public long count() {
    return userRepository.count();
  }

  public void clear() {
    userRepository.deleteAll();
    System.setSecurityManager(null);
  }

  static class MySM extends SecurityManager {
    public void checkAccess(Thread t) {
      throw new SecurityException("simulation");
    }
  }
}
