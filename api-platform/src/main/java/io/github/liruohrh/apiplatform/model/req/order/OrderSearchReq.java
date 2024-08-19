package io.github.liruohrh.apiplatform.model.req.order;

import io.github.liruohrh.apiplatform.model.OrderNumber;
import java.io.Serializable;
import lombok.Data;
@Data
public class OrderSearchReq implements Serializable {
    private Long apiId;
    private OrderNumber actualPayment;
    private OrderNumber amount;
    /**
     * 0-待支付，1-已支付，2-已取消
     */
    private Integer status;
    private OrderNumber price;
    private OrderNumber ctime;
    private OrderNumber utime;
    private static final long serialVersionUID = 1L;
}