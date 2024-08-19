package io.github.liruohrh.apiplatform.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import io.github.liruohrh.apicommon.error.BusinessException;
import io.github.liruohrh.apicommon.error.ErrorCode;
import io.github.liruohrh.apicommon.error.ParamException;
import io.github.liruohrh.apicommon.error.Resp;
import io.github.liruohrh.apiplatform.common.holder.RequestHolder;
import io.github.liruohrh.apiplatform.common.util.EmailUtils;
import io.github.liruohrh.apiplatform.common.util.RequestUtils;
import io.github.liruohrh.apiplatform.constant.CommonConstant;
import io.github.liruohrh.apiplatform.constant.RedisConstant;
import io.github.liruohrh.apiplatform.service.EmailService;
import java.util.concurrent.TimeUnit;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

  /**
   * 因为删除验证码可以异步且不用确认，因此就用RedissonClient
   */
  private final RedissonClient redissonClient;
  private final String from;

  private final JavaMailSender mailSender;

  public EmailServiceImpl(
      JavaMailSender mailSender,
      @Value("${spring.mail.username}") String from,
      RedissonClient redissonClient
  ) {
    this.mailSender = mailSender;
    this.from = from;
    this.redissonClient = redissonClient;
  }

  @Override
  public void captcha(String email) {
    //接口保护
    RBucket<String> freezeString = redissonClient.getBucket(
        RedisConstant.PREFIX_EMAIL_FREEZE + email);
    if (freezeString.isExists()) {
      throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "email被锁定5小时");
    }
    RBucket<Integer> countString = redissonClient.getBucket(RedisConstant.PREFIX_EMAIL_MAX + email);
    Integer sendCount = countString.get();
    if (sendCount != null) {
      if (sendCount > CommonConstant.MAX_CALL_CAPTCHA) {
        freezeString.set("");
        HttpServletRequest req = RequestHolder.get();
        log.warn("用户请求邮箱验证码超过{}次，email={}, addr={}，headers={}",
            CommonConstant.MAX_CALL_CAPTCHA, email,
            req.getRemoteAddr(), RequestUtils.getHeaders(req)
        );
        throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "email被锁定5小时");
      }
      countString.setIfExists(sendCount + 1);
    } else {
      countString.set(1, RedisConstant.EXPIRE_EMAIL_MAX, TimeUnit.MILLISECONDS);
    }

    //生成验证码
    String captcha = RandomUtil.randomString(CommonConstant.CAPTCHA_SIZE);
    try {
      mailSender.send(
          EmailUtils.fillEmail(mailSender, from, email,
              "API Platform",
              "你的验证码是： " + captcha)
      );
      redissonClient.getBucket(RedisConstant.PREFIX_EMAIL_CAPTCHA + email)
          .set(email, RedisConstant.EXPIRE_EMAIL_CAPTCHA, TimeUnit.MILLISECONDS);
    } catch (MessagingException e) {
      log.debug("邮箱发送失败，email={} ", email, e);
      throw new BusinessException("发送邮件验证码失败，请检查邮箱");
    }
  }


  @Override
  public void verifyCaptcha(String email, String code) {
    //接口保护
    RBucket<String> freezeString = redissonClient.getBucket(
        RedisConstant.PREFIX_EMAIL_VERIFY_FREEZE + email);
    if (freezeString.isExists()) {
      throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "email被锁定5小时");
    }
    RBucket<Integer> countString = redissonClient.getBucket(
        RedisConstant.PREFIX_EMAIL_VERIFY_CAPTCHA + email);
    Integer sendCount = countString.get();
    if (sendCount != null) {
      if (sendCount > CommonConstant.MAX_CALL_VERIFY_CAPTCHA) {
        freezeString.set("");
        HttpServletRequest req = RequestHolder.get();
        log.warn("用户验证邮箱验证码超过{}次，email={}, addr={}，headers={}",
            CommonConstant.MAX_CALL_VERIFY_CAPTCHA, email,
            req.getRemoteAddr(), RequestUtils.getHeaders(req)
        );
        throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "email被锁定5小时");
      }
      countString.setIfExists(sendCount + 1);
    } else {
      countString.set(1, RedisConstant.EXPIRE_EMAIL_MAX, TimeUnit.MILLISECONDS);
    }

    //验证验证码
    RBucket<String> captchaString = redissonClient.getBucket(RedisConstant.PREFIX_EMAIL_CAPTCHA);
    String captcha = captchaString.get();
    if (captcha == null) {
      throw new BusinessException(Resp.fail(ErrorCode.TIMEOUT, "验证码过期"));
    }
    if (!StrUtil.equals(code, captcha)) {
      throw new ParamException("验证码错误");
    }
    //验证码成功
    captchaString.deleteAsync();
  }
}
