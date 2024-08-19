package io.github.liruohrh.apiplatform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.liruohrh.model.entity.User;
import javax.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserServiceTest {
  @Resource
  UserService userService;

  @Test
  public void test() {
    Page<User> page = userService.page(new Page<>(1, 10), new LambdaQueryWrapper<User>()
        .orderBy(true, false, User::getCtime)
    );
    System.out.println(page.getRecords());
  }
}
