package io.github.liruohrh.apiplatform.model;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import io.github.liruohrh.model.entity.Order;
import java.io.Serializable;
import lombok.Data;

@Data
public class OrderNumber implements Serializable {
  private Number value;
  private Boolean isBefore;
  private static final long serialVersionUID = 1L;
  public void query(LambdaQueryWrapper<Order> queryWrapper, SFunction<Order, ?> getter){
    queryWrapper.le(
        Boolean.TRUE.equals(isBefore),
        getter,
        value
    );
    queryWrapper.ge(
        Boolean.FALSE.equals(isBefore),
        getter,
        value
    );
    queryWrapper.eq(
        isBefore == null,
        getter,
        value
    );
  }
}
