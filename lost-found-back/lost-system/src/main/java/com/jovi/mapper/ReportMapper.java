package com.jovi.mapper;

import com.jovi.pojo.Message;
import com.jovi.pojo.Report;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ReportMapper {
    // 插入举报
    @Insert("INSERT INTO report (reporter_id, target_user_id, item_id, item_type, report_type, reason, status, create_time) " +
            "VALUES (#{reporterId}, #{targetUserId}, #{itemId}, #{itemType}, #{reportType}, #{reason}, 0, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Report report);

    // 获取举报列表（管理员用）
    @Select("SELECT * FROM report ORDER BY create_time DESC")
    List<Report> selectAll();

    // 按状态获取举报列表
    @Select("SELECT * FROM report WHERE status = #{status} ORDER BY create_time DESC")
    List<Report> selectByStatus(Integer status);

    // 获取举报详情
    @Select("SELECT * FROM report WHERE id = #{id}")
    Report selectById(Integer id);

    // 处理举报
    @Update("UPDATE report SET status = #{status}, handle_time = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Integer id, @Param("status") Integer status);

    // 检查是否已举报过（防止重复举报）
    @Select("SELECT COUNT(*) FROM report WHERE reporter_id = #{reporterId} " +
            "AND ((item_id = #{itemId} AND item_type = #{itemType}) " +
            "OR (target_user_id = #{targetUserId})) " +
            "AND status != 2")
    int checkDuplicate(Report report);

    @Select("SELECT COUNT(*) FROM report WHERE status = #{status}")
    int countByStatus(Integer status);
}