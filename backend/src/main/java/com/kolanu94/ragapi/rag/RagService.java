package com.kolanu94.ragapi.rag;

import com.kolanu94.ragapi.document.DocumentVectorStore;
import com.kolanu94.ragapi.openai.OpenAiClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RagService {
  private final OpenAiClient openAi;
  private final DocumentVectorStore vectorStore;

  public RagService(OpenAiClient openAi, DocumentVectorStore vectorStore) {
    this.openAi = openAi;
    this.vectorStore = vectorStore;
  }

  public AskResponse ask(String question, Integer topK) {
    int k = (topK == null || topK < 1 || topK > 10) ? 3 : topK;

    var qVec = openAi.embed(question);
    var neighbors = vectorStore.searchSimilar(qVec, k);

    String context = buildContext(neighbors);
    String answer = openAi.answerWithContext(question, context);

    var sources = neighbors.stream()
        .map(d -> new AskResponse.SourceDoc(d.id(), d.title(), d.score()))
        .toList();

    return new AskResponse(answer, sources);
  }

  private String buildContext(List<DocumentVectorStore.NeighborDoc> docs) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < docs.size(); i++) {
      var d = docs.get(i);
      sb.append("Document ").append(i + 1).append(": ").append(d.title()).append("\n");
      sb.append(d.content()).append("\n\n");
    }
    return sb.toString();
  }
}