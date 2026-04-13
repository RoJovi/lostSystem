package com.jovi.service.impl;

import com.jovi.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class FileServiceImpl implements com.jovi.service.FileService {

  @Value("${file.upload.path:D:/images/}")
  private String uploadPath;

  @Value("${file.access.url:http://localhost:8080/uploads}")
  private String accessUrl;

  public String upload(MultipartFile file) {
    log.info("文件上传: 原始文件名={}, 大小={}KB", file.getOriginalFilename(), file.getSize() / 1024);
    try {

      // 1. 文件验证
      validateFile(file);

      // 2. 获取原始文件名和扩展名
      String originalFilename = file.getOriginalFilename();
      String extension = getFileExtension(originalFilename);

      // 3. 生成唯一文件名
      String newFileName = UUID.randomUUID().toString() + extension;

      // 4. 创建目标文件
      File targetFile = new File(uploadPath, newFileName);

      // 5. 确保目录存在
      if (!targetFile.getParentFile().exists()) {
        targetFile.getParentFile().mkdirs();
      }

      // 6. 保存文件
      file.transferTo(targetFile);

      // 7. 返回访问URL
      log.info("文件保存成功: 新文件名={}, 路径={}", newFileName, uploadPath + newFileName);
      return accessUrl + "/" + newFileName;

    } catch (IOException e) {
      log.error("文件上传失败: {}", e.getMessage(), e);
      throw new RuntimeException("文件上传失败: " + e.getMessage());
    }
  }

  /**
   * 获取文件扩展名
   */
  private String getFileExtension(String filename) {
    if (filename == null || filename.lastIndexOf(".") == -1) {
      return ""; // 没有扩展名
    }
    return filename.substring(filename.lastIndexOf("."));
  }

  /**
   * 验证文件
   */
  private void validateFile(MultipartFile file) {
    // 检查文件是否为空
    if (file.isEmpty()) {
      throw new RuntimeException("上传文件不能为空");
    }

    String originalFilename = file.getOriginalFilename();

    // 检查文件名是否合法
    if (originalFilename == null || originalFilename.trim().isEmpty()) {
      throw new RuntimeException("文件名不能为空");
    }

    // 检查文件大小（例如限制为10MB）
    long maxSize = 10 * 1024 * 1024;
    if (file.getSize() > maxSize) {
      throw new RuntimeException("文件大小不能超过10MB");
    }

    // 检查文件类型
    String extension = getFileExtension(originalFilename).toLowerCase();
    List<String> allowedTypes = Arrays.asList(".jpg", ".jpeg", ".png", ".gif", ".bmp");
    if (!allowedTypes.contains(extension)) {
      throw new RuntimeException("不支持的文件类型，仅支持: " + allowedTypes);
    }
  }
}
