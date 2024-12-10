package com.task10.handler;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.json.JSONObject;

import java.util.NoSuchElementException;
import java.util.UUID;

public class PostReservationHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static final String RESERVATION_TABLE_NAME = System.getenv("reservations_table");
    private static final String TABLE_NAME = System.getenv("tables_table");

    private final AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
    private final DynamoDB dynamoDb = new DynamoDB(client);
    private final Table reservationTable = dynamoDb.getTable(RESERVATION_TABLE_NAME);
    private final Table table = dynamoDb.getTable(TABLE_NAME);

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        JSONObject tablesData = new JSONObject(event.getBody());
        int tableNumber = Integer.parseInt(tablesData.get("tableNumber").toString());
        try {
            validateTableNumber(tableNumber);
            checkTableAvailability(tableNumber);
            String reservationId = createReservation(tablesData, tableNumber);
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withBody(new JSONObject().put("reservationId", reservationId).toString());
        } catch (Exception e) {
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(400)
                    .withBody(new JSONObject().put("message", e.getMessage()).toString());
        }
    }

    private String createReservation(JSONObject tablesData, int tableNumber) {
        String reservationId = UUID.randomUUID().toString();
        Item item = new Item()
                .withPrimaryKey("id", reservationId)
                .withInt("tableNumber", tableNumber)
                .withString("clientName", tablesData.getString("clientName"))
                .withString("phoneNumber", tablesData.getString("phoneNumber"))
                .withString("date", tablesData.getString("date"))
                .withString("slotTimeStart", tablesData.getString("slotTimeStart"))
                .withString("slotTimeEnd", tablesData.getString("slotTimeEnd"));
        reservationTable.putItem(item);
        return reservationId;
    }

    private void checkTableAvailability(int tableNumber) {
        ScanRequest scanRequest = new ScanRequest()
                .withTableName(RESERVATION_TABLE_NAME);
        ScanResult result = client.scan(scanRequest);
        result.getItems().stream()
                .map(i -> Integer.parseInt(i.get("tableNumber").getN()))
                .filter(v -> v == tableNumber)
                .findAny()
                .ifPresent(i -> {
                    throw new IllegalArgumentException("The table has already reserved");
                });
    }

    private void validateTableNumber(int tableNumber) {
        ScanRequest scanRequest = new ScanRequest()
                .withTableName(TABLE_NAME);
        ScanResult result = client.scan(scanRequest);
        System.out.println("Tables result " + result);
        if (result == null) {
            throw new NoSuchElementException("Table not found");
        }
        result.getItems().stream()
                .map(item -> Integer.parseInt(item.get("number").getN()))
                .filter(v -> v == tableNumber).findFirst().orElseThrow();
    }

}

