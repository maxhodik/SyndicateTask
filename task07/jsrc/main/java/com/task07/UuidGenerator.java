package com.task07;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.google.gson.Gson;
import com.syndicate.deployment.annotations.environment.EnvironmentVariable;
import com.syndicate.deployment.annotations.environment.EnvironmentVariables;
import com.syndicate.deployment.annotations.events.RuleEventSource;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.model.RetentionSetting;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.*;

@LambdaHandler(
        lambdaName = "uuid_generator",
        roleName = "uuid_generator-role",
        isPublishVersion = true,
        aliasName = "learn",
        logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
@RuleEventSource(targetRule = "uuid_trigger")
//@EnvironmentVariables(value = {
//        @EnvironmentVariable(key = "target_bucket", value = "${target_bucket}"),
//        @EnvironmentVariable(key = "region", value = "${region}")
//})

public class UuidGenerator implements RequestHandler<Object, String> {
    //    private static final String BUCKET_NAME = System.getenv("target_bucket");
    private static final String BUCKET_NAME = "cmtr-529b17ca-uuid-storage";
    public static final String BUCKET_NAME_TEST = "cmtr-529b17ca-uuid-storage-test";
    private final AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();
    private Gson gson = new Gson();

    @Override
    public String handleRequest(Object event, Context context) {

        List<String> uuidList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            uuidList.add(UUID.randomUUID().toString());
        }
        String fileName = Instant.now().atOffset(ZoneOffset.UTC).toString();
        Map<String, Object> ids = new HashMap<>();
        ids.put("ids", uuidList);
        String jsonIds = gson.toJson(ids);

//        s3Client.putObject(BUCKET_NAME, fileName, jsonIds);
        s3Client.putObject(BUCKET_NAME_TEST, fileName, jsonIds);

        return "File created" + fileName;
    }
}
