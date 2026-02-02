package com.kolanu94.ragapi;

import com.kolanu94.ragapi.openai.OpenAiProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(OpenAiProperties.class)
public class RagApiApplication {
  public static void main(String[] args) {
    SpringApplication.run(RagApiApplication.class, args);
  }
}