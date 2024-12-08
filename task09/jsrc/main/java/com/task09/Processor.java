package com.task09;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.annotations.lambda.LambdaUrlConfig;
import com.syndicate.deployment.model.RetentionSetting;
import com.syndicate.deployment.model.lambda.url.AuthType;
import com.syndicate.deployment.model.lambda.url.InvokeMode;
import com.task09.dto.ForecastDto;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@LambdaHandler(
        lambdaName = "processor",
        roleName = "processor-role",
        isPublishVersion = true,
        aliasName = "Learn",
        logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
@LambdaUrlConfig(
        authType = AuthType.NONE,
        invokeMode = InvokeMode.BUFFERED
)

public class Processor implements RequestHandler<Object, Map<String, Object>> {
    private static final String TABLE_NAME = "cmtr-529b17ca-Weather-test";
    private final AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
    private final DynamoDB dynamoDb = new DynamoDB(client);
    private final Table weatherTable = dynamoDb.getTable(TABLE_NAME);
    private OpenMeteo openMeteo = new OpenMeteo();
    private Gson gson = new Gson();

    public Map<String, Object> handleRequest(Object request, Context context) {
        String weatherForecast;
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
            weatherForecast = openMeteo.getWeatherForecast();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ForecastDto forecastDto = gson.fromJson(weatherForecast, ForecastDto.class);
        insertNewDataInAuditTable(forecastDto);

        resultMap.put("statusCode", 200);
        resultMap.put("body", "Hello from Lambda");
        return resultMap;
    }

    private void insertNewDataInAuditTable(ForecastDto forecastDto) {

        Map<String, Object> forecast = new HashMap<>();
        Map<String, Object> hourlyMap = new HashMap<>();
        Map<String, Object> hourlyUnitsMap = new HashMap<>();

        hourlyMap.put("temperature_2m", forecastDto.getHourly().getTemperature());
        hourlyMap.put("time", forecastDto.getHourly().getTime());

        System.out.println(forecastDto);

        hourlyUnitsMap.put("temperature_2m", forecastDto.getHourlyUnits().getTemperature());
        hourlyMap.put("time", forecastDto.getHourlyUnits().getTime());

        forecast.put("elevation", forecastDto.getElevation());
        forecast.put("generationtime_ms", forecastDto.getGenerationTimeMs());
        forecast.put("latitude", forecastDto.getLatitude());
        forecast.put("longitude", forecastDto.getLongitude());
        forecast.put("timezone", forecastDto.getTimezone());
        forecast.put("timezone_abbreviation", forecastDto.getTimezoneAbbreviation());
        forecast.put("utc_offset_seconds", forecastDto.getUtcOffsetSeconds());

        forecast.put("hourly", hourlyMap);
        forecast.put("hourly_units", hourlyUnitsMap);


        Item item = new Item()
                .withPrimaryKey("id", UUID.randomUUID().toString())
                .withMap("forecast", forecast);
        weatherTable.putItem(item);
    }
}
