package com.jovi.mapper;

import com.jovi.pojo.Admin;
import org.apache.ibatis.annotations.*;

@Mapper
public interface AdminMapper {
    // 用工号和密码查询管理员信息
    @Select("SELECT id, admin_num, name FROM admin WHERE admin_num = #{adminNum} AND password = #{password}")
    Admin selectByAdminNumAndPassword(Admin admin);
    // 用id查询管理员信息
    @Select("SELECT * FROM admin WHERE id = #{id}")
    Admin selectById(Integer id);

    @Update("UPDATE admin SET name = #{name}, phone = #{phone}, update_time = #{updateTime} WHERE id = #{id}")
    void update(Admin admin);

    @Insert("INSERT admin(admin_num , password ,create_time , update_time) VALUES (#{adminNum}, #{password} ,#{createTime} ,#{updateTime})")
    void insertNumAndPassword(Admin admin);

    @Select("SELECT COUNT(*) FROM admin WHERE admin_num = #{adminNum} ")
    int SelectByAdminNum(String adminNum);

    // 根据ID和密码查询（验证原密码）
    @Select("SELECT * FROM admin WHERE id = #{id} AND password = #{password}")
    Admin selectByIdAndPassword(@Param("id") Integer id, @Param("password") String password);

    // 更新密码
    @Update("UPDATE admin SET password = #{newPassword} WHERE id = #{id}")
    void updatePassword(@Param("id") Integer id, @Param("newPassword") String newPassword);
}
