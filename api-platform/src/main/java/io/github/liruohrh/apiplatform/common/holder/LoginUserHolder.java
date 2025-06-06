package io.github.liruohrh.apiplatform.common.holder;

import com.google.common.base.Supplier;
import io.github.liruohrh.model.entity.User;

public class LoginUserHolder {
  private static final ThreadLocal<Boolean> needLoginThreadLocal = new ThreadLocal<>();
  private static final ThreadLocal<Supplier<Long>> loginUserIdGetterThreadLocal = new ThreadLocal<>();
  private static final ThreadLocal<Supplier<User>> userGetterThreadLocal = new ThreadLocal<>();
  public static void set(
      boolean needLogin,
      Supplier<Long> loginUserIdGetter,
      Supplier<User> userGetter
  ){
    needLoginThreadLocal.set(needLogin);
    loginUserIdGetterThreadLocal.set(loginUserIdGetter);
    userGetterThreadLocal.set(userGetter);
  }
  public static User get(){
    return userGetterThreadLocal.get().get();
  }
  public static boolean needLogin(){
    return needLoginThreadLocal.get();
  }
  public static boolean isLogin(){
    return loginUserIdGetterThreadLocal.get() != null;
  }
  public static Long getUserId(){
    Supplier<Long> supplier = loginUserIdGetterThreadLocal.get();
    return supplier == null ? null : supplier.get();
  }

  public static void clear() {
    needLoginThreadLocal.remove();
    loginUserIdGetterThreadLocal.remove();
    userGetterThreadLocal.remove();
  }
}
