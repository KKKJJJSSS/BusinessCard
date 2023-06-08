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
}
