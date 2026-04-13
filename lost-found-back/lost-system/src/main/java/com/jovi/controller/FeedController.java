package com.jovi.controller;

import com.jovi.pojo.Admin;
import com.jovi.pojo.Result;
import com.jovi.service.AdminService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@Slf4j
@RestController
@RequestMapping
public class FeedController {

    // 首页列表（临时返回空数组）
    @GetMapping("/feed/list")
    public Result getFeedList(
            @RequestParam(required = false, defaultValue = "all") String type,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "desc") String sort,
            @RequestParam(required = false) Integer locationId) {

        log.info("获取首页列表: type={}, keyword={}, sort={}, locationId={}", type, keyword, sort, locationId);

        // TODO: 实现真正的业务逻辑
        return Result.success(new ArrayList<>());
    }
}