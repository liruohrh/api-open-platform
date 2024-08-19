package io.github.liruohrh.apiplatform.rpc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.liruohrh.apiplatform.service.UserService;
import io.github.liruohrh.model.entity.User;
import io.github.liruohrh.rpc.service.RpcUserService;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService
public class RpcUserServiceImpl implements RpcUserService {
    private final UserService userService;

  public RpcUserServiceImpl(UserService userService) {
    this.userService = userService;
  }


  @Override
  public User getUserByAppKey(String appKey) {
    return userService.getOne(new LambdaQueryWrapper<User>()
        .eq(User::getAppKey, appKey)
    );
  }
}
