package com.kolanu94.ragapi.document;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;

@Component
public class DocumentVectorStore {
  private final JdbcTemplate jdbc;

  public DocumentVectorStore(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  /** Update the embedding vector for a given document id */
  public void saveEmbedding(long docId, float[] embedding) {
    // Convert float[] -> pgvector literal: [0.1,0.2,...]
    String vec = toPgVectorLiteral(embedding);

    jdbc.update(
        "UPDATE documents SET embedding = ?::vector WHERE id = ?",
        vec, docId
    );
  }

  /** Top-k similarity search using cosine distance (<=>) */
  public List<NeighborDoc> searchSimilar(float[] queryEmbedding, int k) {
    String vec = toPgVectorLiteral(queryEmbedding);

    return jdbc.query("""
        SELECT id, title, content, created_at,
               1 - (embedding <=> ?::vector) AS score
        FROM documents
        WHERE embedding IS NOT NULL
        ORDER BY embedding <=> ?::vector
        LIMIT ?
        """,
        (rs, rowNum) -> new NeighborDoc(
            rs.getLong("id"),
            rs.getString("title"),
            rs.getString("content"),
            rs.getObject("created_at", OffsetDateTime.class),
            rs.getDouble("score")
        ),
        vec, vec, k
    );
  }

  private String toPgVectorLiteral(float[] v) {
    StringBuilder sb = new StringBuilder();
    sb.append('[');
    for (int i = 0; i < v.length; i++) {
      if (i > 0) sb.append(',');
      sb.append(v[i]);
    }
    sb.append(']');
    return sb.toString();
  }

  public record NeighborDoc(
      long id,
      String title,
      String content,
      OffsetDateTime createdAt,
      double score
  ) {}
}