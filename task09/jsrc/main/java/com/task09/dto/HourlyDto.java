package com.task09.dto;

import lombok.Data;

import java.util.List;

@Data
public class HourlyDto {
    private List<Double> temperature;
    private List<String> time;
}
