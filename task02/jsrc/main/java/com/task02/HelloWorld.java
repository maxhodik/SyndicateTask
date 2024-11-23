package com.task02;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.annotations.lambda.LambdaUrlConfig;
import com.syndicate.deployment.model.RetentionSetting;
import com.syndicate.deployment.model.lambda.url.AuthType;
import com.syndicate.deployment.model.lambda.url.InvokeMode;

import java.util.HashMap;
import java.util.Map;

@LambdaHandler(
        lambdaName = "hello_world",
        roleName = "hello_world-role",
        isPublishVersion = true,
//        aliasName = "${lambdas_alias_name}",
        aliasName = "learn",
        logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
@LambdaUrlConfig(
        authType = AuthType.NONE,
        invokeMode = InvokeMode.BUFFERED
)
public class HelloWorld implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent event, Context context) {
        String method = getMethod(event);
        String path = getPath(event);
        if (method.equalsIgnoreCase("GET") && path.equals("/hello")) {

            return APIGatewayV2HTTPResponse.builder()
                    .withStatusCode(200)
                    .withHeaders(Map.of("Content-Type", "application/json"))
                    .withBody("{\"statusCode\": 200,\"message\": \"Hello from Lambda\"}")
                    .build();
        }
        String message = String.format("Bad request syntax or unsupported method. Request path: %s. HTTP method: %s",
                path, method);
        Map<String, Object> error =  Map.of("statusCode", 400,
                 "message", message);
        String errorMessage = null;
        try {
            errorMessage = objectMapper.writeValueAsString(error);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return APIGatewayV2HTTPResponse.builder()
                .withStatusCode(400)
                .withHeaders(Map.of("Content-Type", "application/json"))
                .withBody(errorMessage)
                .build();
    }

    private String getMethod(APIGatewayV2HTTPEvent requestEvent) {
        return requestEvent.getRequestContext().getHttp().getMethod();
    }

    private String getPath(APIGatewayV2HTTPEvent requestEvent) {
        return requestEvent.getRequestContext().getHttp().getPath();
    }
//	public Map<String, Object> handleRequest(Object request, Context context) {
//		System.out.println("Hello from lambda task 2");
//		Map<String, Object> resultMap = new HashMap<String, Object>();
//		resultMap.put("statusCode", 200);
//		resultMap.put("body", "Hello from Lambda");
//		return resultMap;
//	}
}
