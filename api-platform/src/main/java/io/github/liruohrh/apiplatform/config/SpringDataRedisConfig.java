package io.github.liruohrh.apiplatform.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.annotation.Resource;
import org.redisson.codec.JsonJacksonCodec;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * 因为Redisson不支持转换，除非都是JSON值，因此用 SpringDataRedis ，需要Redisson时才用RedissonClient
 */
@Configuration
public class SpringDataRedisConfig {  
  
  /**  
   * 配置RedisTemplate序列化，因为SpringSession用的是SpringDataRedis接口（只用SpringDataRedis的序列化）  
   */  
  @Resource
  public void configRedisTemplate(RedisTemplate<Object, Object> redisTemplate) {  
    Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(  
        Object.class);  
    ObjectMapper objectMapper = new ObjectMapper();  
    JsonJacksonCodecAdapter.initObjectMapper(objectMapper);  
    serializer.setObjectMapper(objectMapper);  
  
    redisTemplate.setKeySerializer(RedisSerializer.string());
    redisTemplate.setHashKeySerializer(RedisSerializer.string());

    redisTemplate.setValueSerializer(serializer);
    redisTemplate.setHashValueSerializer(serializer);
  }

  /**
   * 使用redisson的Jackson配置
   */
  public static class JsonJacksonCodecAdapter extends JsonJacksonCodec {  
  
    public static void initObjectMapper(ObjectMapper objectMapper) {  
      JsonJacksonCodecAdapter adapter = new JsonJacksonCodecAdapter();  
      adapter.init(objectMapper);  
      adapter.initTypeInclusion(objectMapper);  
    }  
  }  
}