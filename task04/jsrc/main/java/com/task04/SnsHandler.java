package com.task04;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.syndicate.deployment.annotations.events.SnsEventSource;
import com.syndicate.deployment.annotations.events.SnsEvents;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.model.RetentionSetting;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@LambdaHandler(
        lambdaName = "sns_handler",
        roleName = "sns_handler-role",
        isPublishVersion = true,
        aliasName = "learn",
        logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
@SnsEventSource(targetTopic = "lambda_topic")
@SnsEvents
public class SnsHandler implements RequestHandler<SNSEvent, Boolean> {

    @Override
    public Boolean handleRequest(SNSEvent snsEvent, Context context) {
        List<SNSEvent.SNSRecord> records = snsEvent.getRecords();
        if (!records.isEmpty()) {
            records.forEach(r -> context.getLogger().log(r.getSNS().getMessage()));
        }
        return Boolean.TRUE;
    }
}

