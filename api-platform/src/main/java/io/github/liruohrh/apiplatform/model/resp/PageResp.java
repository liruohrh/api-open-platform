package io.github.liruohrh.apiplatform.model.resp;

import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PageResp<T> implements Serializable {
  private List<T> data;
  private long total;
  private long current;
  private long pages;
  private long size;
  private static final long serialVersionUID = 1L;
}
