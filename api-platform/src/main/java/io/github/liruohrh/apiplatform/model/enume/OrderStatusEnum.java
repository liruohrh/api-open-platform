package io.github.liruohrh.apiplatform.model.enume;


public enum OrderStatusEnum implements ValueEnum<Integer>{
  WAIT_PAY(0),
  PAID(1),
  CANCEL(2),
  ;
  private final int value;

  OrderStatusEnum(int value) {
    this.value = value;
  }

  @Override
  public Integer getValue() {
    return value;
  }
}
