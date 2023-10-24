package aws.community.examples;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

import org.json.JSONObject;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeAsyncClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;

public class InvokeBedrockAsync {
    public static void main(String[] args) {
        BedrockRuntimeAsyncClient runtime = BedrockRuntimeAsyncClient.builder()
                .region(Region.US_EAST_1)
                .build();

        String prompt = "Hello Claude, how are you?";

        JSONObject jsonBody = new JSONObject()
                .put("prompt", "Human: " + prompt + " Assistant:")
                .put("temperature", 0.8)
                .put("max_tokens_to_sample", 1024);

        SdkBytes body = SdkBytes.fromUtf8String(
               jsonBody.toString()
        );

        InvokeModelRequest request = InvokeModelRequest.builder()
                .modelId("anthropic.claude-v2")
                .body(body)
                .build();

        CompletableFuture<InvokeModelResponse> futureResponse = runtime.invokeModel(request);

        JSONObject jsonObject = new JSONObject(
                futureResponse.join().body().asString(StandardCharsets.UTF_8)
        );

        String completion = jsonObject.getString("completion");

        System.out.println();
        System.out.println(completion);
        System.out.println();
    }
}
