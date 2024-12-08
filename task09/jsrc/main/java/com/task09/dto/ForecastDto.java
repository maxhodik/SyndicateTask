package com.task09.dto;

import lombok.Data;

@Data
public class ForecastDto {

    private int elevation;
    private int generationTimeMs;
    private HourlyDto hourly;
    private HourlyUnitsDto hourlyUnits;
    private double latitude;
    private double longitude;
    private String timezone;
    private String timezoneAbbreviation;
    private double utcOffsetSeconds;


}
