package com.example.demo.service.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;
@SpringBootTest
@RunWith(SpringRunner.class)
public class RedisServiceImplTest {

    @Autowired
    private RedisServiceImpl redisService;
    @Test
    public void get() {
        System.out.printf("v1的值为：" +  redisService.get("k1"));
    }

    @Test
    public void expire() {
    }
}