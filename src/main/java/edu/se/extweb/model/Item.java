package edu.se.extweb.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Item {

    private String id;
    private String name;
    private String code;
    private String description;
    private LocalDateTime createdAt;

}