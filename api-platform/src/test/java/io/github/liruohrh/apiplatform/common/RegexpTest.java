package io.github.liruohrh.apiplatform.common;

import io.github.liruohrh.apiplatform.constant.CommonConstant;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RegexpTest {

  @Test
  public void testEmail() {
    Assertions.assertTrue(Pattern.matches(CommonConstant.PATTERN_EMAIL, "123456@qq.com"));
  }
}
