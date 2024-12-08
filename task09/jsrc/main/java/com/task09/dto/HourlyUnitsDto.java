package com.task09.dto;

import com.amazonaws.services.dynamodbv2.xspec.S;
import lombok.Data;

@Data
public class HourlyUnitsDto {
    private String temperature;
    private String time;
}
