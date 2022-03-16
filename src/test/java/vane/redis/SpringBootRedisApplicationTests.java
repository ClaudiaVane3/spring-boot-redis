package vane.redis;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import vane.redis.cache.GlobalCache;

import java.util.Arrays;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringBootRedisApplicationTests {

  @Autowired private GlobalCache globalCache;

  @Test
  public void test() {
    globalCache.set("key2", "value3");
    globalCache.lSetAll("list", Arrays.asList("hello", "redis"));
    System.out.println(globalCache.get("key2"));
  }
}
