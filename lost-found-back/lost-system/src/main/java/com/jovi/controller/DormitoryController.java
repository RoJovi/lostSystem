package com.jovi.controller;

import com.jovi.pojo.Location;
import com.jovi.pojo.Result;
import com.jovi.service.DormitoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dormitory")
public class DormitoryController {

    @Autowired
    private DormitoryService dormitoryService;

    //获取宿舍列表
    @GetMapping("/list")
    public Result getDormitoryList() {
        List<Location> dormitorylist = dormitoryService.list();
        return Result.success(dormitorylist);
    }


}
