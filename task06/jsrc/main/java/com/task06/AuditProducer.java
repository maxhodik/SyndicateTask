package com.task06;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.xspec.M;
import com.amazonaws.services.dynamodbv2.xspec.S;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.lambda.runtime.events.models.dynamodb.AttributeValue;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.syndicate.deployment.annotations.events.DynamoDbTriggerEventSource;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.model.RetentionSetting;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@LambdaHandler(
        lambdaName = "audit_producer",
        roleName = "audit_producer-role",
        isPublishVersion = true,
        aliasName = "learn",
        logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
@DynamoDbTriggerEventSource(
        targetTable = "Configuration",
        batchSize = 1
)
public class AuditProducer implements RequestHandler<DynamodbEvent, String> {
    private static final String TABLE_NAME = "cmtr-529b17ca-Audit";
    private final AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
    private final DynamoDB dynamoDb = new DynamoDB(client);
    private final Table auditTable = dynamoDb.getTable(TABLE_NAME);


    @Override
    public String handleRequest(DynamodbEvent dynamodbEvent, Context context) {

        for (DynamodbEvent.DynamodbStreamRecord record : dynamodbEvent.getRecords()) {
            String eventName = record.getEventName();
            if (eventName.equals("INSERT")) {
                Map<String, AttributeValue> newImage = record.getDynamodb().getNewImage();
                insertNewDataInAuditTable(newImage);
                System.out.println("insert");
            } else if (eventName.equals("MODIFY")) {
                Map<String, AttributeValue> newImage = record.getDynamodb().getNewImage();
                Map<String, AttributeValue> oldImage = record.getDynamodb().getOldImage();
                modifyDataInAuditTable(newImage, oldImage);
                System.out.println("modify");
            }
        }
        return "";
    }

    private void modifyDataInAuditTable(Map<String, AttributeValue> newImage, Map<String, AttributeValue> oldImage) {
        String key = newImage.get("key").getS();
        int newValue = Integer.parseInt(newImage.get("value").getN());
        int oldValue = Integer.parseInt(oldImage.get("value").getN());
        if (newValue != oldValue) {
            Item item = new Item()
                    .withPrimaryKey("id", UUID.randomUUID().toString())
                    .withString("itemKey", key)
                    .withString("modificationTime", Instant.now().atOffset(ZoneOffset.UTC).toString())
                    .withString("updatedAttribute", "value")
                    .withInt("oldValue", oldValue)
                    .withInt("newValue", newValue);
            auditTable.putItem(item);
        }
    }

    private void insertNewDataInAuditTable(Map<String, AttributeValue> newImage) {
        String key = newImage.get("key").getS();
        int value = Integer.parseInt(newImage.get("value").getN());
        Map<String, Object> newValueMap = new HashMap<>();
        newValueMap.put("key", key);
        newValueMap.put("value", value);
        Item item = new Item()
                .withPrimaryKey("id", UUID.randomUUID().toString())
                .withString("itemKey", key)
                .withString("modificationTime", Instant.now().atOffset(ZoneOffset.UTC).toString())
                .withMap("newValue", newValueMap);
        auditTable.putItem(item);
    }
}

