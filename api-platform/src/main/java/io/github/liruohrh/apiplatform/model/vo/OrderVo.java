package io.github.liruohrh.apiplatform.model.vo;

import java.io.Serializable;
import lombok.Data;

@Data
public class OrderVo implements Serializable {
    private Long id;

    /**
     * 
     */
    private String orderId;

    /**
     * 
     */
    private Long apiId;

    /**
     * 
     */
    private Long userId;

    /**
     * 
     */
    private Double actualPayment;

    /**
     * 
     */
    private Integer amount;

    /**
     * 0-待支付，1-已支付，2-已取消
     */
    private Integer status;
    private Double price;

    private Long ctime;
    private Long utime;

    private static final long serialVersionUID = 1L;
}