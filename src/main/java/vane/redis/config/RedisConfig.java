package vane.redis.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;
import vane.redis.cache.GlobalCache;
import vane.redis.cache.impl.GlobalCacheImpl;

@EnableCaching
@Configuration
public class RedisConfig {

  @Value("${spring.redis.host}")
  private String host;

  @Value("${spring.redis.database}")
  private Integer database;

  @Value("${spring.redis.port}")
  private Integer port;

  @Value("${spring.redis.password}")
  private String password;

  @Value("${custom.redis.config.max.wait.mills}")
  private Long maxWaitMills;

  @Primary
  @Bean(name = "jedisPoolConfig")
  @ConfigurationProperties(prefix = "spring.redis.pool")
  public JedisPoolConfig jedisPoolConfig() {
    JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
    jedisPoolConfig.setMaxWaitMillis(maxWaitMills);
    return jedisPoolConfig;
  }

  public RedisConnectionFactory redisConnectionFactory(JedisPoolConfig jedisPoolConfig) {
    RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
    config.setHostName(host);
    config.setDatabase(database);
    config.setPassword(password);
    config.setPort(port);
    JedisClientConfiguration.JedisPoolingClientConfigurationBuilder builder =
        (JedisClientConfiguration.JedisPoolingClientConfigurationBuilder)
            JedisClientConfiguration.builder();
    builder.poolConfig(jedisPoolConfig);
    JedisClientConfiguration jedisClientConfiguration = builder.build();
    return new JedisConnectionFactory(config, jedisClientConfiguration);
  }

  // 配置 redisTemplate 针对不同 key 和 value 场景下不同序列化的方式
  public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(factory);
    StringRedisSerializer serializer = new StringRedisSerializer();
    template.setKeySerializer(serializer);
    template.setHashKeySerializer(serializer);
    Jackson2JsonRedisSerializer jsonSerializer = new Jackson2JsonRedisSerializer(Object.class);
    template.setValueSerializer(jsonSerializer);
    template.setHashKeySerializer(jsonSerializer);
    template.afterPropertiesSet();
    return template;
  }

  @Bean
  GlobalCache cache(RedisTemplate redisTemplate) {
    return new GlobalCacheImpl(redisTemplate);
  }
}
