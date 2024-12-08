package com.task09.dto;

import lombok.Data;

import java.util.List;

@Data
public class HourlyDto {
    private List<Double> temperature_2m;
    private List<String> time;
}
