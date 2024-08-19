package io.github.liruohrh.apiplatform.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.liruohrh.apicommon.error.ParamException;
import io.github.liruohrh.apicommon.error.Resp;
import io.github.liruohrh.apiplatform.aop.PreAuth;
import io.github.liruohrh.apiplatform.common.holder.LoginUserHolder;
import io.github.liruohrh.apiplatform.common.util.LoginUtils;
import io.github.liruohrh.apiplatform.common.util.ValidatorUtils;
import io.github.liruohrh.apiplatform.common.validator.LoginParamCheck;
import io.github.liruohrh.apiplatform.common.validator.UniqueAccount;
import io.github.liruohrh.apiplatform.constant.CommonConstant;
import io.github.liruohrh.model.entity.User;
import io.github.liruohrh.apiplatform.model.enume.SortEnum;
import io.github.liruohrh.apiplatform.model.req.PageReq;
import io.github.liruohrh.apiplatform.model.req.user.UserLoginReq;
import io.github.liruohrh.apiplatform.model.req.user.UserNewPasswdReq;
import io.github.liruohrh.apiplatform.model.req.user.UserRegisterReq;
import io.github.liruohrh.apiplatform.model.req.user.UserSortReq;
import io.github.liruohrh.apiplatform.model.req.user.UserUpdateReq;
import io.github.liruohrh.apiplatform.model.resp.PageResp;
import io.github.liruohrh.apiplatform.model.vo.UserVo;
import io.github.liruohrh.apiplatform.service.UserService;
import java.util.stream.Collectors;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/user")
@RestController
@Validated
public class UserController {

  private final UserService userService;
  private final RedisTemplate<Object, Object> redisTemplate;

  public UserController(
      UserService userService,
      RedisTemplate<Object, Object> redisTemplate
  ) {
    this.userService = userService;
    this.redisTemplate = redisTemplate;
  }
  @PreAuth(mustRole = "ADMIN")
  @GetMapping("/{userId}")
  public Resp<UserVo> getUserById(@PathVariable("userId") Long userId) {
    User user = userService.getById(userId);
    user.setPasswd(null);
    user.setAppSecret(null);
    return Resp.ok(BeanUtil.copyProperties(user, UserVo.class));
  }

  @PreAuth(mustRole = "ADMIN")
  @PostMapping("/list")
  public Resp<PageResp<UserVo>> listUser(@RequestBody PageReq<UserVo, UserSortReq> pageReq) {
    if (pageReq.getCurrent() == null) {
      pageReq.setCurrent(1);
    }
    UserVo userVo = pageReq.getSearch();
    UserSortReq sort = pageReq.getSort();
    LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
    if (userVo != null) {
      if(StrUtil.isNotEmpty(userVo.getNickname())){
        queryWrapper.like(User::getNickname, userVo.getNickname());
        userVo.setNickname(null);
      }
      if(StrUtil.isNotEmpty(userVo.getUsername())){
        queryWrapper.like(User::getUsername, userVo.getUsername());
        userVo.setUsername(null);
      }
      if(StrUtil.isNotEmpty(userVo.getPersonalDescription())){
        queryWrapper.like(User::getPersonalDescription, userVo.getPersonalDescription());
        userVo.setPersonalDescription(null);
      }
      queryWrapper.setEntity(BeanUtil.copyProperties(userVo, User.class));
    }
    if(sort  != null){
      queryWrapper.orderBy(sort.getCtime()  != null, sort.getCtime() == SortEnum.ASC, User::getCtime);
    }
    Page<User> userPageQuery =  userService.page(
        new Page<>(pageReq.getCurrent(), CommonConstant.PAGE_MAX_SIZE_USER),
        queryWrapper
    );

    return Resp.ok(new PageResp<>(
        userPageQuery.getRecords().stream()
            .map(user -> BeanUtil.copyProperties(user, UserVo.class))
            .collect(Collectors.toList()),
        userPageQuery.getTotal(),
        userPageQuery.getCurrent(),
        userPageQuery.getPages(),
        userPageQuery.getSize()
    ));
  }

  @PutMapping("/app-secret")
  public Resp<Void> resetUserAppSecret() {
    userService.resetAppSecret();
    return Resp.ok(null);
  }

  @PutMapping
  public Resp<Boolean> updateUser(@RequestBody @Validated UserUpdateReq userUpdateReq) {
    if (!ValidatorUtils.isAllNull(userUpdateReq, 1)) {
      userService.updateInfo(userUpdateReq);
      return Resp.ok(true);
    }
    return Resp.ok(false);
  }

  @PostMapping("/passwd")
  public Resp<Void> newUserPasswd(@RequestBody @Validated UserNewPasswdReq userNewPasswdReq) {
    userService.newPasswd(userNewPasswdReq);
    return Resp.ok(null);
  }

  @PostMapping("/logout")
  public Resp<Void> logout() {
    LoginUtils.clearLoginState(redisTemplate);
    return Resp.ok(null);
  }

  @PostMapping("/register")
  public Resp<Void> register(
      @Validated @UniqueAccount @RequestBody UserRegisterReq userRegisterReq) {
    userService.register(userRegisterReq);
    return Resp.ok(null);
  }

  @PostMapping("/login")
  public Resp<User> login(@Validated @LoginParamCheck @RequestBody UserLoginReq userLoginReq) {
    if(LoginUserHolder.isLogin()){
      throw new ParamException("不允许多次登录");
    }
    return Resp.ok(userService.login(userLoginReq));
  }

  @GetMapping
  public Resp<User> getLoginUser() {
    return Resp.ok(LoginUserHolder.get());
  }
}
