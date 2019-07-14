package com.example.demo.service.impl;

import com.example.demo.service.PmsBrandService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@SpringBootTest
@RunWith(SpringRunner.class)
public class PmsBrandServiceImplTest {
    @Autowired
    private PmsBrandService service;

    @Test
    public void listAll() {
        service.listAllBrand();
    }
}