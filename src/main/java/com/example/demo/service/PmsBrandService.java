package com.example.demo.service;

import com.example.demo.mbg.model.PmsBrand;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PmsBrandService {
    List<PmsBrand> listAllBrand();
    int createBrand(PmsBrand brand);
    int updateBrand(Long id, PmsBrand brand);
    int deleteBrand(Long id);
    PmsBrand getBrand(Long id);
    List<PmsBrand> listBrand(int pageNum, int pageSize);
}
