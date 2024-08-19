package io.github.liruohrh.apiplatform.constant;

public interface RedisConstant {
  String PREFIX = "apiplatform:";
  String PREFIX_LOGIN = PREFIX + "login:";
  long EXPIRE_LOGIN = 7 * 24 * 60 * 60 * 1000;




  String PREFIX_EMAIL_CAPTCHA = PREFIX + "email:captcha:";
  long EXPIRE_EMAIL_CAPTCHA = 5 * 60  * 1000;
  String PREFIX_EMAIL_MAX = PREFIX + "email:max:";
  long EXPIRE_EMAIL_MAX = 5 * 60 * 1000;
  String PREFIX_EMAIL_FREEZE = PREFIX + "email:freeze:";
  long EXPIRE_EMAIL_FREEZE = 5 * 60 * 60 * 1000;


  String PREFIX_EMAIL_VERIFY_FREEZE = PREFIX + "email:verify:freeze:";
  long EXPIRE_EMAIL_VERIFY_FREEZE = 5 * 60 * 60 * 1000;
  String PREFIX_EMAIL_VERIFY_CAPTCHA = PREFIX + "email:verify:captcha";
  long EXPIRE_EMAIL_VERIFY_CAPTCHA = 5 * 60 * 1000;
}
