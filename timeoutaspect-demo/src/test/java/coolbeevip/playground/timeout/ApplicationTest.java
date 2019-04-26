package coolbeevip.playground.timeout;

import coolbeevip.playground.timeout.exception.TimeoutAbortedException;
import coolbeevip.playground.timeout.exception.TimeoutAbortedFailureException;
import coolbeevip.playground.timeout.jpa.User;
import coolbeevip.playground.timeout.service.MyService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class ApplicationTest {

  @Autowired
  MyService myService;

  @Before
  public void before() {
    myService.clear();
  }

  @Test
  @SneakyThrows
  public void noTimeoutTest() {
    List<User> users = new ArrayList<>();
    users.add(User.builder().id(1).name("zhanglei").build());
    myService.blockedOfWait(users, 0);
    Optional<User> user = myService.find(1);
    Assert.assertNotNull(user.get());
    Assert.assertEquals(user.get().getId(), Integer.valueOf(1));
  }

  /**
   * simulation execution 2s
   */
  @Test
  public void noTimeoutBatchTest() throws TimeoutAbortedException {
    try {
      List<User> users = new ArrayList<>();
      users.add(User.builder().id(1).name("zhanglei").build());
      users.add(User.builder().id(2).name("coolbeevip").build());
      myService.blockedOfWait(users, 0);
    } finally {
      Assert.assertEquals(myService.count(), 2l);
    }
  }

  @Test
  public void noTimeoutUniqueIndexExceptionTest() throws TimeoutAbortedException {
    try {
      List<User> users = new ArrayList<>();
      users.add(User.builder().id(1).name("zhanglei").build());
      users.add(User.builder().id(2).name("zhanglei").build());
      myService.blockedOfWait(users, 0);
    } catch (Exception e) {
      log.error(e.getMessage());
    } finally {
      Assert.assertEquals(myService.count(), 0l);
    }
  }

  @Test
  public void timeoutWithBlockedOfWaitTest() throws TimeoutAbortedException {
    try {
      List<User> users = new ArrayList<>();
      users.add(User.builder().id(1).name("zhanglei").build());
      myService.blockedOfWait(users, 5000);
    } catch (Exception e) {
      log.error(e.getMessage());
    } finally {
      Assert.assertEquals(myService.count(), 0l);
    }
  }

  @Test
  public void timeoutWithBlockedOfSleepTest() throws TimeoutAbortedException {
    try {
      List<User> users = new ArrayList<>();
      users.add(User.builder().id(1).name("zhanglei").build());
      myService.blockedOfSleep(users, 5000);
    } catch (Exception e) {
      log.error(e.getMessage());
    } finally {
      Assert.assertEquals(myService.count(), 0l);
    }
  }

  @Test
  public void timeoutWithBlockedOfAsyncTest() throws TimeoutAbortedException {
    try {
      List<User> users = new ArrayList<>();
      users.add(User.builder().id(1).name("zhanglei").build());
      myService.blockedOfAsync(users, 5000);
    } catch (Exception e) {
      log.error(e.getMessage());
    } finally {
      Assert.assertEquals(myService.count(), 0l);
    }
  }

  @Test
  public void timeoutWithBlockedOfThreadJoinTest() throws TimeoutAbortedException {
    try {
      List<User> users = new ArrayList<>();
      users.add(User.builder().id(1).name("zhanglei").build());
      myService.blockedOfThreadJoin(users, 5000);
    } catch (Exception e) {
      log.error(e.getMessage());
    } finally {
      Assert.assertEquals(myService.count(), 0l);
    }
  }

  @Test
  public void timeoutWithBlockedOfLockTest() throws TimeoutAbortedException {
    try {
      myService.blockedOfLockLock();
      List<User> users = new ArrayList<>();
      users.add(User.builder().id(1).name("zhanglei").build());
      myService.blockedOfLock(users, 5000);
    } catch (Exception e) {
      log.error(e.getMessage());
    } finally {
      Assert.assertEquals(myService.count(), 0l);
    }
  }

  @Test
  public void timeoutWithBlockedOfBusyCPUTest() throws TimeoutAbortedException {
    try {
      List<User> users = new ArrayList<>();
      users.add(User.builder().id(1).name("zhanglei").build());
      myService.blockedOfBusyCPU(users);
    } catch (Exception e) {
      log.error(e.getMessage());
    } finally {
      Assert.assertEquals(myService.count(), 0l);
    }
  }

  @Test
  public void timeoutWithBlockedIOTest() throws TimeoutAbortedException {
    try {
      List<User> users = new ArrayList<>();
      users.add(User.builder().id(1).name("zhanglei").build());
      myService.blockedOfIO(users);
    } catch (Exception e) {
      log.error(e.getMessage());
    } finally {
      Assert.assertEquals(myService.count(), 0l);
    }
  }

  @Test
  public void timeoutWithBlockedNestingTest() throws TimeoutAbortedException {
    try {
      List<User> users = new ArrayList<>();
      users.add(User.builder().id(1).name("zhanglei").build());
      users.add(User.builder().id(2).name("coolbeevip").build());
      myService.blockedOfNesting(users);
    } catch (Exception e) {
      log.error(e.getMessage());
    } finally {
      Assert.assertEquals(myService.count(), 0l);
    }
  }

  @Test(expected = TimeoutAbortedFailureException.class)
  public void timeoutWithAccessRejectionTest() throws TimeoutAbortedException {
      List<User> users = new ArrayList<>();
      users.add(User.builder().id(1).name("zhanglei").build());
      myService.accessRejectionBySecurityManager(users, 5000);
  }

  @Test(expected = TimeoutAbortedException.class)
  public void timeoutWithBlockedOfSleepCatchExceptionTest() throws TimeoutAbortedException {
    List<User> users = new ArrayList<>();
    users.add(User.builder().id(1).name("zhanglei").build());
    myService.blockedOfSleep(users, 5000);
  }

  @Test(expected = TimeoutAbortedException.class)
  public void timeoutWithBlockedOfWaitCatchExceptionTest() throws TimeoutAbortedException {
    List<User> users = new ArrayList<>();
    users.add(User.builder().id(1).name("zhanglei").build());
    myService.blockedOfWait(users, 5000);
  }
}