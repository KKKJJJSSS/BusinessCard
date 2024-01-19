package com.example.BusinessCard.Dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PageDto {
    private Long id;
    private String title;
    private String context;
    private LocalDateTime created_at;
}
