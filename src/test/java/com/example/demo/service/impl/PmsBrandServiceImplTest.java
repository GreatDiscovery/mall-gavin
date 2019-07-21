package com.example.demo.service.impl;

import com.example.demo.mbg.model.PmsBrand;
import com.example.demo.service.PmsBrandService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;


@SpringBootTest
@RunWith(SpringRunner.class)
public class PmsBrandServiceImplTest {
    @Autowired
    private PmsBrandService service;

    @Test
    public void listAll() {
        List<PmsBrand> list = service.listAllBrand();
       list.stream().forEach(System.out::println);
    }

    @Test
    public void create() {
        PmsBrand brand = new PmsBrand();
        brand.setId(11L);
        brand.setName("Mac Pro");
        service.createBrand(brand);
    }
}