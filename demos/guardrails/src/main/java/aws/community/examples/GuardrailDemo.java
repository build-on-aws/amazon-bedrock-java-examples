package aws.community.examples;

import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrock.BedrockClient;
import software.amazon.awssdk.services.bedrock.model.*;
import software.amazon.awssdk.services.bedrock.model.GuardrailTopicType;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.*;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static software.amazon.awssdk.services.bedrock.model.GuardrailContentFilterType.*;
import static software.amazon.awssdk.services.bedrock.model.GuardrailFilterStrength.*;
import static software.amazon.awssdk.services.bedrock.model.GuardrailPiiEntityType.*;
import static software.amazon.awssdk.services.bedrock.model.GuardrailSensitiveInformationAction.*;

/**
 * This example demonstrates the creation and usage of guardrails with Amazon Bedrock.
 *
 * Key benefits demonstrated:
 * 1. Content filtering: Block harmful inputs and responses
 * 2. Topic control: Prevent discussions on specified topics
 * 3. PII protection: Safeguard sensitive information
 * 4. Customization: Apply tailored filters for specific use cases
 *
 * The demo shows how to:
 * 1. Configure various guardrail filters
 * 2. Create and apply a guardrail to a model interaction
 * 3. Compare model responses with and without guardrails
 */
public class GuardrailDemo {

    private static final Region AWS_REGION = Region.US_EAST_1;

    // You can use almost any model on Amazon Bedrock. Simply change the model ID.
    private static final String MODEL_ID = "anthropic.claude-3-haiku-20240307-v1:0";

    private String guardrailId;
    private String guardrailVersion;

    public static void main(String[] args) {
        GuardrailDemo demo = new GuardrailDemo();
        demo.runDemo();
    }

    private void runDemo() {
        configureAndCreateGuardrail();
        demonstrateGuardrailEffect();
    }

    /**
     * Configures filters and creates a guardrail with those configurations.
     */
    private void configureAndCreateGuardrail() {
        // Configure filters
        Consumer<GuardrailContentFilterConfig.Builder> contentFilter = configureContentFilter();
        Consumer<GuardrailPiiEntityConfig.Builder> piiFilter = configurePiiFilter();
        Consumer<GuardrailTopicConfig.Builder> topicFilter = configureTopicFilter();
        Consumer<GuardrailManagedWordsConfig.Builder> profanityFilter = configureProfanityFilter();
        Consumer<GuardrailWordConfig.Builder> customWordFilter = configureCustomWordFilter();
        Consumer<GuardrailRegexConfig.Builder> customRegexFilter = configureCustomRegexFilter();

        // Create guardrail
        createGuardrail(contentFilter, piiFilter, topicFilter, profanityFilter, customWordFilter, customRegexFilter);
    }

    /**
     * Configures content filter to detect and filter harmful content.
     */
    private Consumer<GuardrailContentFilterConfig.Builder> configureContentFilter() {
        return filter -> filter
                .type(HATE).inputStrength(HIGH).outputStrength(HIGH)
                .type(SEXUAL).inputStrength(HIGH).outputStrength(HIGH)
                .type(VIOLENCE).inputStrength(HIGH).outputStrength(HIGH)
                .type(PROMPT_ATTACK).inputStrength(HIGH).outputStrength(NONE);
    }

    /**
     * Configures filter for personally identifiable information (PII).
     */
    private Consumer<GuardrailPiiEntityConfig.Builder> configurePiiFilter() {
        return filter -> filter
                .type(NAME).action(ANONYMIZE)
                .type(EMAIL).action(ANONYMIZE)
                .type(IP_ADDRESS).action(ANONYMIZE)
                .type(UK_NATIONAL_HEALTH_SERVICE_NUMBER).action(ANONYMIZE)
                .type(CREDIT_DEBIT_CARD_NUMBER).action(BLOCK);
    }

    /**
     * Configures filter for denied topics.
     */
    private Consumer<GuardrailTopicConfig.Builder> configureTopicFilter() {

        return topics -> topics
                .name("Investment Advice")
                .definition("Guidance or recommendations regarding the management of funds or assets.")
                .examples(List.of("Is investing in stocks better than bonds?", "Should I invest in gold?"))
                .type(GuardrailTopicType.DENY)

                .name("Cryptocurrencies")
                .definition("Digital currencies that use cryptography for security.")
                .examples(List.of("What's the best cryptocurrency to invest in?", "How do I mine Bitcoin?"))
                .type(GuardrailTopicType.DENY);
    }

