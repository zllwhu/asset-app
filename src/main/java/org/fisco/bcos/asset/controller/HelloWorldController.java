package org.fisco.bcos.asset.controller;

import org.fisco.bcos.asset.entity.HelloWorld;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/helloworld")
public class HelloWorldController {
    private HelloWorld helloWorld;

    private static final Logger logger = LoggerFactory.getLogger(HelloWorldController.class);

    public HelloWorldController(HelloWorld helloWorld) {
        this.helloWorld = helloWorld;
        logger.info("hello");
    }

    @GetMapping
    public String hello() {
        logger.debug("hello-run");
        return helloWorld.hello();
    }
}
