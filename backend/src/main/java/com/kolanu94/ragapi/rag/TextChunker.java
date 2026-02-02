package com.kolanu94.ragapi.rag;

import java.util.ArrayList;
import java.util.List;

public class TextChunker {
  private final int chunkSize;
  private final int overlap;

  public TextChunker(int chunkSize, int overlap) {
    this.chunkSize = chunkSize;
    this.overlap = overlap;
  }

  public List<String> chunk(String text) {
    if (text == null) return List.of();
    text = text.trim();
    if (text.isEmpty()) return List.of();

    List<String> out = new ArrayList<>();
    int start = 0;

    while (start < text.length()) {
      int end = Math.min(start + chunkSize, text.length());
      // try to cut nicely at newline/period if possible
      int niceEnd = findNiceEnd(text, start, end);
      String chunk = text.substring(start, niceEnd).trim();
      if (!chunk.isEmpty()) out.add(chunk);

      if (niceEnd >= text.length()) break;
      start = Math.max(0, niceEnd - overlap);
    }
    return out;
  }

  private int findNiceEnd(String s, int start, int end) {
    int scanFrom = Math.max(start, end - 150);
    for (int i = end; i > scanFrom; i--) {
      char c = s.charAt(i - 1);
      if (c == '\n' || c == '.' || c == '!' || c == '?') return i;
    }
    return end;
  }
}