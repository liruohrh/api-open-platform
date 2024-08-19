package io.github.liruohrh.apiplatform.model.req.order;

import java.io.Serializable;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderCreateReq implements Serializable {
    @NotNull(message = "未知API")
    private Long apiId;

    private Integer amount;
    private Boolean free;
    private static final long serialVersionUID = 1L;
}