package org.acme;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;

import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;

import java.nio.charset.Charset;
import com.fasterxml.jackson.databind.ObjectMapper;

@Path("/bedrock")
public class LLMService {

    private static String llm = "amazon.titan-tg1-large";
    @GET
    @Path("/llm")
    public void getLlm(@QueryParam("model") String llmname) {
        llm = llmname;
    }
    @GET
    @Path("/llm_in_use")
    public String getLlm() {
        return llm;
    }
    @GET
    @Path("/ask")
    public String askBedrock(@QueryParam("question") String question) {
        final ObjectMapper objectMapper = new ObjectMapper();
        String response="";
        try (BedrockRuntimeClient bedrockClient = BedrockRuntimeClient.builder()
                .build()) {

            InvokeModelRequest invokeModelRequest = InvokeModelRequest.builder()
                    .body(SdkBytes.fromString("{\"inputText\" : \""+
                            question + "\"}", Charset.defaultCharset()))
                    .modelId(llm)
                    .build();

            InvokeModelResponse imResponse = bedrockClient.invokeModel(invokeModelRequest);
            BedrockResult bedrockResult = objectMapper.readValue(
                    imResponse.body().asUtf8String(), BedrockResult.class);
            response = bedrockResult.getResults()[0].getOutputText();
        }
        catch (Exception e) {
            return "Error" + e.getMessage();
        }
        return "Success: " + response;
    }



}
