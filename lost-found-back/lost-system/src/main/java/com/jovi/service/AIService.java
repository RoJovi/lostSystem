package com.jovi.service;

import com.jovi.pojo.*;

import java.util.List;

public interface AIService {
    String completeDescription(AICompleteRequest request);
    String analyzeStatistics(String prompt);  // 管理端统计分析
}
