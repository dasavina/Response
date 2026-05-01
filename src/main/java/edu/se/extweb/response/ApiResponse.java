package edu.se.extweb.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ApiResponse<M, T> {

    private M meta;
    private List<T> data;

}