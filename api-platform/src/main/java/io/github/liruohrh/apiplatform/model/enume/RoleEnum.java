package io.github.liruohrh.apiplatform.model.enume;

public enum RoleEnum implements ValueEnum<String>{
  USER("USER"),
  ADMIN("ADMIN"),
  SYSTEM("system"),
  ;
  private final String value;

  RoleEnum(String value) {
    this.value = value;
  }

  @Override
  public String getValue() {
    return value;
  }
  public boolean eq(String role) {
    return value.equals(role);
  }
}
