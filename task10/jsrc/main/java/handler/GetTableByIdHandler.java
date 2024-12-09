package handler;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.json.JSONObject;

import java.util.Map;

public class GetTableByIdHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static final String TABLE_NAME = "${tables_table}";
    private final AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
    private final DynamoDB dynamoDb = new DynamoDB(client);
    private final Table table = dynamoDb.getTable(TABLE_NAME);

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        Map<String, String> pathParams = event.getPathParameters();
        Item item = table.getItem("id", Integer.parseInt(pathParams.get("tableId")));
        return new APIGatewayProxyResponseEvent()
                .withStatusCode(200)
                .withBody(new JSONObject(item.toJSONPretty()).toString());
    }
}
