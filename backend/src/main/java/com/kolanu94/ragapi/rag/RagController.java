package com.kolanu94.ragapi.rag;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/documents")
public class RagController {
  private final RagService rag;

  public RagController(RagService rag) {
    this.rag = rag;
  }

  @PostMapping("/ask")
  public AskResponse ask(@RequestBody AskRequest req) {
    if (req == null || req.question() == null || req.question().trim().isEmpty()) {
      throw new IllegalArgumentException("question is required");
    }
    return rag.ask(req.question().trim(), req.topK());
  }
}