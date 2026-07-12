package com.hei.school.endpoint.rest.controller;

import com.hei.school.service.test.HelloWorldService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class HelloS3Controller {
  private final HelloWorldService service;

  @GetMapping("/file")
  public String helloWorld(@RequestParam String name) {
    return service.uploadHelloWorldMessage(name);
  }
}
