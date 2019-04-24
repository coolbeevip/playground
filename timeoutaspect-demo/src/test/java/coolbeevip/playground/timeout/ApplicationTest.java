package coolbeevip.playground.timeout;

import coolbeevip.playground.timeout.exception.TimeoutAspectException;
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
    myService.save(users, 2000);
    Optional<User> user = myService.find(1);
    Assert.assertNotNull(user.get());
    Assert.assertEquals(user.get().getId(), Integer.valueOf(1));
  }

  /**
   * simulation execution 2s
   */
  @Test
  public void noTimeoutBatchTest() throws TimeoutAspectException {
    try {
      List<User> users = new ArrayList<>();
      users.add(User.builder().id(1).name("zhanglei").build());
      users.add(User.builder().id(2).name("coolbeevip").build());
      myService.save(users, 2000);
    } finally {
      Assert.assertEquals(myService.count(), 2l);
    }
  }

  @Test
  public void noTimeoutUniqueIndexExceptionTest() throws TimeoutAspectException {
    try {
      List<User> users = new ArrayList<>();
      users.add(User.builder().id(1).name("zhanglei").build());
      users.add(User.builder().id(2).name("zhanglei").build());
      myService.save(users, 2000);
    } catch (Exception e) {
      log.error(e.getMessage());
    } finally {
      Assert.assertEquals(myService.count(), 0l);
    }
  }

  @Test
  public void timeoutText() throws TimeoutAspectException {
    try {
      List<User> users = new ArrayList<>();
      users.add(User.builder().id(1).name("zhanglei").build());
      myService.save(users, 3000);
    } catch (Exception e) {
      log.error(e.getMessage());
    } finally {
      Assert.assertEquals(myService.count(), 0l);
    }
  }

  @Test
  public void timeoutBatchText() throws TimeoutAspectException {
    try {
      List<User> users = new ArrayList<>();
      users.add(User.builder().id(1).name("zhanglei").build());
      users.add(User.builder().id(2).name("coolbeevip").build());
      myService.save(users, 3000);
    } catch (Exception e) {
      log.error(e.getMessage());
    } finally {
      Assert.assertEquals(myService.count(), 0l);
    }
  }

  @Test(expected = TimeoutAspectException.class)
  public void timeoutCatchTimeoutAspectExceptionTest() throws TimeoutAspectException {
    List<User> users = new ArrayList<>();
    users.add(User.builder().id(1).name("zhanglei").build());
    myService.save(users, 3000);
  }
}