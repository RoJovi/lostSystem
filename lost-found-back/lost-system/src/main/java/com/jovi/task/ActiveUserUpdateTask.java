package com.jovi.task;

import com.jovi.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableScheduling
public class ActiveUserUpdateTask {

    @Autowired
    private UserMapper userMapper;

    // 每天凌晨 0:00 执行
    @Scheduled(cron = "0 0 0 * * ?")
    public void updateActiveUsers() {
        log.info("开始更新用户活跃状态...");
        int updated = userMapper.updateActiveStatus();
        log.info("用户活跃状态更新完成，共更新 {} 条记录", updated);
    }
}