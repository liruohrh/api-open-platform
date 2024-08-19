package io.github.liruohrh.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName order
 */
@TableName(value ="goods_order")
@Data
public class Order implements Serializable {
    /**
     * 
     */
    @TableId
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

    /**
     * 
     */
    @TableField(fill = FieldFill.INSERT)
    private Long ctime;

    /**
     * 
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long utime;

    /**
     * 
     */
    @TableLogic
    private Integer deleted;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}