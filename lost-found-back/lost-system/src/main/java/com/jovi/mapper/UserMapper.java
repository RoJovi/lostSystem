package com.jovi.mapper;

import com.jovi.pojo.LoginUserDTO;
import com.jovi.pojo.OldAndNewPassword;
import com.jovi.pojo.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper {
    //用手机号和密码换信息
    @Select("SELECT id, nickname, email,phone,avatar FROM user WHERE phone = #{account} AND password = #{password}")
    User selectByPhoneAndPassword(LoginUserDTO loginUserDTO) ;

    //用邮箱和密码换信息
    @Select("SELECT id, nickname, email,phone,avatar FROM user WHERE email = #{account} AND password = #{password}")
    User selectByEmailAndPassword(LoginUserDTO loginUserDTO) ;

    //用id换基本信息，查询回显
    @Select("SELECT * FROM user WHERE id = #{id} ")
    User selectById(Integer id);

    //改名，改邮箱，改电话号，改头像
    @Update("UPDATE user SET nickname = #{nickname}, email = #{email} ,phone = #{phone}, avatar = #{avatar}, update_time = #{updateTime} WHERE id = #{id}")
    int update(User user);

    @Insert("INSERT student(student_num , password ,create_time , update_time) VALUES (#{studentNum}, #{password} ,#{createTime} ,#{updateTime})")
    void insertNumAndPassword(User user);


    //检查邮箱
    @Select("SELECT COUNT(*) FROM user WHERE email = #{email} ")
    int SelectByEmail(String email);
    //检查手机号
    @Select("SELECT COUNT(*) FROM user WHERE phone = #{phone} ")
    int SelectByPhone(String phone);

    @Update("UPDATE user SET password = #{newPassword}, update_time = NOW() WHERE id = #{id} AND password = #{oldPassword} ")
    int updatePassword(OldAndNewPassword oldAndNewPassword);
}
