package io.github.liruohrh.apiplatform.service;

import io.github.liruohrh.apicommon.error.BusinessException;
import io.github.liruohrh.apicommon.error.ParamException;

public interface EmailService {
  /**
   * 5min内连续请求超过3次，则冻结10min
   */
  void captcha(String email);
  /**
   * @param email
   * @param code
   * @throws BusinessException 验证码过期
   * @throws ParamException    验证码错误
   */
  void verifyCaptcha(String email, String code);
}
