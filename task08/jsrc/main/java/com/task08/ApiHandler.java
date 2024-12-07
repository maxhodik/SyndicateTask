package com.task08;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.annotations.lambda.LambdaLayer;
import com.syndicate.deployment.annotations.lambda.LambdaUrlConfig;
import com.syndicate.deployment.model.Architecture;
import com.syndicate.deployment.model.ArtifactExtension;
import com.syndicate.deployment.model.DeploymentRuntime;
import com.syndicate.deployment.model.RetentionSetting;
import com.syndicate.deployment.model.lambda.url.AuthType;
import com.syndicate.deployment.model.lambda.url.InvokeMode;

import java.io.IOException;
import java.util.Map;

@LambdaHandler(
        lambdaName = "api_handler",
        roleName = "api_handler-role",
        isPublishVersion = true,
        layers = {"meteo-weather"},
        runtime = DeploymentRuntime.JAVA17,
        aliasName = "learn",
        logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)

@LambdaLayer(
        layerName = "meteo-weather",
        libraries = {"layer/java/lib/task08-layer-1.0-SNAPSHOT.jar"},
        runtime = DeploymentRuntime.JAVA17,
        architectures = {Architecture.ARM64},
        artifactExtension = ArtifactExtension.ZIP
)
@LambdaUrlConfig(
        authType = AuthType.NONE,
        invokeMode = InvokeMode.BUFFERED
)
public class ApiHandler implements RequestHandler<Map<String, String>, String> {


    @Override
    public String handleRequest(Map<String, String> event, Context context) {
        String latitudeStr = event.get("latitude");
        String longitudeStr = event.get("longitude");
        latitudeStr = "50.4375";
        longitudeStr = "30.5";
        if (longitudeStr == null || latitudeStr == null) {
            return "error: latitude and longitude are required";
        }
        double latitude = Double.parseDouble(latitudeStr);
        double longitude = Double.parseDouble(longitudeStr);
        OpenMeteo meteo = new OpenMeteo();

        try {
            System.out.println("____________________________________________________________________");
            return meteo.getWeatherForecast(latitude, longitude);
        } catch (IOException e) {
            return "error:" + e.getMessage();
        }

    }
}