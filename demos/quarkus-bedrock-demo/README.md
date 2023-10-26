# Amazon Bedrock Demo using Quarkus

This is a sample project showing how to access Amazon Bedrock from a Quarkus application deployed on AWS Lambda as a fat jar app.

## Prerequisites

To deploy this demo you need:
- Access to an [AWS account](https://aws.amazon.com/free/)
- [Apache Maven](https://maven.apache.org/)
- [Quarkus](https://quarkus.io/)
- The [AWS SAM CLI](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/install-sam-cli.html)

## Build & Deploy

Build with:

```shell script
quarkus build --no-tests
```

Deploy it:

```shell script
sam deploy --guided
```

You can proceed with default settings for all options, except for `QuarkusBedrock has no authentication. Is this okay?`, which must be answered with `y` for the application to successfully be deployed.

```
Setting default arguments for 'sam deploy'
=========================================
Stack Name [quarkus-bedrock-demo]:
AWS Region [us-east-1]: 
#Shows you resources changes to be deployed and require a 'Y' to initiate deploy
Confirm changes before deploy [Y/n]:
#SAM needs permission to be able to create roles to connect to the resources in your template
Allow SAM CLI IAM role creation [Y/n]:
#Preserves the state of previously provisioned resources when an operation fails
Disable rollback [Y/n]:
QuarkusBedrock has no authentication. Is this okay? [y/N]: Y
Save arguments to configuration file [Y/n]: 
SAM configuration file [samconfig.toml]: 
SAM configuration environment [default]: 
```

After successful deployment, you will see the generated output:

```
CloudFormation outputs from deployed stack
------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------   
Outputs                                                                                                                                                                                                                                            
------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------   
Key                 QuarkusBedrock                                                                                                                                                                                                                 
Description         URL for application                                                                                                                                                                                                            
Value               https://SOMEURL.execute-api.us-east-1.amazonaws.com/                                                                                                                                                                        
----------------------------------------------------------------------------

Successfully created/updated stack - quarkus-bedrock-demo in us-east-1
```

Now you can access your REST interface to Amazon Bedrock:

```
https://SOMEURL.execute-api.us-east-1.amazonaws.com/bedrock/ask?question=Hello. Who are you?
```

And you can check the default model:

```
https://SOMEURL.execute-api.us-east-1.amazonaws.com/bedrock/llm_in_use
```

You can change the llm:

```
https://SOMEURL.execute-api.us-east-1.amazonaws.com/bedrock/llm?model=amazon.titan-tg1-large
```

## Environment Cleanup

To delete the SAM application, navigate to the `quarkus-bedrock` directory, execute the following command, and answer "y" to all prompts:

```bash
sam delete
```



