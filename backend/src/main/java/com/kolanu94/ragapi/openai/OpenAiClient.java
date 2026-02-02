package com.kolanu94.ragapi.openai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Component
public class OpenAiClient {
  private final OpenAiProperties props;
  private final ObjectMapper om;
  private final HttpClient http;

  public OpenAiClient(OpenAiProperties props, ObjectMapper om) {
    this.props = props;
    this.om = om;
    this.http = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(10))
        .build();
  }

  public float[] embed(String text) {
    requireKey();

    try {
      var body = om.createObjectNode();
      body.put("model", props.getEmbedModel());
      body.put("input", text);

      var req = HttpRequest.newBuilder()
          .uri(URI.create("https://api.openai.com/v1/embeddings"))
          .timeout(Duration.ofSeconds(30))
          .header("Authorization", "Bearer " + props.getApiKey())
          .header("Content-Type", "application/json")
          .POST(HttpRequest.BodyPublishers.ofString(body.toString(), StandardCharsets.UTF_8))
          .build();

      var res = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
      if (res.statusCode() / 100 != 2) {
        throw new RuntimeException("OpenAI embeddings failed: " + res.statusCode() + " " + res.body());
      }

      JsonNode root = om.readTree(res.body());
      JsonNode arr = root.get("data").get(0).get("embedding");

      float[] vec = new float[arr.size()];
      for (int i = 0; i < arr.size(); i++) {
        vec[i] = (float) arr.get(i).asDouble();
      }
      return vec;

    } catch (Exception e) {
      throw new RuntimeException("OpenAI embed() error: " + e.getMessage(), e);
    }
  }

  public String answerWithContext(String question, String context) {
    requireKey();

    try {
      var body = om.createObjectNode();
      body.put("model", props.getChatModel());

      var messages = om.createArrayNode();

      var sys = om.createObjectNode();
      sys.put("role", "system");
      sys.put("content",
          "You are a helpful assistant. Answer using ONLY the provided context. " +
          "If the answer is not in the context, say you don't know."
      );
      messages.add(sys);

      var user = om.createObjectNode();
      user.put("role", "user");
      user.put("content",
          "Context:\n" + context + "\n\nQuestion:\n" + question
      );
      messages.add(user);

      body.set("messages", messages);
      body.put("temperature", 0.2);

      var req = HttpRequest.newBuilder()
          .uri(URI.create("https://api.openai.com/v1/chat/completions"))
          .timeout(Duration.ofSeconds(60))
          .header("Authorization", "Bearer " + props.getApiKey())
          .header("Content-Type", "application/json")
          .POST(HttpRequest.BodyPublishers.ofString(body.toString(), StandardCharsets.UTF_8))
          .build();

      var res = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
      if (res.statusCode() / 100 != 2) {
        throw new RuntimeException("OpenAI chat failed: " + res.statusCode() + " " + res.body());
      }

      JsonNode root = om.readTree(res.body());
      return root.get("choices").get(0).get("message").get("content").asText().trim();

    } catch (Exception e) {
      throw new RuntimeException("OpenAI answer() error: " + e.getMessage(), e);
    }
  }

  private void requireKey() {
    if (props.getApiKey() == null || props.getApiKey().isBlank()) {
      throw new IllegalStateException("OPENAI_API_KEY is missing. Set it in docker compose env.");
    }
  }
}