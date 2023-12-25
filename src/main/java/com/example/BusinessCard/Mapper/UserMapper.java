package com.example.BusinessCard.Mapper;

import com.example.BusinessCard.Dto.UserDto;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    @Insert("INSERT INTO users (username, password, email, number) VALUES (#{username}, #{password}, #{email}, #{number})")
    void insert(UserDto user);

    @Select("SELECT * FROM users WHERE username = #{username}")
    UserDto findByUsername(String username);

    @Select("SELECT COUNT(*) FROM users WHERE username = #{username}")
    int countByUsername(String username);

    @Select("SELECT COUNT(*) FROM users WHERE email = #{email}")
    int countByEmail(String email);

    @Select("SELECT COUNT(*) FROM users WHERE number = #{number}")
    int countByNumber(String number);

    @Select("SELECT * FROM users WHERE number = #{number}")
    UserDto findByNumber(UserDto userDto);

}