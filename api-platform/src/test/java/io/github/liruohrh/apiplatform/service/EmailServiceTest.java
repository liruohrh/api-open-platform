package io.github.liruohrh.apiplatform.service;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;

@SpringBootTest
public class EmailServiceTest {
  @Resource
  EmailService emailService;
  @Resource
  JavaMailSender mailSender;
  @Test
  public void test() throws MessagingException {
    emailService.captcha("2372221537@qq.com");
  }
}
