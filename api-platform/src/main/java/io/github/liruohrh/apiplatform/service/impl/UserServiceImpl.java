package io.github.liruohrh.apiplatform.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.liruohrh.apicommon.error.BusinessException;
import io.github.liruohrh.apicommon.error.ErrorCode;
import io.github.liruohrh.apicommon.error.ParamException;
import io.github.liruohrh.apicommon.error.Resp;
import io.github.liruohrh.apiplatform.common.holder.LoginUserHolder;
import io.github.liruohrh.apiplatform.common.util.LoginUtils;
import io.github.liruohrh.apiplatform.common.util.MustUtils;
import io.github.liruohrh.apiplatform.constant.CommonConstant;
import io.github.liruohrh.apiplatform.mapper.UserMapper;
import io.github.liruohrh.model.entity.User;
import io.github.liruohrh.apiplatform.model.enume.LoginType;
import io.github.liruohrh.apiplatform.model.enume.RoleEnum;
import io.github.liruohrh.apiplatform.model.enume.UserStatusEnum;
import io.github.liruohrh.apiplatform.model.req.user.UserLoginReq;
import io.github.liruohrh.apiplatform.model.req.user.UserNewPasswdReq;
import io.github.liruohrh.apiplatform.model.req.user.UserRegisterReq;
import io.github.liruohrh.apiplatform.model.req.user.UserUpdateReq;
import io.github.liruohrh.apiplatform.service.EmailService;
import io.github.liruohrh.apiplatform.service.UserService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
* @author LYM
* @description 针对表【user】的数据库操作Service实现
* @createDate 2024-08-09 19:08:16
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

  private final String SALT = "api-platform.liruohrh";
  private final EmailService emailService;
  private final RedisTemplate<Object, Object> redisTemplate;
  public UserServiceImpl(
      EmailService emailService,
      RedisTemplate<Object, Object> redisTemplate
  ) {
    this.emailService = emailService;
    this.redisTemplate = redisTemplate;
  }
  @Override
  public void register(UserRegisterReq userRegisterReq) {
    emailService.verifyCaptcha(userRegisterReq.getEmail(), userRegisterReq.getCaptcha());
    User newUser = userRegisterReq.toUserEntity();


    //可以改为 SecureRandom，不需要固定SALT
    newUser.setAppKey(DigestUtil.md5Hex(
        SALT + newUser.getEmail() + RandomUtil.randomString(CommonConstant.APP_KEY_RANDOM_LEN)));
    newUser.setAppSecret(DigestUtil.md5Hex(SALT + newUser.getEmail() + RandomUtil.randomString(
        CommonConstant.APP_SECRET_RANDOM_LEN)));
    newUser.setPasswd( digestPasswd( newUser.getPasswd()));
    save(newUser);
  }

  @Override
  public User login(UserLoginReq userLoginReq) {
    User  loginUser = null;
    if(userLoginReq.getLoginType() == LoginType.CODE){
      User user = getOne(new LambdaQueryWrapper<User>()
          .eq(User::getEmail, userLoginReq.getEmail())
      );
      if(user == null){
        throw new ParamException(Resp.fail(ErrorCode.NO_REGISTER, "请先注册"));
      }
      if(!UserStatusEnum.COMMON.is(user.getStatus())){
        throw new BusinessException(ErrorCode.USER_FORBIDDEN);
      }
      emailService.verifyCaptcha(userLoginReq.getEmail(), userLoginReq.getCaptcha());
      loginUser = user;
    }if(userLoginReq.getLoginType() == LoginType.PASSWD){
      //密码登录：email、username同时存在，优先email
      User user = getOne(new LambdaQueryWrapper<User>()
          .eq(StrUtil.isNotEmpty(userLoginReq.getEmail()), User::getEmail, userLoginReq.getEmail())
          .eq(StrUtil.isEmpty(userLoginReq.getEmail()) && StrUtil.isNotEmpty(userLoginReq.getUsername()), User::getUsername,
              userLoginReq.getUsername())
          .eq(StrUtil.isNotEmpty(userLoginReq.getPasswd()), User::getPasswd,
              digestPasswd(userLoginReq.getPasswd())));
      if(user == null){
        throw new ParamException("请输入正确的密码登录参数");
      }
      if(!UserStatusEnum.COMMON.is(user.getStatus())){
        throw new BusinessException(ErrorCode.USER_FORBIDDEN);
      }
      loginUser = user;
    }
    LoginUtils.setLoginState(redisTemplate, loginUser.getId());
    return loginUser;
  }


  @Override
  public void newPasswd(UserNewPasswdReq userNewPasswdReq) {
    if(!exists(null, null, userNewPasswdReq.getEmail())){
      throw new ParamException("邮箱未注册");
    }
    emailService.verifyCaptcha(userNewPasswdReq.getEmail(), userNewPasswdReq.getCaptcha());
    MustUtils.dbSuccess(update(new LambdaUpdateWrapper<User>()
        .eq(User::getEmail, userNewPasswdReq.getEmail())
        .set(User::getPasswd, digestPasswd(userNewPasswdReq.getNewPasswd()))
    ));
  }

  @Override
  public void updateInfo(UserUpdateReq userUpdateReq) {
    User loginUser = LoginUserHolder.get();

    //admin 且 username=system才能更改role
    if(StrUtil.isNotEmpty(userUpdateReq.getRole())){
      if(!RoleEnum.ADMIN.is(loginUser.getRole()) ||  !"system".equals(loginUser.getUsername())){
        throw new ResponseStatusException(HttpStatus.FORBIDDEN);
      }
    }

    if(!exists(userUpdateReq.getId(), null, null)){
      throw new ParamException(Resp.fail(ErrorCode.NOT_EXISTS, "用户不存在"));
    }

    //非管理员不能更新其他人的
    if(!loginUser.getId().equals(userUpdateReq.getId()) && !RoleEnum.ADMIN.eq(loginUser.getRole())){
      throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }
    //管理员更新email不需要验证码
    if(!RoleEnum.ADMIN.eq(loginUser.getRole()) && StrUtil.isNotEmpty(userUpdateReq.getEmail())){
      if(StrUtil.isEmpty(userUpdateReq.getCaptcha())){
        throw new ParamException("更新邮箱需要验证码");
      }
      emailService.verifyCaptcha(loginUser.getEmail(), userUpdateReq.getCaptcha());
    }

    User user = BeanUtil.copyProperties(userUpdateReq, User.class);
    MustUtils.dbSuccess(updateById(user));
  }

  @Override
  public String resetAppSecret() {
    User loginUser = LoginUserHolder.get();
    loginUser.setAppSecret(DigestUtil.md5Hex(SALT + loginUser.getEmail() + RandomUtil.randomString(
        CommonConstant.APP_SECRET_RANDOM_LEN)));

    MustUtils.dbSuccess(update(new LambdaUpdateWrapper<User>()
        .eq(User::getId, loginUser.getId())
        .set(User::getAppSecret, loginUser.getAppSecret())
    ));
    return loginUser.getAppSecret();
  }

  private boolean exists(Long id, String name, String email) {
    LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<User>()
        .select(User::getId);
    if (id != null) {
      return getOne(queryWrapper
          .eq(User::getId, id)) != null;
    } else if (name != null) {
      return getOne(queryWrapper
          .eq(User::getUsername, name)) != null;
    } else if (email != null) {
      return getOne(queryWrapper
          .eq(User::getEmail, email)) != null;
    }
    return false;
  }

  private String digestPasswd(String passwd){
    return  DigestUtil.md5Hex(SALT + passwd);
  }
}




