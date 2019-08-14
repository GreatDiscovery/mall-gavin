package com.example.demo.service;

import com.example.demo.common.api.CommonResult;

public interface UmsMemberService {
    CommonResult generateAuthCode(String telephone);
    CommonResult verifyAuthCode(String telephone, String authCode);
}
