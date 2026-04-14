package com.jovi.controller;

import com.jovi.pojo.Admin;
import com.jovi.pojo.FeedPostVO;
import com.jovi.pojo.Result;
import com.jovi.service.AdminService;
import com.jovi.service.FeedService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@RestController
public class FeedController {

    @Autowired
    private FeedService feedService;

    @GetMapping("/feed/list")
    public Result getFeedList(
            @RequestParam(required = false, defaultValue = "all") String type,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "desc") String sort,
            @RequestParam(required = false) Integer locationId) {

        log.info("获取首页列表: type={}, keyword={}, sort={}, locationId={}", type, keyword, sort, locationId);

        List<FeedPostVO> list = feedService.getFeedList(type, keyword, sort, locationId);
        return Result.success(list);
    }
}