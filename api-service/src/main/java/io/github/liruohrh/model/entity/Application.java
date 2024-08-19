package io.github.liruohrh.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName application
 */
@TableName(value ="application")
@Data
public class Application implements Serializable {
    /**
     * 
     */
    @TableId
    private Long id;

    /**
     * 申请类型
     */
    private String applicationType;

    /**
     * 
     */
    private String title;

    /**
     * 
     */
    private String content;

    /**
     * 
     */
    private String replyContent;

    /**
     * 根据不同申请类型做出不同审核状态回调，0-待审核，1-审核通过，2-审核不通过
     */
    private String auditStatus;

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

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}