package io.github.liruohrh.apiplatform.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FloatParseTest {

  @Test
  public void test() throws JsonProcessingException {
    {
      Float fVal = 1.11f;
      Double dVal = 1.11;
      ObjectMapper objectMapper = new ObjectMapper();
      Assertions.assertEquals("1.11", objectMapper.writeValueAsString(fVal));
      Assertions.assertEquals("1.11", objectMapper.writeValueAsString(dVal));
      Assertions.assertEquals(fVal, objectMapper.readValue("\"1.11\"", Float.class));
      Assertions.assertEquals(dVal, objectMapper.readValue("\"1.11\"", Double.class));
    }


    {
      Float fVal = 1.11f;
      Double dVal = 1.11;
      Assertions.assertEquals("1.11", fVal.toString());
      Assertions.assertEquals("1.11", dVal.toString());
      Assertions.assertEquals(fVal, Float.parseFloat("1.11"));
      Assertions.assertEquals(dVal, Double.parseDouble("1.11"));
    }
  }
}
