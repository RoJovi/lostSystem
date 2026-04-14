package com.jovi.service;

import com.jovi.pojo.MessageVO;
import com.jovi.pojo.ReportAdminVO;
import com.jovi.pojo.ReportRequest;

import java.util.List;

public interface ReportService {
    Integer addReport(Integer reporterId, ReportRequest request);

    // 管理员：获取所有举报列表
    List<ReportAdminVO> getAllReports(Integer status);

    // 管理员：处理举报
    boolean handleReport(Integer reportId, String action);
}
