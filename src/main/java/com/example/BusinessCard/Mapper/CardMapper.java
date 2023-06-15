package com.example.BusinessCard.Mapper;

import com.example.BusinessCard.Dto.CardDto;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CardMapper {
    @Insert("INSERT INTO business_card (username, name, department, position," +
            " company_name, email, phone_number, address, number," +
            " fax, memo, image) VALUES " +
            "(#{username}, #{name}, #{department}, #{position}, #{company_name}," +
            " #{email}, #{phone_number}, #{address}, #{number}, #{fax}, #{memo}," +
            " #{image})")
    void insertCard(CardDto card);

    @Select("SELECT * FROM business_card")
    List<CardDto> getCardList();

    @Delete("DELETE FROM business_card WHERE id = #{id}")
    Integer deleteCard(@Param("id") int id);

    @Update("UPDATE business_card SET name=#{name}, department=#{department}, position=#{position}," +
            "company_name=#{company_name}, email=#{email}, phone_number=#{phone_number}, address=#{address}," +
            "number=#{number}, fax=#{fax}, memo=#{memo}, image=#{image} WHERE id=#{id}")
    void updateCard(CardDto card);

    @Select("SELECT image FROM business_card WHERE id = #{id}")
    String getImagePathById(int id);

    @Select("SELECT * FROM business_card WHERE id = #{id}")
    CardDto getCardById(@Param("id") int id);

    @Insert("INSERT INTO business_card (phone_number, username, name, image, number, fax, department, position, address, email, company_name) VALUES " +
            "(#{cardDto.phone_number}, #{cardDto.username}, #{cardDto.name}, #{cardDto.image}, #{cardDto.number}, " +
            "#{cardDto.fax}, #{cardDto.department}, #{cardDto.position}, #{cardDto.address}, #{cardDto.email}, #{cardDto.company_name})")
    void autoUpload(@Param("cardDto") CardDto cardDto);
}
