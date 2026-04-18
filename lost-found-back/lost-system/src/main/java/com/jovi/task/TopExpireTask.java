package com.jovi.task;

import com.jovi.mapper.LostItemMapper;
import com.jovi.mapper.FoundItemMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@EnableScheduling
public class TopExpireTask {

    @Autowired
    private LostItemMapper lostItemMapper;

    @Autowired
    private FoundItemMapper foundItemMapper;

    // 每小时执行一次
    @Scheduled(cron = "0 0 * * * ?")
    public void expireTopItems() {
        LocalDateTime now = LocalDateTime.now();

        // 失物置顶过期
        int lostExpired = lostItemMapper.expireTopItems();
        // 拾物置顶过期?
        //int foundExpired = foundItemMapper.expireTopItems();

        if (lostExpired > 0 ) {
            log.info("置顶过期：失物 {} 条", lostExpired);
        }
    }
}