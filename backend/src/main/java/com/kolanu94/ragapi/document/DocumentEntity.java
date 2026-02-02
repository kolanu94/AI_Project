package com.kolanu94.ragapi.document;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "documents")
public class DocumentEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 200)
  private String title;

  @Column(nullable = false, columnDefinition = "text")
  private String content;

  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt;

  @PrePersist
  void onCreate() {
    if (createdAt == null) createdAt = OffsetDateTime.now();
  }

  public Long getId() { return id; }
  public String getTitle() { return title; }
  public String getContent() { return content; }
  public OffsetDateTime getCreatedAt() { return createdAt; }

  public void setTitle(String title) { this.title = title; }
  public void setContent(String content) { this.content = content; }
}