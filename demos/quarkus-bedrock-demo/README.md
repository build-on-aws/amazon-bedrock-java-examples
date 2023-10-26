# Amazon Bedrock Demo using Quarkus

This is a sample project showing how to access Amazon Bedrock from a Quarkus application deployed on AWS Lambda as a fat jar app.

## Prerequisites

To deploy this demo you need:
- Access to an [AWS account](https://aws.amazon.com/free/)
- [Apache Maven](https://maven.apache.org/)
- [Quarkus](https://quarkus.io/)
- The [AWS SAM CLI](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/install-sam-cli.html)


## Build the application
To build the application, execute the following command in the `quarkus-bedrock-demo` directory:

```bash
quarkus build
```

## Deploy the application
To deploy the application, execute the following command in the `quarkus-bedrock-demo` directory:

```bash
sam deploy --guided
```

**Note:** You can accept all default settings, except the following two:

- **AWS Region** must be set a region that supports Bedrock, e.g. **us-east-1**. 
- **QuarkusBedrock has no authentication. Is this okay?** must be answered with `[Y]`.

```
Setting default arguments for 'sam deploy'
=========================================
...
AWS Region []: us-east-1
...
QuarkusBedrock has no authentication. Is this okay? [y/N]: Y
...
```

After successful deployment, you will see the generated output:

```
CloudFormation outputs from deployed stack
------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------   
Outputs                                                                                                                                                                                                                                            
------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------   
Key                 QuarkusBedrock                                                                                                                                                                                                                 
Description         URL for application                                                                                                                                                                                                            
Value               https://URL_PREFIX.execute-api.us-east-1.amazonaws.com/                                                                                                                                                                        
----------------------------------------------------------------------------

Successfully created/updated stack - quarkus-bedrock-demo in us-east-1
```

## Access the Application

*Note: Replace `URL_PREFIX.execute-api.us-east-1.amazonaws.com` with the actual URL that has been returned during the deployment.*

### Send a prompt

To send a prompt to the currently active model, use the following endpoint:

```
https://URL_PREFIX.execute-api.us-east-1.amazonaws.com/bedrock/prompt?p=YOUR_PROMPT
```

Example:

```
https://URL_PREFIX.execute-api.us-east-1.amazonaws.com/bedrock/prompt?p=Hello, how are you?
```

### Check active model

To check which model is currently active, use the following endpoint:

```
https://URL_PREFIX.execute-api.us-east-1.amazonaws.com/bedrock/llm
```

### Change active model

To change the active model, use the following endpoint:

```
https://URL_PREFIX.execute-api.us-east-1.amazonaws.com/bedrock/llm/set?model=NEW_MODEL
```

Example:

```
https://URL_PREFIX.execute-api.us-east-1.amazonaws.com/bedrock/llm/set?model=anthropic.claude-instant-v1
```


The following models are currently available as part of this app:
- `anthropic.claude-v1`
- `anthropic.claude-instant-v1`
- `anthropic.claude-v2`

## Environment Cleanup

To delete the SAM application, navigate to the `quarkus-bedrock` directory, execute the following command, and answer "y" to all prompts:

```bash
sam delete
```



