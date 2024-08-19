package io.github.liruohrh.apiplatform.constant;

public interface CommonConstant {
    String COOKIE_LOGIN_NAME = "api-token";
    String PATTERN_USERNAME = "\\w+?";
    String PATTERN_EMAIL = "^[A-Za-z0-9\\u4e00-\\u9fa5]+@[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(?:\\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+$";
    int CAPTCHA_SIZE = 6;
    int APP_KEY_RANDOM_LEN = 6;
    int APP_SECRET_RANDOM_LEN = 8;

    int MAX_CALL_CAPTCHA = 3;
    int MAX_CALL_VERIFY_CAPTCHA = 4;

    String SESSION_LOGIN_USER = "LOGIN_USER";


    int PAGE_MAX_SIZE_USER = 10;
    int PAGE_MAX_SIZE_API = 15;
    int PAGE_MAX_SIZE_API_SEARCH = 12;
    int PAGE_MAX_SIZE_ORDER = 15;
}
