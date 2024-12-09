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

public class PostTableHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static final String TABLE_NAME = "${tables_table}";
    private final AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
    private final DynamoDB dynamoDb = new DynamoDB(client);
    private final Table table = dynamoDb.getTable(TABLE_NAME);

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        JSONObject tablesData = new JSONObject(event.getBody());
        int tableId = Integer.parseInt(tablesData.get("id").toString());

        Item item = new Item()
                .withPrimaryKey("id", Integer.parseInt(tablesData.get("id").toString()))
                .withInt("number", Integer.parseInt(tablesData.get("number").toString()))
                .withInt("places", Integer.parseInt(tablesData.get("places").toString()))
                .withBoolean("isVip", tablesData.getBoolean("isVip"))
                .withInt("minOrder", Integer.parseInt(tablesData.get("minOrder").toString()));
        table.putItem(item);
        return new APIGatewayProxyResponseEvent()
                .withStatusCode(200)
                .withBody(new JSONObject().put("id", tableId).toString());
    }
}
