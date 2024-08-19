package io.github.liruohrh.apicommon.error;


public class ParamException extends BusinessException {
  public ParamException(String message) {
    super(Resp.fail(ErrorCode.PARAM, message));
  }
  public ParamException(Resp<Void> errorResp) {
    super(errorResp);
  }
}
