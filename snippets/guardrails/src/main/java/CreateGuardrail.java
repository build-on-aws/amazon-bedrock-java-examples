// SPDX-License-Identifier: MIT-0

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrock.BedrockClient;
import software.amazon.awssdk.services.bedrock.model.*;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.ContentBlock;
import software.amazon.awssdk.services.bedrockruntime.model.ConversationRole;
import software.amazon.awssdk.services.bedrockruntime.model.ConverseResponse;

import java.util.List;
import java.util.function.Consumer;

import static software.amazon.awssdk.services.bedrock.model.GuardrailContentFilterType.*;
import static software.amazon.awssdk.services.bedrock.model.GuardrailFilterStrength.HIGH;
import static software.amazon.awssdk.services.bedrock.model.GuardrailFilterStrength.NONE;
import static software.amazon.awssdk.services.bedrock.model.GuardrailPiiEntityType.*;
import static software.amazon.awssdk.services.bedrock.model.GuardrailSensitiveInformationAction.*;

/**
 * This example demonstrates the creation and usage of guardrails with Amazon Bedrock.
 * Guardrails enable the implementation of safeguards for generative AI applications
 * based on specific use cases and responsible AI policies.
 *
 * Guardrails can be applied across multiple foundation models (FM) to provide:
 * - Consistent user experience
 * - Standardized safety and privacy controls
 *
 * Key features of guardrails include:
 * 1. Content filters: Block harmful user inputs and toxic model responses
 * 2. Denied topics: Prevent discussions on specified undesirable topics
 * 3. Word filters: Block specific words, phrases, and profanity
 * 4. Sensitive information filters: Protect PII and other sensitive data
 * 5. Contextual grounding check: Detect and filter hallucinations in model responses
 *
 * Use cases for guardrails include:
 * - Chatbot applications: Filter harmful content
 * - Banking applications: Block investment advice-related queries
 * - Call center applications: Redact PII from conversation summaries
 *
 * This example demonstrates how to:
 * 1. Create a guardrail configuration with various safety measures
 * 2. Apply the guardrail to a model interaction
 * 3. Send a user message to the model and receive a response
 *
 * Note: Ensure proper AWS credentials and permissions are set up before running this code.
 */
public class CreateGuardrail {
    public static final Region AWS_REGION = Region.US_EAST_1;

    public static void main(String[] args) {

        CreateGuardrailResponse guardrail = createGuardrail();

        // This can be almost every model on Amazon Bedrock, simply swap out the ID
        String modelId = "anthropic.claude-3-haiku-20240307-v1:0";

        // The text you want to send to the model
        String userInput = "[Your message to the model]";

        // Send the input along with the guardrail definition to the model
        String responseText = sendToModel(userInput, guardrail, modelId);

        // Print the model's response
        System.out.println(responseText);
    }

    /**
     * Creates an Amazon Bedrock Guardrail configuration with various safety measures.
     *
     * @return The created guardrail configuration
     */
    private static CreateGuardrailResponse createGuardrail() {
        // The Bedrock client is used to create and manage resources
        BedrockClient bedrock = BedrockClient.builder().region(AWS_REGION).build();

        // Create the guardrail to implement safeguards for your generative AI applications.
        CreateGuardrailResponse guardrail = bedrock.createGuardrail(request -> request
                // A unique name for this guardrail configuration
                .name("A_Guardrail")

                // The message to be returned when the input has been blocked
                .blockedInputMessaging("Input not accepted. Please try rephrasing your request.")

                // The message to be returned when the model's response has been blocked
                .blockedOutputsMessaging("Response unavailable. Please try a different query.")

                .topicPolicyConfig(topicPolicy -> topicPolicy
                        // Topics that should be denied
                        .topicsConfig(ConfigureDeniedTopics()))

                .contentPolicyConfig(contentPolicy -> contentPolicy
                        // Content filters for hate speech, sexual content, etc.
                        .filtersConfig(configureContentFilter()))

                .sensitiveInformationPolicyConfig(pii -> pii
                        // Filters for PII and other commonly used sensitive information
                        .piiEntitiesConfig(configureSensitiveInformationFilter())
                        // Custom filters for additional sensitive information types
                        .regexesConfig(configureRegexFilter()))

                .wordPolicyConfig(wordPolicy -> wordPolicy
                        // Filters for profanity, based on managed and regularly updated lists
                        .managedWordListsConfig(configureProfanityFilter())
                        // Filters for custom words and terms, such as a competitor's products, etc.
                        .wordsConfig(configureCustomWordFilter()))
        );
        return guardrail;
    }

