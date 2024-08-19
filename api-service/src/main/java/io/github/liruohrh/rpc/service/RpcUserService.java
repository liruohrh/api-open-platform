package io.github.liruohrh.rpc.service;

import io.github.liruohrh.model.entity.User;

public interface RpcUserService {
  User getUserByAppKey(String appKey);
}
