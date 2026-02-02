package com.dlmgroup.collectorcoin.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

	@RequestMapping(value = {
    "/",
    "/login",
    "/register",
    "/logout",
	"/admin",
    "/myportfolio",
    "/myportfolio/{id}",
    "/newlisting",
	"/web3-test",
    "/listings/{id}",
    "/newbid",
	"/selectfrombids",
    "/restorerportfolio",
    "/investedPortfolio",
    "/auction"
  })
	public String index() {
		return "index";
  }
}
