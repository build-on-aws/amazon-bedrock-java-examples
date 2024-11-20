package org.acme.config;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.auth.StsAssumeRoleCredentialsProvider;
import software.amazon.awssdk.services.sts.model.AssumeRoleRequest;

/**
 * @author Angel Conde
 *
 */
@Slf4j
@Configuration
public class    AWSCredentialsConfig {

    @Value("${aws.iam.role}")
    private String assumeRoleARN="";

    @Value("${aws.bedrock.region}")
    private String region;

    @Bean
    public AwsCredentialsProvider customPermissionProvider() {
        log.info("Assuming role "+ assumeRoleARN);
        StsClient sts=StsClient.builder()
                .region(Region.of(region))
                .build();
        StsAssumeRoleCredentialsProvider stsRoleCredentials=
                StsAssumeRoleCredentialsProvider.builder()
                        .stsClient(sts)
                        .refreshRequest(() -> AssumeRoleRequest
                        .builder()
                        .roleArn(assumeRoleARN)
                        .roleSessionName("bootful")
                        .build())

                .build();
    return stsRoleCredentials;
    }
}
