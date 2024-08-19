package io.github.liruohrh.apiweb.controller;

import javax.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/e")
@RestController
public class ErrorGatewayTestController {
  @GetMapping
  public void e(HttpServletResponse resp){
    resp.setStatus(500);
  }
}
