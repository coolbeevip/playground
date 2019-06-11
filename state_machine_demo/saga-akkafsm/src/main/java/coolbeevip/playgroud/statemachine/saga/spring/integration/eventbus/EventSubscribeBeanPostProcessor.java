package coolbeevip.playgroud.statemachine.saga.spring.integration.eventbus;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class EventSubscribeBeanPostProcessor implements BeanPostProcessor {

  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName)
      throws BeansException {
    return bean;
  }

  //对于每个容器执行了初始化的 bean，如果这个 bean 的某个方法注解了@Subscribe,则将该 bean 注册到事件总线
  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName)
      throws BeansException {
    // for each method in the bean
    Method[] methods = bean.getClass().getMethods();
    for (Method method : methods) {
      // check the annotations on that method
      Annotation[] annotations = method.getAnnotations();
      for (Annotation annotation : annotations) {
        // if it contains the Subscribe annotation
        if (annotation.annotationType().equals(Subscribe.class)) {
          // 如果这是一个Guava @Subscribe注解的事件监听器方法，说明所在bean实例
          // 对应一个Guava事件监听器类，将该bean实例注册到Guava事件总线
          eventBus.register(bean);
          return bean;
        }
      }
    }

    return bean;
  }

  // 事件总线bean由Spring IoC容器负责创建，这里只需要通过@Autowired注解注入该bean即可使用事件总线
  @Autowired
  EventBus eventBus;
}