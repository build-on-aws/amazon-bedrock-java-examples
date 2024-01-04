 package org.acme.service;

/*
 *        Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 *        Permission is hereby granted, free of charge, to any person obtaining a copy of this
 *        software and associated documentation files (the "Software"), to deal in the Software
 *        without restriction, including without limitation the rights to use, copy, modify,
 *        merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 *        permit persons to whom the Software is furnished to do so.
 *
 *        THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 *        INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 *        PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 *        HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 *        OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 *        SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import lombok.extern.slf4j.Slf4j;
import org.acme.model.BedrockAPI;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This service uses Amazon Bedrock Libraries from AWS Java SDK to interact with it.
 *
 * @author Angel Conde
 */
@Service
@Slf4j
public class BedrockAPIService {

    private static String llm = "anthropic.claude-v2";

    private final BedrockRuntimeClient bedrockClient;

    @Autowired
    public BedrockAPIService(final BedrockRuntimeClient bedrockClient) {
        super();
        this.bedrockClient = bedrockClient;
    }


    public String createProductIndex(BedrockAPI product) {
        return null;
    }

    public void findByLastName(final String lastName) {

    }

    public String queryBedrock(String prompt, float temperature) {
        JSONObject jsonBody = new JSONObject()
                .put("prompt", "Human: " + prompt + " Assistant:")
                .put("temperature", temperature)
                .put("max_tokens_to_sample", 1024);

        SdkBytes body = SdkBytes.fromUtf8String(
                jsonBody.toString()
        );

        InvokeModelRequest request = InvokeModelRequest.builder()
                .modelId(llm)
                .body(body)
                .build();

        InvokeModelResponse invocationResponse = bedrockClient.invokeModel(request);

        return invocationResponse.body().asUtf8String();

    }
}