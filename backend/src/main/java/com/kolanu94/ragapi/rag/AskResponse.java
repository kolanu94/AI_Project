package com.kolanu94.ragapi.rag;

import java.util.List;

public record AskResponse(
    String answer,
    List<SourceDoc> sources
) {
  public record SourceDoc(long id, String title, double score) {}
}