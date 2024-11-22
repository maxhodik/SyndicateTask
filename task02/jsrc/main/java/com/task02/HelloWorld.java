package com.task02;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.model.RetentionSetting;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import java.util.HashMap;
import java.util.Map;

//@LambdaHandler(
//        lambdaName = "hello_world",
//        roleName = "hello_world-role",
//        isPublishVersion = true,
//        aliasName = "${lambdas_alias_name}",
//        logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
//)
public class HelloWorld implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
//
//    public Map<String, Object> handleRequest(Object request, Context context) {
//        System.out.println("Hello from lambda");
//        Map<String, Object> resultMap = new HashMap<String, Object>();
//        resultMap.put("statusCode", 200);
//        resultMap.put("body", "Hello from Lambda");
//        return resultMap;
//    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        String httpMethod = event.getHttpMethod();
        String path = event.getPath();
        if (httpMethod.equalsIgnoreCase("GET")&& path.equals("/hello")){
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withBody("{\"message\": \"Hello, world!\"}")
                    .withHeaders(Map.of("Content-Type", "application/json"));
        }
        String badRequestMessage= String.format("Bad request syntax or unsupported method. Request path: %s. HTTP method: %s", path, httpMethod);
        return new APIGatewayProxyResponseEvent()
                .withStatusCode(400)
                .withBody("{\"message\": badRequestMessage}")
                .withHeaders(Map.of("Content-Type", "application/json"));
    }
}
