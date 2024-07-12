# Amazon Bedrock Guardrail Demo

This repository contains a Java-based demonstration of how to configure and apply guardrails to interactions with Large Language Models (LLMs) on Amazon Bedrock. The demo showcases the power and flexibility of guardrails in ensuring safe and controlled AI interactions.

## Table of Contents

- [Overview](#overview)
- [Key Features](#key-features)
- [Prerequisites](#prerequisites)
- [Setup](#setup)
- [Running the Demo](#running-the-demo)
- [Customization](#customization)
- [License](#license)

## Overview

This demo illustrates how to create and apply guardrails to LLM interactions using Amazon Bedrock. Guardrails are essential for implementing safeguards in generative AI applications, ensuring consistent user experiences and standardized safety controls across multiple foundation models.

## Key Features

The demo showcases several key benefits of using guardrails:

1. **Content Filtering**: Block harmful user inputs and toxic model responses.
2. **Topic Control**: Prevent discussions on specified undesirable topics.
3. **PII Protection**: Safeguard personally identifiable information and other sensitive data.
4. **Customization**: Apply tailored filters for specific use cases, including custom word lists and regex patterns.

## Prerequisites

To run this demo, you'll need:

- Java Development Kit (JDK) 11 or later
- Apache Maven
- AWS account with access to Amazon Bedrock
- AWS CLI configured with appropriate credentials

## Setup

1. Clone this repository:
   ```
   git clone https://github.com/build-on-aws/amazon-bedrock-java-examples.git
   cd demos/guardrails
   ```

2. Install dependencies:
   ```
   mvn clean install
   ```

3. Ensure your AWS credentials are properly configured in your environment or AWS credentials file.

## Running the Demo

To run the demo, execute the following command from the module's root directory:

```
mvn exec:java -Dexec.mainClass="aws.community.examples.GuardrailDemo"
```

The demo will:
1. Configure various guardrail filters
2. Create a guardrail with these configurations
3. Demonstrate the effect of the guardrail by sending a sample message to the LLM both with and without the guardrail applied

## Customization

You can customize the demo by modifying the filter configurations in the `configure*Filter()` methods. Some areas you might want to adjust:

- Content filter strengths
- PII entity types and actions
- Denied topics and their definitions
- Custom word lists
- Regex patterns for custom filters

## License

This library is licensed under the MIT-0 License. See the [LICENSE](LICENSE) file.

---

For more information on Amazon Bedrock and its capabilities, please refer to the [Amazon Bedrock User Guide](https://docs.aws.amazon.com/bedrock/?trk=2483aad2-15a6-4b7a-a1c5-189851586b67&sc_channel=el).