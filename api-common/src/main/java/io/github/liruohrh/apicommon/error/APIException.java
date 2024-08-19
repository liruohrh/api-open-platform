package io.github.liruohrh.apicommon.error;


public class APIException extends BusinessException {
  public APIException(String message) {
    super(Resp.fail(ErrorCode.API_CALL, message));
  }
  public APIException(Resp<Void> errorResp) {
    super(errorResp);
  }
}
