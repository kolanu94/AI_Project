package com.kolanu94.ragapi.openai;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "openai")
public class OpenAiProperties {
  /**
   * Loaded from env: OPENAI_API_KEY
   */
  private String apiKey;

  /**
   * env: OPENAI_CHAT_MODEL (default set in yaml below)
   */
  private String chatModel = "gpt-4o-mini";

  /**
   * env: OPENAI_EMBED_MODEL
   */
  private String embedModel = "text-embedding-3-small";

  public String getApiKey() { return apiKey; }
  public void setApiKey(String apiKey) { this.apiKey = apiKey; }

  public String getChatModel() { return chatModel; }
  public void setChatModel(String chatModel) { this.chatModel = chatModel; }

  public String getEmbedModel() { return embedModel; }
  public void setEmbedModel(String embedModel) { this.embedModel = embedModel; }
}