    /**
     * Configures filter for profanity using managed word lists.
     */
    private Consumer<GuardrailManagedWordsConfig.Builder> configureProfanityFilter() {
        return filter -> filter.type(GuardrailManagedWordsType.PROFANITY);
    }

    /**
     * Configures filter for custom words or phrases.
     */
    private Consumer<GuardrailWordConfig.Builder> configureCustomWordFilter() {
        return filter -> filter.text("RivalInvest").text("Turbo Loan");
    }

    /**
     * Configures custom regex filter for specific patterns.
     */
    private Consumer<GuardrailRegexConfig.Builder> configureCustomRegexFilter() {
        return filter -> filter
                .name("Bitcoin Address")
                .pattern("^[13][a-km-zA-HJ-NP-Z1-9]{25,34}$")
                .action(BLOCK);
    }

    /**
     * Creates a guardrail with the specified filters.
     */
    private void createGuardrail(Consumer<GuardrailContentFilterConfig.Builder> contentFilter,
                                 Consumer<GuardrailPiiEntityConfig.Builder> piiFilter,
                                 Consumer<GuardrailTopicConfig.Builder> topicFilter,
                                 Consumer<GuardrailManagedWordsConfig.Builder> profanityFilter,
                                 Consumer<GuardrailWordConfig.Builder> customWordFilter,
                                 Consumer<GuardrailRegexConfig.Builder> customRegexFilter) {

        try (BedrockClient bedrock = BedrockClient.builder().region(AWS_REGION).build()) {

            CreateGuardrailResponse response = bedrock.createGuardrail(g -> g
                    .name("Guardrail_" + generateRandomPostfix())

                    .blockedInputMessaging("I'm not able to discuss this topic. Please rephrase your request.")
                    .blockedOutputsMessaging("Response unavailable. Please try a different query.")

                    .topicPolicyConfig(c -> c.topicsConfig(topicFilter))
                    .contentPolicyConfig(c -> c.filtersConfig(contentFilter))
                    .sensitiveInformationPolicyConfig(c -> c.piiEntitiesConfig(piiFilter).regexesConfig(customRegexFilter))
                    .wordPolicyConfig(c -> c.managedWordListsConfig(profanityFilter).wordsConfig(customWordFilter)));

            guardrailId = response.guardrailId();
            guardrailVersion = response.version();

            System.out.println("Guardrail created with ID: " + guardrailId + " and version: " + guardrailVersion);

        } catch (AwsServiceException e) {
            System.out.println("Error creating guardrail: " + e.awsErrorDetails().errorMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Demonstrates the effect of applying a guardrail to model interactions.
     */
    private void demonstrateGuardrailEffect() {
        System.out.println("-".repeat(80));
        String userInput = "I'd like to invest in cryptocurrency.";
        System.out.println("\nUser input: " + userInput);

        System.out.println("\nWithout Guardrail:");
        String responseWithoutGuardrail = converse(userInput, false);
        System.out.println("Response: " + responseWithoutGuardrail);

        System.out.println("\nWith Guardrail:");
        String responseWithGuardrail = converse(userInput, true);
        System.out.println("Response: " + responseWithGuardrail);
        System.out.println("-".repeat(80));
    }

    /**
     * Sends a conversation request to the model with or without a guardrail.
     */
    private String converse(String userInput, boolean withGuardrail) {
        try (BedrockRuntimeClient runtime = BedrockRuntimeClient.builder().region(AWS_REGION).build()) {

            ConverseRequest.Builder requestBuilder = ConverseRequest.builder()
                    .modelId(MODEL_ID)
                    .messages(msg -> msg.role(ConversationRole.USER).content(ContentBlock.fromText(userInput)))
                    .system(SystemContentBlock.fromText(
                            "You are a friendly customer service chatbot for a bank. Answer the customer's questions " +
                                    "about our products and services with one to two-sentence responses."));

            if (withGuardrail) {
                requestBuilder.guardrailConfig(config -> config
                        .guardrailIdentifier(guardrailId)
                        .guardrailVersion(guardrailVersion));
            }

            ConverseResponse response = runtime.converse(requestBuilder.build());
            return response.output().message().content().getFirst().text();

        } catch (AwsServiceException e) {
            System.out.println("Error in conversation: " + e.awsErrorDetails().errorMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Generates a random 8-character string for unique guardrail naming.
     */
    private static String generateRandomPostfix() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8);
    }
}