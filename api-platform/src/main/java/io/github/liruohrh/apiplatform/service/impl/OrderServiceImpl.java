package io.github.liruohrh.apiplatform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.liruohrh.model.entity.Order;
import io.github.liruohrh.apiplatform.service.OrderService;
import io.github.liruohrh.apiplatform.mapper.OrderMapper;
import org.springframework.stereotype.Service;

/**
* @author LYM
* @description 针对表【order】的数据库操作Service实现
* @createDate 2024-08-12 17:39:30
*/
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order>
    implements OrderService {

}




