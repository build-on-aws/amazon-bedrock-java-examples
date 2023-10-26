package org.acme;

public class BedrockResult {

    private int inputTextTokenCount;
    private BedrockResultItem[] results;

    public int getInputTextTokenCount() {
        return inputTextTokenCount;
    }

    public void setInputTextTokenCount(int inputTextTokenCount) {
        this.inputTextTokenCount = inputTextTokenCount;
    }

    public BedrockResultItem[] getResults() {
        return results;
    }

    public void setResults(BedrockResultItem[] resultItems) {
        this.results = resultItems;
    }
    
}

class BedrockResultItem {

    private int tokenCount;
    private String outputText;
    private String completionReason;

    public String getCompletionReason() {
        return completionReason;
    }

    public void setCompletionReason(String completionReason) {
        this.completionReason = completionReason;
    }

    public void setTokenCount(int tokenCount) {
        this.tokenCount = tokenCount;
    }

    public void setOutputText(String outputText) {
        this.outputText = outputText;
    }

    public int getTokenCount() {

        return tokenCount;
    }

    public String getOutputText() {
        return outputText;
    }

}
