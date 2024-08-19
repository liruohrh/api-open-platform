package io.github.liruohrh.apiplatform.common.holder;

import io.github.liruohrh.model.entity.User;
import java.util.function.Supplier;

public class LoginUserHolder {
  private static final ThreadLocal<Supplier<User>> threadLocal = new ThreadLocal<>();
  private static final ThreadLocal<Long> loginUserIdThreadLocal = new ThreadLocal<>();
  public static void set(Long userId, Supplier<User> subject){
    threadLocal.set(new Supplier<User>() {
      User user = null;
      @Override
      public User get() {
        if(user == null){
          user = subject.get();
        }
        return user;
      }
    });
    loginUserIdThreadLocal.set(userId);
  }
  public static User get(){
    return threadLocal.get().get();
  }
  public static boolean isLogin(){
    return loginUserIdThreadLocal.get() != null;
  }
  public static Long getUserId(){
    return loginUserIdThreadLocal.get();
  }
}
