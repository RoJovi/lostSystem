package com.jovi.controller;

import com.jovi.pojo.AICompleteRequest;
import com.jovi.pojo.Result;
import com.jovi.service.AIService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/ai")
public class AIController {

    @Autowired
    private AIService aiService;

    /**
     * AI补全描述
     */
    @PostMapping("/complete")
    public Result complete(@RequestBody AICompleteRequest request,
                           HttpServletRequest httpRequest) {
        Integer userId = (Integer) httpRequest.getAttribute("userId");
        if (userId == null) {
            return Result.error("未登录");
        }

        log.info("用户 {} 请求AI补全: type={}, title={}", userId, request.getType(), request.getTitle());

        try {
            String description = aiService.completeDescription(request);
            return Result.success(description);
        } catch (Exception e) {
            log.error("AI补全失败: {}", e.getMessage());
            return Result.error("AI服务繁忙，请稍后重试");
        }
    }
}