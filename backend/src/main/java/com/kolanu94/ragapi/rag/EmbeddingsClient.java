package com.kolanu94.ragapi.rag;

public interface EmbeddingsClient {
  float[] embed(String text);
}