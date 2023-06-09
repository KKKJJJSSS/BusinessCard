package com.example.BusinessCard.Mapper;

import com.example.BusinessCard.Dto.CardDto;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CardMapper {
    @Insert("INSERT INTO business_card (name, department, position, company_name, email, phone_number, address, number, fax, memo, username) " +
            "VALUES (#{name}, #{department}, #{position}, #{company_name}, #{email}, #{phone_number}, #{address}, #{number}, #{fax}, #{memo}, #{username})")
    void insertCard(CardDto card);

    @Select("SELECT * FROM business_card")
    List<CardDto> getCardList();

    @Delete("DELETE FROM business_card WHERE id = #{id}")
    Integer deleteCard(@Param("id") int id);

    @Update("UPDATE business_card SET name=#{name}, department=#{department}, position=#{position}," +
            "company_name=#{company_name}, email=#{email}, phone_number=#{phone_number}, address=#{address}," +
            "number=#{number}, fax=#{fax}, memo=#{memo} WHERE id=#{id}")
    void updateCard(CardDto card);
}
