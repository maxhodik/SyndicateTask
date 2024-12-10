package com.task11.handler;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GetReservationHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static final String TABLE_NAME = System.getenv("reservations_table");
    private final AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
    private final DynamoDB dynamoDb = new DynamoDB(client);
    private final Table table = dynamoDb.getTable(TABLE_NAME);

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        ScanRequest scanRequest = new ScanRequest()
                .withTableName(TABLE_NAME);
        List<Object> tempList = new ArrayList<>();
        ScanResult result = client.scan(scanRequest);
        for (Map<String, AttributeValue> item : result.getItems()) {
            Map<String, Object> tableMap = Map.of(
                    "tableNumber", Integer.parseInt(item.get("tableNumber").getN()),
                    "clientName", item.get("clientName").getS(),
                    "phoneNumber", item.get("phoneNumber").getS(),
                    "date", item.get("date").getS(),
                    "slotTimeStart", item.get("slotTimeStart").getS(),
                    "slotTimeEnd", item.get("slotTimeEnd").getS());
            tempList.add(tableMap);
        }
        return new APIGatewayProxyResponseEvent()
                .withStatusCode(200)
                .withBody(new JSONObject().put("reservations", tempList).toString());

    }
}
