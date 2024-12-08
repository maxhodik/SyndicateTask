package com.task09.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

@Data
public class ForecastDto {

    private int elevation;
    private int generationtime_ms;
    private HourlyDto hourly;
    private HourlyUnitsDto hourly_units;
    private double latitude;
    private double longitude;
    private String timezone;
    private String timezone_abbreviation;
    private double utc_offset_seconds;

}
