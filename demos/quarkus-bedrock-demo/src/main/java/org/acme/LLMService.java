package org.acme;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;

@Path("/bedrock")
public class LLMService {

    private static String llm = "anthropic.claude-v2";

    @GET
    @Path("/llm")
    public String getLlm() {
        return "Current Model: " + llm;
    }

    @GET
    @Path("/llm/set")
    public String setLlm(@QueryParam("model") String new_llm) {
        llm = new_llm;
        return "New model: " + llm;
    }

    @GET
    @Path("/prompt")
    public String invokeModel(@QueryParam("p") String prompt) {
        String response="";

        try (BedrockRuntimeClient runtime = BedrockRuntimeClient.builder().build()) {

            JSONObject jsonBody = new JSONObject()
                    .put("prompt", "Human: " + prompt + " Assistant:")
                    .put("temperature", 0.8)
                    .put("max_tokens_to_sample", 1024);

            SdkBytes body = SdkBytes.fromUtf8String(
                    jsonBody.toString()
            );

            InvokeModelRequest request = InvokeModelRequest.builder()
                    .modelId(llm)
                    .body(body)
                    .build();

            InvokeModelResponse invocationResponse = runtime.invokeModel(request);

            JSONObject jsonObject = new JSONObject(
                    invocationResponse.body().asString(StandardCharsets.UTF_8)
            );

            String completion = jsonObject.getString("completion");

            response = "Model: " + llm + "\n\n";
            response += "Prompt: " + prompt + "\n\n";
            response += "Response: " + completion;

        } catch (Exception e) {
            return "Error" + e.getMessage();
        }
        return response;
    }

}
