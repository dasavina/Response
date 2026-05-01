package edu.se.extweb.response;

import lombok.Data;

@Data
public class BaseMetaData {

    private boolean success;
    private int code;
    private String errorMessage;

}