    /**
     * Sends a message to the specified model using the provided guardrail.
     *
     * @param userInput The user's input message
     * @param guardrail The guardrail configuration
     * @param modelId The ID of the model to use
     * @return The model's response text
     */
    private static String sendToModel(String userInput, CreateGuardrailResponse guardrail, String modelId) {

        // The Bedrock Runtime client is used to send messages to generative AI models
        BedrockRuntimeClient runtime = BedrockRuntimeClient.builder().region(AWS_REGION).build();

        // Send a message, along with the guardrail config to the model of your choice
        ConverseResponse response = runtime.converse(request -> request
                // Add the ID of the model you want to use
                .modelId(modelId)
                // Define which guardrail ID/version you want to use in this conversation
                .guardrailConfig(cfg -> cfg
                        .guardrailIdentifier(guardrail.guardrailId())
                        .guardrailVersion(guardrail.version())
                )
                // Add the message(s) you want to send
                .messages(msg -> msg
                        .role(ConversationRole.USER)
                        .content(ContentBlock.fromText(userInput)))
        );

        // Extract and return the generated text from the model's response.
        return response.output().message().content().get(0).text();
    }

    /**
     * Some topics may be undesirable in the context of your generative AI application. For example, a bank may want
     * their AI assistant to avoid any conversation related to investment advice or engage in conversations related
     * to cryptocurrencies.
     *
     * Denied topics can be defined by providing a natural language definition of the topic along with a few
     * optional example phrases of the topic. The definition and example phrases are used to detect if an input
     * prompt or a model completion belongs to the topic.
     */
    private static Consumer<GuardrailTopicConfig.Builder> ConfigureDeniedTopics() {
        return topics -> topics
                .name("Investment Advice")
                .definition(
                        "Investment advice means inquiries, guidance or recommendations regarding the management " +
                        "or allocation of funds or assets with the goal of generating returns or achieving specific " +
                        "financial objectives.")
                .examples(List.of(
                        "Is investing in the stocks better than bonds?",
                        "Should I invest in gold"))
                .type(GuardrailTopicType.DENY)

                .name("Cryptocurrencies")
                .definition(
                        "Cryptocurrencies refer to digital or virtual currencies that use cryptography for security, " +
                        "operate independently of a central bank, and can be used for online transactions.")
                .examples(List.of(
                        "What's the best cryptocurrency to invest in right now?",
                        "How do I mine Bitcoin?",
                        "Should I buy Ethereum or Dogecoin?"))
                .type(GuardrailTopicType.DENY);
    }

    /**
     * Predefined content filters help detect and filter harmful user inputs and FM-generated outputs.
     *
     * Filtering is done based on confidence classification of user inputs and FM responses across each of six
     * categories: HATE, SEXUAL, INSULTS, VIOLENCE, MISCONDUCT, PROMPT_ATTACK
     *
     * All user inputs and FM responses are classified across four strength levels - NONE, LOW, MEDIUM,
     * and HIGH. For example, if a statement is classified as Hate with HIGH confidence, the likelihood of that
     * statement representing hateful content is high.
     *
     * You can configure the strength of the filters for each of the content Filter categories. The filter strength
     * determines the sensitivity of filtering harmful content:
     * NONE:   There are no content filters applied. All user inputs and FM-generated outputs are allowed.
     * LOW:    The strength of the filter is low. Content classified as harmful with HIGH confidence will be
     *         filtered out. Content classified as harmful with NONE, LOW, or MEDIUM confidence will be allowed.
     * MEDIUM: Content classified as harmful with HIGH and MEDIUM confidence will be filtered out. Content
     *         classified as harmful with NONE or LOW confidence will be allowed.
     * HIGH:   This represents the strictest filtering configuration. Content classified as harmful with HIGH,
     *         MEDIUM and LOW confidence will be filtered out. Content deemed harmless will be allowed.
     */
    private static Consumer<GuardrailContentFilterConfig.Builder> configureContentFilter() {
        return filter -> filter
                .type(HATE).inputStrength(HIGH).outputStrength(HIGH)
                .type(SEXUAL).inputStrength(HIGH).outputStrength(HIGH)
                .type(INSULTS).inputStrength(HIGH).outputStrength(HIGH)
                .type(VIOLENCE).inputStrength(HIGH).outputStrength(HIGH)
                .type(MISCONDUCT).inputStrength(HIGH).outputStrength(HIGH)
                .type(PROMPT_ATTACK).inputStrength(HIGH).outputStrength(NONE);
    }

