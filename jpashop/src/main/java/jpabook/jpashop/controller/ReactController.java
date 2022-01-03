package jpabook.jpashop.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RestController
public class ReactController {

    @GetMapping("/react/hello")
    public List<String> hello()
    {
        return Arrays.asList("현재 시간",  new Date().toString()+"입니다.");
    }

    @GetMapping("hello")
    public List<String> hello2() { return Arrays.asList("현재 시간",  new Date().toString()+"입니다."); }
}
