package com.jovi.controller;

import com.jovi.pojo.ReportRequest;
import com.jovi.pojo.Result;
import com.jovi.pojo.SendMessageRequest;
import com.jovi.service.MessageService;
import com.jovi.service.ReportService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@Slf4j
@RestController
public class ReportController {

    @Autowired
    private ReportService reportService;

    /**
     * 举报帖子或用户
     */
    @PostMapping("/report")
    public Result report(@RequestBody ReportRequest request,
                         HttpServletRequest httpRequest) {
        Integer reporterId = (Integer) httpRequest.getAttribute("userId");
        if (reporterId == null) {
            return Result.error("未登录");
        }

        // 不能举报自己
        if (request.getTargetUserId() != null && reporterId.equals(request.getTargetUserId())) {
            return Result.error("不能举报自己");
        }

        log.info("用户 {} 举报，帖子ID: {}, 帖子类型: {}, 被举报用户: {}, 原因: {}",
                reporterId, request.getItemId(), request.getItemType(),
                request.getTargetUserId(), request.getReason());

        Integer reportId = reportService.addReport(reporterId, request);

        return Result.success(null);
    }

}