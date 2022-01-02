package jpabook.jpashop.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class ReactController {

    @GetMapping("/react/hello")
    public String hello() {
        return "현재 시간은 " + new Date() + "입니다.";
    }
}
