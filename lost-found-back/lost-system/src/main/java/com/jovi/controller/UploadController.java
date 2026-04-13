package com.jovi.controller;

import com.jovi.pojo.Result;
import com.jovi.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    @Autowired
    private FileService fileService;

    @PostMapping
    public Result uploadFile(@RequestParam("file") MultipartFile file) {
        String url = fileService.upload(file);
        return Result.success(url);
    }
}