    /**
     * Predefined filters for sensitive information such as financial or personally identifiable information (PII)
     * in input prompts or model responses.
     *
     * You can configure the following modes of handling filtered information:
     * Block: Sensitive information filter policies can block requests for sensitive information. Examples of such
     *        applications may include general question and answer applications based on public documents. If
     *        sensitive information is detected in the prompt or response, the guardrail blocks the entire content.
     * Mask:  Information filter policies can mask or redact information from model responses. For example,
     *        guardrails will mask PIIs while generating summaries of conversations between users and customer
     *        service agents. If sensitive information is detected in the model response, the guardrail masks it
     *        with an identifier, the sensitive information is masked and replaced with identifier tags (e.g.,
     *        [NAME-1], [NAME-2], [EMAIL-1], etc.).
     */
    private static Consumer<GuardrailPiiEntityConfig.Builder> configureSensitiveInformationFilter() {
        return filter -> filter
                // General information
                .type(AGE).action(ANONYMIZE)
                .type(NAME).action(ANONYMIZE)
                .type(EMAIL).action(ANONYMIZE)
                .type(USERNAME).action(ANONYMIZE)
                .type(PASSWORD).action(BLOCK)
                // etc.

                // Financial information
                .type(CREDIT_DEBIT_CARD_CVV).action(BLOCK)
                .type(CREDIT_DEBIT_CARD_NUMBER).action(BLOCK)
                .type(CREDIT_DEBIT_CARD_EXPIRY).action(BLOCK)
                // etc.

                // IT-related information
                .type(IP_ADDRESS).action(ANONYMIZE)
                .type(MAC_ADDRESS).action(ANONYMIZE)
                .type(AWS_ACCESS_KEY).action(BLOCK)
                .type(AWS_SECRET_KEY).action(BLOCK)
                // etc.

                // Country-specific PII
                .type(CA_HEALTH_NUMBER).action(ANONYMIZE)
                .type(US_SOCIAL_SECURITY_NUMBER).action(ANONYMIZE)
                .type(UK_UNIQUE_TAXPAYER_REFERENCE_NUMBER).action(ANONYMIZE);
        // etc.
    }

    /**
     * You can use a regular expressions to define patterns for a guardrail to recognize and act upon, such as
     * serial numbers, booking IDs etc.
     */
    private static Consumer<GuardrailRegexConfig.Builder> configureRegexFilter() {
        return filter -> filter
                .name("Bitcoin Address")
                .pattern("^[13][a-km-zA-HJ-NP-Z1-9]{25,34}$")
                .action(BLOCK);
    }

    /**
     * Filters profane words. The list of profanities is based on conventional definitions of profanity and is
     * continually updated.
     */
    private static Consumer<GuardrailManagedWordsConfig.Builder> configureProfanityFilter() {
        return filter -> filter
                .type(GuardrailManagedWordsType.PROFANITY);
    }

    /**
     * Filters custom words and phrases of up to three words to a list. You can add up to 10,000 items to the custom
     * word filter.
     */
    private static Consumer<GuardrailWordConfig.Builder> configureCustomWordFilter() {
        return filter -> filter
                .text("RivalInvest")
                .text("Turbo Loan");
    }
}
