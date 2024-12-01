package com.task04;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.syndicate.deployment.annotations.events.SqsEvents;
import com.syndicate.deployment.annotations.events.SqsTriggerEventSource;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.model.RetentionSetting;

import java.util.HashMap;
import java.util.Map;

@LambdaHandler(
		lambdaName = "sqs_handler",
		roleName = "SqsHandler-role",
		isPublishVersion = true,
		aliasName = "learn",
		logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED

)
@SqsTriggerEventSource(targetQueue = "async_queue", batchSize = 10)
@SqsEvents
public class SqsHandler implements RequestHandler<SQSEvent, String> {

	@Override
	public String handleRequest(SQSEvent sqsEvent, Context context) {
		try {
			sqsEvent.getRecords()
					.forEach(e -> context.getLogger().log("Message:" + e.getBody()));
			return "Messages processed successfully!";
		} catch (Exception e) {
			context.getLogger().log("Error processing SQS messages: " + e.getMessage());
			return "Error processing SQS messages.";
		}
	}
}

