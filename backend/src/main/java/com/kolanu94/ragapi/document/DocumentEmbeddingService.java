package com.kolanu94.ragapi.document;

import com.kolanu94.ragapi.openai.OpenAiClient;
import org.springframework.stereotype.Service;

@Service
public class DocumentEmbeddingService {
  private final OpenAiClient openAi;
  private final DocumentVectorStore vectorStore;

  public DocumentEmbeddingService(OpenAiClient openAi, DocumentVectorStore vectorStore) {
    this.openAi = openAi;
    this.vectorStore = vectorStore;
  }

  public void embedAndStore(long docId, String content) {
    var embedding = openAi.embed(content);
    vectorStore.saveEmbedding(docId, embedding);
  }
}