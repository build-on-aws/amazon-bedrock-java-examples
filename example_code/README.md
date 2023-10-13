# Amazon Bedrock Java Examples 

To build and run these AWS SDK for Java (v2) code examples, you need the following:

* [Apache Maven](https://maven.apache.org/) (>3.0)
* [AWS SDK for Java](https://aws.amazon.com/sdk-for-java/) (downloaded and extracted somewhere on
  your machine)
* **All Java examples assume that you have set up your credentials in the credentials file in the .aws folder**. For information about how to set AWS credentials and the AWS Region, see [Set up AWS credentials and Region for development](http://docs.aws.amazon.com/sdk-for-java/v2/developer-guide/setup-credentials.html) in the *AWS SDK for Java Developer Guide*.

## Build and run the service examples

### Build the examples using  Apache Maven or Gradle

To run these examples, you can setup your development environment to use Apache Maven or Gradle to configure and build AWS SDK for Java projects. For more information, see "Get started with the AWS SDK for Java 2.x" located at https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html.

### Build the examples from the command line

To build any of the service examples, open a command-prompt (terminal) window and change to the directory containing the examples you want to build or run. Then type::

```bash
mvn package
```

### Run the service examples

**IMPORTANT**

The examples perform AWS operations for the account for which you've specified credentials, and you may incur AWS service charges by running them. See the [AWS Pricing](https://aws.amazon.com/pricing/) page for details about the charges you can expect for a given service and operation.

Some of these examples may perform *destructive* operations on AWS resources, such as deleting an Amazon S3 bucket or an Amazon DynamoDB table. **Be very careful** when running an operation that may delete or modify AWS resources in your account. It's best to create separate test-only resources when experimenting with these examples.

Because you built the JAR file that contains the dependencies, you can run an example using the following command. For example, you can run an S3 Java V2 example using this command:

```bash
java -cp target/S3J2Project-1.0-SNAPSHOT.jar com.example.s3.ListObjects mybucket
```