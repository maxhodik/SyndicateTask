package com.task10.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RouteKey {
    private String method;
    private String path;


}
