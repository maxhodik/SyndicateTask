package com.task05;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.syndicate.deployment.annotations.events.DynamoDbTriggerEventSource;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.annotations.lambda.LambdaUrlConfig;
import com.syndicate.deployment.model.RetentionSetting;
import com.syndicate.deployment.model.lambda.url.AuthType;
import com.syndicate.deployment.model.lambda.url.InvokeMode;
import dto.RequestDto;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@LambdaHandler(
        lambdaName = "api_handler",
        roleName = "api_handler-role",
        isPublishVersion = true,
        aliasName = "learn",
        logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
@LambdaUrlConfig(
        authType = AuthType.NONE,
        invokeMode = InvokeMode.BUFFERED
)
@DynamoDbTriggerEventSource(targetTable = "Events", batchSize = 10)
public class ApiHandler implements RequestHandler<Object, Map<String, Object>> {
    private final DynamoDbClient dynamoDbClient;
    private final ObjectMapper objectMapper;
    private Gson gson;
    private static final String TABLE_NAME = "cmtr-529b17ca-Events";
    private RequestDto dto;
    static final DynamoDbClient standardClient = DynamoDbClient.builder()
            .region(Region.EU_CENTRAL_1)
            .build();

    // Use the configured standard client with the enhanced client.
//    static final DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
//            .dynamoDbClient(standardClient)
//            .build();
//    //static final DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.create();
//    static final DynamoDbTable<Event> customerTable = enhancedClient.table("cmtr-975a2528-Events-test", TableSchema.fromBean(Event.class));
    public ApiHandler() {

        this.dynamoDbClient = DynamoDbClient.builder()
                .region(Region.EU_CENTRAL_1)
                .build();
        ;
        this.objectMapper = new ObjectMapper();
        this.gson = new Gson();
    }


    @Override
    public Map<String, Object> handleRequest(Object request, Context context) {
        try {
            RequestDto requestDto = objectMapper.readValue(
                    new ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(request), RequestDto.class);
            Gson gson = new Gson();

//            if (requestDto==null || requestDto.getPrincipalId()==null){
//                HashMap<Object, Object> objectObjectHashMap = new HashMap<>();
//                objectObjectHashMap.put("event", event.getBody());
//                objectObjectHashMap.put("request", requestDto);
//                objectObjectHashMap.put("id", requestDto==null? null: requestDto.getPrincipalId());
//                Type typeObject = new TypeToken<HashMap>() {}.getType();
//
//                return  APIGatewayV2HTTPResponse.builder()
//                        .withStatusCode(200)
//                        .withBody(event.getBody()+ "resp "+ gson.toJson(map, typeObject)).build();
//            }
//            Map<String, String> eventData = objectMapper.readValue(event.getBody(), Map.class);
            String eventId = UUID.randomUUID().toString();
//            eventData.put("id", eventId);
            String createdAt = Instant.now().toString();
//            eventData.put("createdAt", createdAt);
            Map<String, AttributeValue> item = new HashMap<>();
            item.put("id", AttributeValue.builder().s(eventId).build());
//             item.put("id", AttributeValue.builder().s(eventData.get("id")).build());
            item.put("principalId", AttributeValue.builder().n(requestDto.getPrincipalId().toString()).build());
            item.put("createdAt", AttributeValue.builder().s(createdAt).build());
            item.put("body", AttributeValue.builder().s(objectMapper.writeValueAsString(requestDto.getContent())).build());

            PutItemRequest putItemRequest = PutItemRequest.builder()
                    .tableName(TABLE_NAME)
                    .item(item)
                    .build();
            dynamoDbClient.putItem(putItemRequest);
            Map<String, Object> resultMap = new HashMap<String, Object>();
            resultMap.put("statusCode", 201);
            resultMap.put("event", item);
            return resultMap;
        } catch (JsonProcessingException e) {
            context.getLogger().log("Error saving event: " + e.getMessage());
            return Map.of("message", e.getMessage());
        }
    }
}

