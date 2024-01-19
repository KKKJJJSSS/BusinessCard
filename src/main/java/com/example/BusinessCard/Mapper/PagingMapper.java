package com.example.BusinessCard.Mapper;

import com.example.BusinessCard.Dto.PageDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PagingMapper {
    @Select("SELECT * FROM share_board")
    List<PageDto> getBoardList();
}