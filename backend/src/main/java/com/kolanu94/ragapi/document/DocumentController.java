package com.kolanu94.ragapi.document;

import com.kolanu94.ragapi.document.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/documents")
public class DocumentController {

  private final DocumentRepository repo;

  public DocumentController(DocumentRepository repo) {
    this.repo = repo;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public DocumentDetail create(@RequestBody CreateDocumentRequest req) {
    if (req == null || isBlank(req.title()) || isBlank(req.content())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "title and content are required");
    }

    var e = new DocumentEntity();
    e.setTitle(req.title().trim());
    e.setContent(req.content().trim());

    // ✅ optional
    if (req.source() != null && !req.source().trim().isEmpty()) {
      e.setSource(req.source().trim());
    }

    var saved = repo.save(e);
    return new DocumentDetail(saved.getId(), saved.getTitle(), saved.getContent(), saved.getCreatedAt());
  }

  @GetMapping
  public List<DocumentListItem> list() {
    return repo.findAll().stream()
        .sorted(Comparator.comparing(DocumentEntity::getCreatedAt).reversed()) // ✅ stable ordering
        .map(d -> new DocumentListItem(d.getId(), d.getTitle(), d.getCreatedAt()))
        .toList();
  }

  @GetMapping("/{id}")
  public DocumentDetail get(@PathVariable Long id) {
    var d = repo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Document not found: " + id));
    return new DocumentDetail(d.getId(), d.getTitle(), d.getContent(), d.getCreatedAt());
  }

  private boolean isBlank(String s) {
    return s == null || s.trim().isEmpty();
  }
}