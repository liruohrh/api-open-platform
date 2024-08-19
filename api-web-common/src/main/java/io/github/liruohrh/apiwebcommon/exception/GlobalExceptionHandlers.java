package io.github.liruohrh.apiwebcommon.exception;

import io.github.liruohrh.apicommon.error.BusinessException;
import io.github.liruohrh.apicommon.error.ErrorCode;
import io.github.liruohrh.apicommon.error.ParamException;
import io.github.liruohrh.apicommon.error.Resp;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import javax.validation.ConstraintViolation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

/**
 * @Author:liruo
 * @Date:2023-06-10-17:07:07
 * @Desc
 * Spring DispatcherServlet
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandlers {
  @ExceptionHandler({ServletException.class})
  public ResponseEntity<Resp<Void>> servletExceptionHandler(
      HttpServletRequest req,
      HttpServletResponse resp,
      ServletException ex
  ) {
    HttpStatus status = ExceptionResolverUtil
        .handlerServletException(req, resp, ex);
    if (status == null) {
      status = HttpStatus.INTERNAL_SERVER_ERROR;
    }
    log.warn("Servlet: status={}", status, ex);
    return new ResponseEntity<Resp<Void>>(new Resp(status.value(), ex.getMessage(), null),  status);
  }

  @ExceptionHandler({ResponseStatusException.class})
  public ResponseEntity<Resp<Void>> responseStatusExceptionHandler(
      HttpServletRequest req,
      HttpServletResponse resp,
      ResponseStatusException error
  ) {
    ExceptionResolverUtil.handlerResponseStatusException(req, resp, error);
    log.warn("ResponseStatus", error);
    return new ResponseEntity<Resp<Void>>(new Resp(error.getStatus().value(), error.getMessage(), null), error.getStatus());
  }

  /**
   * BindException: ModelAttributeMethodProcessor(@ModelAttributeMethod、no annotation custom bean)
   * ConstraintViolationException: @Validated on method parameters
   * MethodArgumentNotValidException: RequestPartMethodArgumentResolver、RequestResponseBodyMethodProcessor
   * ServletRequestBindingException: such as @RequestHeader
   */
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler({
      BindException.class,
      ConstraintViolationException.class,
      MethodArgumentNotValidException.class,
      ServletRequestBindingException.class,
      ParamException.class,
  })
  public Resp<Void> badRequestExceptionHandler(Exception err) {
    Resp<Void> resp = null;
    boolean needLog = true;
    if (err instanceof ParamException) {
      ParamException e = (ParamException) err;
      resp = e.errorResp;
    }
    else if (err instanceof ConstraintViolationException) {
      String errorList = ((ConstraintViolationException) err)
          .getConstraintViolations()
          .stream()
          .map(ConstraintViolation::getMessage)
          .collect(Collectors.joining(", "));
      resp = Resp.fail(ErrorCode.PARAM, errorList);
    }
    else if (err instanceof BindException) {
      //also MethodArgumentNotValidException
      String errorList = ((BindException) err)
          .getAllErrors()
          .stream()
          .map(DefaultMessageSourceResolvable::getDefaultMessage)
          .collect(Collectors.joining(", "));
      resp = Resp.fail(ErrorCode.PARAM, errorList);
    }

    log.warn("badRequest for param: {}", resp.getCode(), err);
    return resp;
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler({BusinessException.class})
  public Resp<Void> businessExceptionHandler(BusinessException err) {
    log.warn("business: {}", err.errorResp.getCode(), err);
    return err.errorResp;
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler({Throwable.class})
  public Resp<Void> systemExceptionHandler(Throwable err) {
    log.error("system: {}",ErrorCode.SYSTEM.getCode(),   err);
    return Resp.fail(ErrorCode.SYSTEM);
  }
}
