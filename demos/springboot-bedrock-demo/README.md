# Amazon Bedrock Demo using SpringBoot

This is a sample project, that demonstrates how to access Amazon Bedrock from a SpringBoot application that could deployed on AWS Fargate or on AWS Lambda (via native compilation step for avoid those cold start times).

## Prerequisites

To deploy this demo you need:
- Access to an [AWS account](https://aws.amazon.com/free/)
- [Apache Maven](https://maven.apache.org/)

This demo sent rest calls have signed with AWS credentials that will be mapped to a IAMRole with permissions to access Amazon Bedrock.

This sample code does not secure the Rest API in any way.

In a production environment you could also employ Amazon Cognito or other mechanisms to secure the API and dynamically attach the role to the API depending the logged user following best practices. The project could be integrated with Spring Security in order to gain such features.

## Build the application
To build the application, execute the following command in the `springboot-bedrock-demo` directory:

```bash
mvn package
```

## Access the Application

*Note: Replace `URL_PREFIX.execute-api.us-east-1.amazonaws.com` with the actual URL that has been returned during the deployment.*

### Send a prompt

To send a prompt to the currently active model, use the following endpoint:

```
https://URL_PREFIX.execute-api.us-east-1.amazonaws.com/bedrock?prompt=YOUR_PROMPT
```

Example:

```
https://URL_PREFIX.execute-api.us-east-1.amazonaws.com/bedrock?promp=Hello, how are you?
```

### Check active model

To check which model is currently active, use the following endpoint:

```
https://URL_PREFIX.execute-api.us-east-1.amazonaws.com/llm
```

### Change active model

To change the active model, use the following endpoint:

```
https://URL_PREFIX.execute-api.us-east-1.amazonaws.com/llm/set?model=NEW_MODEL
```

Example:

```
https://URL_PREFIX.execute-api.us-east-1.amazonaws.com/llm/set?model=anthropic.claude-instant-v1
```


The following models are currently available as part of this app:
- `anthropic.claude-v1`
- `anthropic.claude-instant-v1`
- `anthropic.claude-v2`

Provide the following configuration in the application.yml file

aws.bedrock.region=aws-region-you-are-using
aws.iamrole=iam-master-role-ARN

This project uses an IAM Role. The project is using STS to get credentials for that Role, remember to adapt it for your needs. In the blogpost, the app is launched via Fargate and the Task Role is used for the permission chain.


You can use Postman to interact with the API. The API docs can be seen at http://localhost:8080/swagger-ui/index.html

Live demo with URL: http://localhost:8080/bedrockui
Start to input some characters in the search box, which will open an auto-complete box of maximum 5 suggestions.
Complete the search text and click search button to see the search results.




