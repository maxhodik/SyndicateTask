package com.task04;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.model.RetentionSetting;

@LambdaHandler(
        lambdaName = "sqs_handler",
        roleName = "sqs_handler-role",
        isPublishVersion = true,
        aliasName = "learn",
        logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
public class SqsHandler implements RequestHandler<SQSEvent, String> {

    @Override
    public String handleRequest(SQSEvent sqsEvent, Context context) {
        sqsEvent.getRecords()
                .forEach(e -> context.getLogger().log("Message:" + e.getBody()));
        return "Messages processed successfully!";
    }
}
