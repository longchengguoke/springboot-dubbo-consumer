package com.dubbo.consumer.controller;

import com.dubbo.server.service.TestService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * @author Mr.C
 * @Description
 * @create 2018/7/16 17:57
 * Copyright: Copyright (c) 2018
 * Company:CWWT
 */

@Controller
public class TestController {

    @Resource
    private TestService testService;
    @RequestMapping(value = "/" ,produces = "application/json;charset=utf-8")
    @ResponseBody
    private String test(){
        return testService.sayHello("chengwei");
    }

}
