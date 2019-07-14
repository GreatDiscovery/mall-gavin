package com.example.demo.service.impl;

import com.example.demo.mbg.mapper.PmsBrandMapper;
import com.example.demo.mbg.model.PmsBrand;
import com.example.demo.service.PmsBrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PmsBrandServiceImpl implements PmsBrandService {
    @Autowired
    private PmsBrandMapper brandMapper;
    @Override
    public List<PmsBrand> listAllBrand() {
//        return brandMapper.selectByExample(new PmsBrandExample());
        return null;
    }

    @Override
    public int createBrand(PmsBrand brand) {
        return 0;
    }

    @Override
    public int updateBrand(Long id, PmsBrand brand) {
        return 0;
    }

    @Override
    public int deleteBrand(Long id) {
        return 0;
    }

    @Override
    public PmsBrand getBrand(Long id) {
        return null;
    }

    @Override
    public List<PmsBrand> listBrand(int pageNum, int pageSize) {
        return null;
    }
}
