package com.company.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ReactAppController {

  @RequestMapping(value = { "/website", "/website/{x:[\\w\\-]+}", "/website/{x:^(?!api$).*$}/**/{y:[\\w\\-]+}" })
  public String getIndex() {
    return "forward:/index.html";
  }
}