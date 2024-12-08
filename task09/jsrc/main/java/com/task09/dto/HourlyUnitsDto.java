package com.task09.dto;

import com.amazonaws.services.dynamodbv2.xspec.S;
import lombok.Data;

@Data
public class HourlyUnitsDto {
    private String temperature_2m;
    private String time;
}
