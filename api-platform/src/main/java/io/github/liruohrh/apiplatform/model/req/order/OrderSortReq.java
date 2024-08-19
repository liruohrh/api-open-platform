package io.github.liruohrh.apiplatform.model.req.order;

import io.github.liruohrh.apiplatform.model.enume.SortEnum;
import java.io.Serializable;
import lombok.Data;

@Data
public class OrderSortReq implements Serializable {
  private SortEnum ctime;
  private SortEnum actualPayment;
  private SortEnum amount;
  private SortEnum utime;
  private static final long serialVersionUID = 1L;
}
