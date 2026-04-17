package com.jovi.mapper;

import com.jovi.pojo.LoginUserDTO;
import com.jovi.pojo.OldAndNewPassword;
import com.jovi.pojo.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

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

    @Insert("INSERT user(nickname , email, phone , password ,create_time , update_time) VALUES (#{nickname},#{email} , #{phone}, #{password} ,#{createTime} ,#{updateTime})")
    void insertNumAndPassword(User user);


    //检查邮箱
    @Select("SELECT COUNT(*) FROM user WHERE email = #{email} ")
    int SelectByEmail(String email);
    //检查手机号
    @Select("SELECT COUNT(*) FROM user WHERE phone = #{phone} ")
    int SelectByPhone(String phone);

    @Update("UPDATE user SET password = #{newPassword}, update_time = NOW() WHERE id = #{id} AND password = #{oldPassword} ")
    int updatePassword(OldAndNewPassword oldAndNewPassword);

    // 增加评论计数
    @Update("UPDATE user SET comment_count = comment_count + 1 WHERE id = #{userId}")
    void incrementCommentCount(Integer userId);

    @Select("SELECT * FROM user WHERE nickname LIKE CONCAT('%', #{keyword}, '%') OR username LIKE CONCAT('%', #{keyword}, '%')")
    List<User> selectByKeyword(String keyword);

    @Select("SELECT * FROM user ORDER BY create_time DESC")
    List<User> selectAll();

    @Update("UPDATE user SET status = #{status} WHERE id = #{userId}")
    void updateStatus(@Param("userId") Integer userId, @Param("status") Integer status);
    
    @Select("SELECT COUNT(*) FROM user")
    int countAll();

    @Select("SELECT COUNT(*) FROM user WHERE post_count + comment_count >= 5")
    int countActive();

    @Delete("DELETE FROM user WHERE id = #{id}")
    int deleteById(Integer id);

    @Update("UPDATE user SET post_count = post_count + 1 WHERE id = #{userId}")
    void incrementPostCount(Integer userId);

    @Update("UPDATE user SET post_count = post_count - 1 WHERE id = #{userId} AND post_count > 0")
    void decrementPostCount(Integer userId);
}
