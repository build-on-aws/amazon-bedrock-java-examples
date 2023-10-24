package aws.community.examples;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

import org.json.JSONObject;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeAsyncClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelWithResponseStreamRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelWithResponseStreamResponseHandler;
import software.amazon.awssdk.services.bedrockruntime.model.ResponseStream;

public class InvokeBedrockStreamingAsync {
    public static void main(String[] args) {
        BedrockRuntimeAsyncClient runtime = BedrockRuntimeAsyncClient.builder()
                .region(Region.US_EAST_1)
                .build();

        String prompt = "Explain large language models to me.";

        JSONObject jsonBody = new JSONObject()
                .put("prompt", "Human: " + prompt + " Assistant:")
                .put("temperature", 0.8)
                .put("max_tokens_to_sample", 2048);

        SdkBytes body = SdkBytes.fromUtf8String(
                jsonBody.toString());

        InvokeModelWithResponseStreamRequest request = InvokeModelWithResponseStreamRequest.builder()
                .modelId("anthropic.claude-v2")
                .body(body)
                .build();

        InvokeModelWithResponseStreamResponseHandler.Visitor visitor = InvokeModelWithResponseStreamResponseHandler.Visitor
                .builder()
                .onChunk((chunk) -> {
                    JSONObject jsonObject = new JSONObject(
                            chunk.bytes().asString(StandardCharsets.UTF_8));

                    System.out.print(jsonObject.getString("completion"));

                })
                .onDefault((event) -> {
                    System.out.println("\n\nDefault: " + event.toString());
                })
                .build();

        InvokeModelWithResponseStreamResponseHandler responseHandler = InvokeModelWithResponseStreamResponseHandler
                .builder()
                .onComplete(
                        () -> System.out.println("\n\nCompleted streaming response."))
                .onError(
                        (error) -> System.out.println(
                                "\n\nError streaming response: " + error.getMessage()))
                .onEventStream((stream) -> {
                    // print the response stream as it comes in
                    stream.subscribe(
                            (ResponseStream e) -> {
                                e.accept(visitor);
                            });
                })
                .build();

        CompletableFuture<Void> futureResponse = runtime.invokeModelWithResponseStream(
                request,
                responseHandler);

        futureResponse.join();
    }
}
