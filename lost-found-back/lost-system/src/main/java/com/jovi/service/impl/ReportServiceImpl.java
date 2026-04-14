package com.jovi.service.impl;

import com.jovi.mapper.*;
import com.jovi.pojo.*;
import com.jovi.service.MessageService;
import com.jovi.service.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportMapper reportMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private LostItemMapper lostItemMapper;

    @Autowired
    private FoundItemMapper foundItemMapper;

    @Override
    @Transactional
    public Integer addReport(Integer reporterId, ReportRequest request) {
        Report report = new Report();
        report.setReporterId(reporterId);

        if (request.getItemId() != null) {
            // 举报帖子
            report.setItemId(request.getItemId());
            report.setItemType(request.getItemType());
            report.setReportType(0);
        } else if (request.getTargetUserId() != null) {
            // 举报用户
            report.setTargetUserId(request.getTargetUserId());
            report.setReportType(1);
        } else {
            throw new RuntimeException("请指定举报的帖子或用户");
        }

        report.setReason(request.getReason());
        reportMapper.insert(report);

        log.info("举报提交成功，举报ID: {}, 举报人: {}", report.getId(), reporterId);
        return report.getId();
    }

    @Override
    public List<ReportAdminVO> getAllReports(Integer status) {
        List<Report> reports;
        if (status == null) {
            reports = reportMapper.selectAll();
        } else {
            reports = reportMapper.selectByStatus(status);
        }

        List<ReportAdminVO> result = new ArrayList<>();
        for (Report report : reports) {
            ReportAdminVO vo = new ReportAdminVO();
            vo.setId(report.getId());
            vo.setItemId(report.getItemId());
            vo.setItemType(report.getItemType());
            vo.setReason(report.getReason());
            vo.setStatus(report.getStatus());
            vo.setCreateTime(report.getCreateTime());
            vo.setHandleTime(report.getHandleTime());

            // 获取举报人信息
            User reporter = userMapper.selectById(report.getReporterId());
            vo.setReporterName(reporter != null ? reporter.getNickname() : "未知用户");

            // 获取被举报的帖子标题
            if (report.getItemId() != null && report.getItemType() != null) {
                String title = null;
                if (report.getItemType() == 0) {
                    title = lostItemMapper.selectTitleById(report.getItemId());
                } else {
                    title = foundItemMapper.selectTitleById(report.getItemId());
                }
                vo.setPostTitle(title != null ? title : "帖子已删除");
            }

            result.add(vo);
        }
        return result;
    }

    @Override
    @Transactional
    public boolean handleReport(Integer reportId, String action) {
        Report report = reportMapper.selectById(reportId);
        if (report == null) {
            return false;
        }

        Integer status = "approve".equals(action) ? 1 : 2;
        reportMapper.updateStatus(reportId, status);

        // 如果通过举报，执行相应处理
        if ("approve".equals(action)) {
            if (report.getReportType() == 0 && report.getItemId() != null) {
                // 举报帖子：删除帖子
                if (report.getItemType() == 0) {
                    lostItemMapper.deleteById(report.getItemId());
                    log.info("通过举报，已删除失物帖子: {}", report.getItemId());
                } else {
                    foundItemMapper.deleteById(report.getItemId());
                    log.info("通过举报，已删除拾物帖子: {}", report.getItemId());
                }
            } else if (report.getReportType() == 1 && report.getTargetUserId() != null) {
                // 举报用户：封禁用户
                userMapper.updateStatus(report.getTargetUserId(), 0);
                log.info("通过举报，已封禁用户: {}", report.getTargetUserId());
            }
        }

        log.info("举报处理完成，举报ID: {}, 处理结果: {}", reportId, action);
        return true;
    }

}